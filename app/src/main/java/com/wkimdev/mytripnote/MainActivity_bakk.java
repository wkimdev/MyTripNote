package com.wkimdev.mytripnote;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.sdk.user.UserApiClient;
import com.wkimdev.mytripnote.adapter.HomeTravelListAdapter;
import com.wkimdev.mytripnote.adapter.WeatherViewPagerAdapter;
import com.wkimdev.mytripnote.config.GlobalApplication;
import com.wkimdev.mytripnote.config.PreferenceManager;
import com.wkimdev.mytripnote.model.NoteItem;
import com.wkimdev.mytripnote.model.ReservationItem;
import com.wkimdev.mytripnote.model.SearchData;
import com.wkimdev.mytripnote.model.TravelItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

//  로그인 이후 들어오는 홈 화면
public class MainActivity_bakk extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "MainActivity";

    // 드로어메뉴의 개인정보 노출 뷰
    private TextView tv_nickName;
    private TextView tv_email;
    private ImageView iv_profile;
    private Button btn_logout;

    // 여행 노트 추가 버튼
    private CircleImageView btn_add_trip;

    // 구글 API 클라이언트 객체
    private GoogleSignInClient mGoogleSignInClient;

    // SNS별 로그인여부 구분값
    private boolean isGoogleLogin;
    private boolean isKakaoLogin;


    // 날씨 자동변경 스레드 관련 필드 선언
    private ViewPager viewPager_weather;
    private WeatherViewPagerAdapter pagerAdapter;
    private int weatherCurrentPage;
    private Timer timer;
    private static final int DELAY_WEATHER = 0; //날씨 슬라이딩 스레드시작 딜레이 시간
    private static final int PERIOD_WEATHER = 3000; //날씨 슬라이딩 시간 간격
    private Handler weatherHandler; // 날씨 API를 호출하기 위한 핸들러 등록
    private ArrayList<String[]> weatherArray;
    private Bitmap bitmap;


    // Firebase 연동을 위한 선언
    public static final FirebaseDatabase database;
    public static DatabaseReference databaseReference;
    static {
        database = FirebaseDatabase.getInstance();
    }

    // 홈 여행 리스트리사이클러뷰
    private RecyclerView rv_home_travel_list;
    private LinearLayoutManager linearLayoutManager;
    private HomeTravelListAdapter homeTrableListadatper;


    /**
     * 앱 실행시 필요한 기능들 초기화 및 연동 선언
     *  - 뷰 선언
     *  - Firebase DB 연결
     *  - Firebase DB로 부터 홈 여행 리스트 데이터 조회
     *  - viewPager 등록
     *  - 핸들러 객체 선언
     *  - 인텐트 요청을 받을 수 있도록 객체 선언
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 로그인이 되어 있으면, 로그인화면을 띄우지 않는 처리
        if(!PreferenceManager.getBoolean(MainActivity_bakk.this, "isLogin")){
            startActivity(new Intent(this, LoginActivity.class));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate: ENTER!!");

        tv_nickName = findViewById(R.id.tv_nickName);
        iv_profile = findViewById(R.id.iv_profile);
        btn_logout = findViewById(R.id.btn_logout);
        tv_email = findViewById(R.id.tv_email);
        btn_add_trip = findViewById(R.id.btn_add_trip);
        viewPager_weather = findViewById(R.id.viewPager_weather);
        rv_home_travel_list = findViewById(R.id.rv_home_travel_list);
        linearLayoutManager = new LinearLayoutManager(this);
        // 홈 여행 리스트에 담을 여행데이터 리스트
        ArrayList<TravelItem> travelItems = new ArrayList<>();


        // 단말별 유니크ID 생성
        String userAndroidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        PreferenceManager.setString(this, "userAndroidId", userAndroidId);

        // Firebase DB 테이블 연결
        String databaseRefPath = "Trip/" + userAndroidId;
        databaseReference = database.getReference(databaseRefPath);


        // 구글 로그아웃 처리를 위해 GoogleSignInOptions 객체 생성
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // GoogleSignInOptions에 지정된 옵션으로, GoogleSignInClient 객체 구성
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 이전에 로그인을 했으면 사용자명 셋팅
        // TODO - 통일 할 방법 고안
        if (PreferenceManager.getBoolean(MainActivity_bakk.this, "isLogin")) {
            Log.e(TAG, "로그인 값 저장상태 확인 " + PreferenceManager.getString(this, "nickName"));
            Glide.with(this).load(PreferenceManager.getString(this, "photoUrl")).into(iv_profile);
            tv_nickName.setText(PreferenceManager.getString(this, "nickName") + " 님");
            tv_email.setText(PreferenceManager.getString(this, "email"));
        }


        // 로그인 화면으로 부터 결과값을 인텐트로 받는 처리
        // TODO - 한개로 통일해서 전달해야 함
        Intent intent = getIntent();
        String googleNickName = intent.getStringExtra("nickName");
        String googlePhotoUrl = intent.getStringExtra("photoUrl");
        String googleEmail = intent.getStringExtra("googleEmail");
        String kakaoNickName = intent.getStringExtra("kakaoNickName");
        String kakaoPhotoUrl = intent.getStringExtra("kakaoPhoto");
        String kakaoEmail = intent.getStringExtra("kakaoEmail");

        if (!TextUtils.isEmpty(googleNickName)) {
            Glide.with(this).load(googlePhotoUrl).into(iv_profile);
            tv_nickName.setText(googleNickName + " 님");
            tv_email.setText(googleEmail);
            isGoogleLogin = true;
            PreferenceManager.setString(this, "nickName", googleNickName);
            PreferenceManager.setString(this, "email", googleEmail);
            PreferenceManager.setString(this, "photoUrl", googlePhotoUrl);
        } else if (!TextUtils.isEmpty(kakaoNickName)) {
            Glide.with(this).load(kakaoPhotoUrl).into(iv_profile);
            tv_nickName.setText(kakaoNickName+ " 님");
            tv_email.setText(kakaoEmail);
            isKakaoLogin = true;
            PreferenceManager.setString(this, "nickName", kakaoNickName);
            PreferenceManager.setString(this, "email", kakaoEmail);
            PreferenceManager.setString(this, "photoUrl", kakaoPhotoUrl);
        }



        // firease realtime database에서 여행 목록 데이터 조회
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren() ) {

                    TravelItem travelItem = new TravelItem();
                    travelItem.setTravelId(snapshot.getKey()); //travelId

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String key = snapshot1.getKey();
                        if ("travelTitle".equals(key)) {
                            travelItem.setTravelTitle(snapshot1.getValue().toString());
                        } else if ("travelDate".equals(key)) {
                            travelItem.setTravelDate(snapshot1.getValue().toString());
                        } else if ("destination".equals(key)) {
                            travelItem.setDestination(snapshot1.getValue().toString());
                        } else if ("travelType".equals(key)) {
                            travelItem.setTravelType(snapshot1.getValue().toString());
                        }
                    }
                    travelItems.add(travelItem);
                }

                homeTrableListadatper = new HomeTravelListAdapter(travelItems, MainActivity_bakk.this, MainActivity_bakk.this);
                rv_home_travel_list.setLayoutManager(linearLayoutManager);
                rv_home_travel_list.setAdapter(homeTrableListadatper);
                homeTrableListadatper.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, String.valueOf(databaseError.toException()));
            }
        });

    }

    /**
     * - 쓰레드 시작 메소드 호출
     * - 버튼 이벤트 등록
     * */
    @Override
    protected void onResume() {
        super.onResume();

        // 날씨 API 호출 스레드
        ChangeWeatherByCountry thread = new ChangeWeatherByCountry();
        thread.start();

        // 날씨영역 자동변경 처리 쓰레드
        weatherHandler = new Handler();

        // 날씨영역 자동변경 타이머 스레드
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                weatherHandler.post(new ChangeAdvertiseImageThread());
            }
        }, DELAY_WEATHER, PERIOD_WEATHER);


        // 드로어 메뉴의 로그아웃 실행 코드
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isGoogleLogin) { // 구글 로그인의 경우 로그아웃 처리
                    Log.e(TAG, "onClick: 로그아웃 버튼 클릭!!!!! ");
                    PreferenceManager.setBoolean(MainActivity_bakk.this, "isLogin", false);
                    mGoogleSignInClient.signOut()
                            .addOnCompleteListener(MainActivity_bakk.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.e(TAG, "구글 로그아웃 성공 !");
                                    Toast.makeText(MainActivity_bakk.this, "로그아웃을 했습니다!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity_bakk.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                } else if (isKakaoLogin) { // 카카오 로그인의 경우 로그아웃 처리
                    PreferenceManager.setBoolean(MainActivity_bakk.this, "isLogin", false);
                    UserApiClient.getInstance().logout(error -> {
                        if (error != null) {
                            Log.e(TAG, "onClick: 로그아웃 실패, SDK에서 토큰 삭제 됨!", error);
                        } else {
                            Log.e(TAG, "카카오 로그아웃 성공, SDK에서 토큰 삭제 됨!");
                            Toast.makeText(MainActivity_bakk.this, "로그아웃을 했습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity_bakk.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        return null;
                    });
                }
            }
        });


        // 여행 추가 버튼 클릭
        btn_add_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity_bakk.this, AddTripPopupActivity.class));
            }
        });
    }

    // Handler에게 요청을 받고, 3초마다 광고 이미지를 변경하는 스레드
    private class ChangeAdvertiseImageThread implements Runnable {
        @Override
        public void run() {
            if(weatherCurrentPage == 6) {
                weatherCurrentPage = 0;
            }
            viewPager_weather.setCurrentItem(weatherCurrentPage++, true);
        }
    }

    // 메인스레드에서 곧바로 네트워크 접속 시도를 할 수 없기 때문에, 쓰레드를 통해 날씨 API를 요청
    class ChangeWeatherByCountry extends Thread {
        @Override
        public void run() {
            weatherArray = initWeatherAPI();
            initViewPager.sendMessage(new Message());
        }
    }

    // viewpager로부터 요청을 받아 viewPager어뎁터를 실행하는 핸들러
    Handler initViewPager = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            pagerAdapter = new WeatherViewPagerAdapter(MainActivity_bakk.this, weatherArray);
            viewPager_weather.setAdapter(pagerAdapter);
        }
    };

    // 홈 여행 목록 클릭 후, 여행노트 화면으로 이동
    @Override
    public void onClickTravelList(String travelId) {
        Intent intent = new Intent(this, TripNoteActivity.class);
        PreferenceManager.setString(this, "currentTravelId", travelId);
        startActivity(intent);
    }

    @Override
    public void onClickYoutubeList(SearchData searchData) {

    }

    @Override
    public void onClickReservationList(ReservationItem reservationItem) {

    }

    @Override
    public void onClickNoteList(NoteItem noteItem) {

    }

    // 날씨API를 호출해 6국가의 날씨 정보를 리턴하는 메소드
    public ArrayList<String[]> initWeatherAPI() {

        URL url; //URL 주소 객체
        URLConnection connection; //URL접속을 가지는 객체
        URLConnection connection2; //URL접속을 가지는 객체2
        InputStream is; //URL접속에서 내용을 읽기위한 Stream
        InputStream is2; //URL접속에서 내용을 읽기위한 Stream
        InputStreamReader isr;
        BufferedReader br;
        String[] cities = {"Seoul", "Paris", "London", "Tokyo", "Sydney", "Chicago"};
        ArrayList<String[]> weatherArray = new ArrayList<>();

        try{
            for (int i = 0; i < cities.length; i++) {

                String[] result = new String[7];

                //URL객체를 생성하고 해당 URL로 접속한다
                url = new URL("https://api.openweathermap.org/data/2.5/weather?q="
                        + cities[i] +"&appid=" + GlobalApplication.WEATHER_API_KEY);

                connection = url.openConnection();

                //내용을 읽어오기위한 InputStream객체를 생성
                is = connection.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);

                String jsonText = readAll(br);
                JSONObject json = new JSONObject(jsonText);

                try {
                    JSONObject obj = json;
                    // 날씨 설명
                    result[0] = obj.getJSONArray("weather").getJSONObject(0).get("description").toString();
                    // 온도 - API 출력결과에서 temp:296.48 켈빈을 섭씨로 변환하기 위해 273.15 를 뺀 결과를 출력
                    double temp = (Double) obj.getJSONObject("main").get("temp");
                    double celsius = temp - 273.15;
                    result[1] = String.valueOf(Math.floor(celsius));
                    result[2] = cities[i]; // 도시명
                    result[3] = obj.getJSONObject("main").get("humidity").toString(); // 도시명
                    result[4] = obj.getJSONObject("wind").get("speed").toString(); // 바람
                    result[5] = obj.getJSONObject("clouds").get("all").toString(); // 구름
                    String weatherIcon = obj.getJSONArray("weather").getJSONObject(0).get("icon").toString(); // 아이콘

                    // 아이콘 이미지 URL을 가져오기 위한 작업
                    URL url2 = new URL("https://openweathermap.org/img/w/" + weatherIcon + ".png");
                    connection2 = url2.openConnection();
                    is2 = connection2.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is2); // Bitmap으로 반환
                    result[6] = getBase64String(bitmap);

                    weatherArray.add(result);
                } catch (Exception e) {
                    Log.e(TAG, "openweathermap error ( api ? ) : ", e);
                }
            }

        }catch(MalformedURLException mue){
            System.err.println("잘못된 URL입니다.");
        }catch(IOException ioe){
            System.err.println("IOException " + ioe);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weatherArray;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    // Bitmap타입을 String 타입변환
    public String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    // String타입을 Bitmap타입으로 변환
    public static Bitmap getStringToBitmap(String stringBitmap) {
        byte[] decodedByteArray = Base64.decode(stringBitmap, Base64.NO_WRAP);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        return decodedBitmap;
    }

    // 유티크 키값 생성
    public static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


}