package com.wkimdev.mytripnote

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.*
import com.kakao.sdk.user.UserApiClient
import com.wkimdev.mytripnote.adapter.HomeTravelListAdapter
import com.wkimdev.mytripnote.adapter.WeatherViewPagerAdapter
import com.wkimdev.mytripnote.config.GlobalApplication
import com.wkimdev.mytripnote.config.PreferenceManager
import com.wkimdev.mytripnote.model.NoteItem
import com.wkimdev.mytripnote.model.ReservationItem
import com.wkimdev.mytripnote.model.SearchData
import com.wkimdev.mytripnote.model.TravelItem
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.*


//  로그인 이후 들어오는 홈 화면
class MainActivity : AppCompatActivity(), OnClickListener {
    // 드로어메뉴의 개인정보 노출 뷰
    private var tv_nickName: TextView? = null
    private var tv_email: TextView? = null
    private var iv_profile: ImageView? = null
    private var btn_logout: Button? = null

    // 여행 노트 추가 버튼
    private var btn_add_trip: CircleImageView? = null

    // 구글 API 클라이언트 객체
    private var mGoogleSignInClient: GoogleSignInClient? = null

    // SNS별 로그인여부 구분값
    private var isGoogleLogin = false
    private var isKakaoLogin = false

    // 날씨 자동변경 스레드 관련 필드 선언
    private var viewPager_weather: ViewPager? = null
    private var pagerAdapter: WeatherViewPagerAdapter? = null
    private var weatherCurrentPage = 0
    private var timer: Timer? = null
    private var weatherHandler // 날씨 API를 호출하기 위한 핸들러 등록
            : Handler? = null
    private var weatherArray: ArrayList<Array<String?>>? = null
    private var bitmap: Bitmap? = null

    // 홈 여행 리스트리사이클러뷰
    private var rv_home_travel_list: RecyclerView? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var homeTrableListadatper: HomeTravelListAdapter? = null

    /**
     * 앱 실행시 필요한 기능들 초기화 및 연동 선언
     * - 뷰 선언
     * - Firebase DB 연결
     * - Firebase DB로 부터 홈 여행 리스트 데이터 조회
     * - viewPager 등록
     * - 핸들러 객체 선언
     * - 인텐트 요청을 받을 수 있도록 객체 선언
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        // 로그인이 되어 있으면, 로그인화면을 띄우지 않는 처리
        if (!PreferenceManager.getBoolean(this@MainActivity, "isLogin")) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e(TAG, "onCreate: ENTER!!")
        tv_nickName = findViewById(R.id.tv_nickName)
        iv_profile = findViewById(R.id.iv_profile)
        btn_logout = findViewById(R.id.btn_logout)
        tv_email = findViewById(R.id.tv_email)
        btn_add_trip = findViewById(R.id.btn_add_trip)
        viewPager_weather = findViewById(R.id.viewPager_weather)
        rv_home_travel_list = findViewById(R.id.rv_home_travel_list)
        linearLayoutManager = LinearLayoutManager(this)
        // 홈 여행 리스트에 담을 여행데이터 리스트
        val travelItems = ArrayList<TravelItem>()


        // 단말별 유니크ID 생성
        val userAndroidId =
            Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        PreferenceManager.setString(this, "userAndroidId", userAndroidId)

        // Firebase DB 테이블 연결
        val databaseRefPath = "Trip/$userAndroidId"
        databaseReference = database!!.getReference(databaseRefPath)


        // 구글 로그아웃 처리를 위해 GoogleSignInOptions 객체 생성
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // GoogleSignInOptions에 지정된 옵션으로, GoogleSignInClient 객체 구성
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // 이전에 로그인을 했으면 사용자명 셋팅
        // TODO - 통일 할 방법 고안
        if (PreferenceManager.getBoolean(this@MainActivity, "isLogin")) {
            Log.e(TAG, "로그인 값 저장상태 확인 " + PreferenceManager.getString(this, "nickName"))

            /*val profileIv: ImageView = iv_profile ?: return // if null, return early
            Glide.with(this).load(PreferenceManager.getString(this, "photoUrl")).into(profileIv)

            // Use a local variable to safely cast the TextView
            val nicknameTextView: TextView = tv_nickName ?: return // if null, return early
            nicknameTextView.text = PreferenceManager.getString(this, "nickName") + " 님"

            // Assuming tv_email is also a nullable TextView, do the same thing
            val emailTextView: TextView = tv_email ?: return // if null, return early
            emailTextView.text = PreferenceManager.getString(this, "email")*/
            setProfileInfo()

        }


        // 로그인 화면으로 부터 결과값을 인텐트로 받는 처리
        // TODO - 한개로 통일해서 전달해야 함
        val intent = intent
        val googleNickName = intent.getStringExtra("nickName")
        val googlePhotoUrl = intent.getStringExtra("photoUrl")
        val googleEmail = intent.getStringExtra("googleEmail")
        val kakaoNickName = intent.getStringExtra("kakaoNickName")
        val kakaoPhotoUrl = intent.getStringExtra("kakaoPhoto")
        val kakaoEmail = intent.getStringExtra("kakaoEmail")

        if (!TextUtils.isEmpty(googleNickName)) {
            /*val profileIv: ImageView = iv_profile ?: return // if null, return early
            Glide.with(this).load(googlePhotoUrl).into(profileIv)

            // Use a local variable to safely cast the TextView
            val nicknameTextView: TextView = tv_nickName ?: return // if null, return early
            nicknameTextView.setText("$googleNickName 님")

            // Assuming tv_email is also a nullable TextView, do the same thing
            val emailTextView: TextView = tv_email ?: return // if null, return early
            emailTextView.setText(googleEmail)*/
            setProfileInfo()

            isGoogleLogin = true
            PreferenceManager.setString(this, "nickName", googleNickName)
            PreferenceManager.setString(this, "email", googleEmail)
            PreferenceManager.setString(this, "photoUrl", googlePhotoUrl)

        } else if (!TextUtils.isEmpty(kakaoNickName)) {
            /** Glide.with(this).load(kakaoPhotoUrl).into(iv_profile)
            tv_nickName.setText("$kakaoNickName 님")
            tv_email.setText(kakaoEmail)*/
            setProfileInfo()

            isKakaoLogin = true
            PreferenceManager.setString(this, "nickName", kakaoNickName)
            PreferenceManager.setString(this, "email", kakaoEmail)
            PreferenceManager.setString(this, "photoUrl", kakaoPhotoUrl)
        }


        // firease realtime database에서 여행 목록 데이터 조회
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val travelItem = TravelItem()
                    travelItem.travelId = snapshot.key //travelId
                    for (snapshot1 in snapshot.children) {
                        val key = snapshot1.key
                        if ("travelTitle" == key) {
                            travelItem.travelTitle = snapshot1.value.toString()
                        } else if ("travelDate" == key) {
                            travelItem.travelDate = snapshot1.value.toString()
                        } else if ("destination" == key) {
                            travelItem.destination = snapshot1.value.toString()
                        } else if ("travelType" == key) {
                            travelItem.travelType = snapshot1.value.toString()
                        }
                    }
                    travelItems.add(travelItem)
                }
                homeTrableListadatper =
                    HomeTravelListAdapter(travelItems, this@MainActivity, this@MainActivity)

                val homeTravelList: RecyclerView = rv_home_travel_list ?: return
                homeTravelList.layoutManager = linearLayoutManager
                homeTravelList.setAdapter(homeTrableListadatper)

                homeTrableListadatper!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, databaseError.toException().toString())
            }
        })
    }


    private fun setProfileInfo() {
        setImageViewFromUrl(iv_profile, PreferenceManager.getString(this, "photoUrl"))
        setTextViewWithSuffix(tv_nickName, " 님", PreferenceManager.getString(this, "nickName"))
        setTextView(tv_email, PreferenceManager.getString(this, "email"))
    }

    private fun setImageViewFromUrl(imageView: ImageView?, url: String?) {
        imageView ?: return
        Glide.with(this).load(url).into(imageView)
    }

    private fun setTextViewWithSuffix(textView: TextView?, suffix: String, text: String?) {
        textView ?: return
        textView.text = "$text$suffix"
    }

    private fun setTextView(textView: TextView?, text: String?) {
        textView ?: return
        textView.text = "$text"
    }



    /**
     * - 쓰레드 시작 메소드 호출
     * - 버튼 이벤트 등록
     */
    override fun onResume() {
        super.onResume()

        // 날씨 API 호출 스레드
        val thread: ChangeWeatherByCountry = ChangeWeatherByCountry()
        thread.start()

        // 날씨영역 자동변경 처리 쓰레드
        weatherHandler = Handler()

        // 날씨영역 자동변경 타이머 스레드
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                weatherHandler!!.post(ChangeAdvertiseImageThread())
            }
        }, DELAY_WEATHER.toLong(), PERIOD_WEATHER.toLong())


        // 드로어 메뉴의 로그아웃 실행 코드
        btn_logout!!.setOnClickListener {
            if (isGoogleLogin) { // 구글 로그인의 경우 로그아웃 처리
                Log.e(TAG, "onClick: 로그아웃 버튼 클릭!!!!! ")
                PreferenceManager.setBoolean(this@MainActivity, "isLogin", false)
                mGoogleSignInClient!!.signOut()
                    .addOnCompleteListener(this@MainActivity) {
                        Log.e(TAG, "구글 로그아웃 성공 !")
                        Toast.makeText(this@MainActivity, "로그아웃을 했습니다!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
            } else if (isKakaoLogin) { // 카카오 로그인의 경우 로그아웃 처리
                PreferenceManager.setBoolean(this@MainActivity, "isLogin", false)

                // 로그아웃
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                    }
                    else {
                        Log.e(TAG, "카카오 로그아웃 성공, SDK에서 토큰 삭제 됨!")
                        Toast.makeText(this@MainActivity, "로그아웃을 했습니다!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                }

            }
        }


        // 여행 추가 버튼 클릭
        btn_add_trip!!.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    AddTripPopupActivity::class.java
                )
            )
        }
    }

    // Handler에게 요청을 받고, 3초마다 광고 이미지를 변경하는 스레드
    private inner class ChangeAdvertiseImageThread : Runnable {
        override fun run() {
            if (weatherCurrentPage == 6) {
                weatherCurrentPage = 0
            }
            viewPager_weather!!.setCurrentItem(weatherCurrentPage++, true)
        }
    }

    // 메인스레드에서 곧바로 네트워크 접속 시도를 할 수 없기 때문에, 쓰레드를 통해 날씨 API를 요청
    internal inner class ChangeWeatherByCountry : Thread() {
        override fun run() {
            weatherArray = initWeatherAPI()
            initViewPager.sendMessage(Message())
        }
    }

    // viewpager로부터 요청을 받아 viewPager어뎁터를 실행하는 핸들러
    var initViewPager: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            pagerAdapter = WeatherViewPagerAdapter(this@MainActivity, weatherArray)
            viewPager_weather!!.adapter = pagerAdapter
        }
    }

    // 홈 여행 목록 클릭 후, 여행노트 화면으로 이동
    override fun onClickTravelList(travelId: String) {
        val intent = Intent(this, TripNoteActivity::class.java)
        PreferenceManager.setString(this, "currentTravelId", travelId)
        startActivity(intent)
    }

    override fun onClickYoutubeList(searchData: SearchData) {}
    override fun onClickReservationList(reservationItem: ReservationItem) {}
    override fun onClickNoteList(noteItem: NoteItem) {}

    // 날씨API를 호출해 6국가의 날씨 정보를 리턴하는 메소드
    fun initWeatherAPI(): ArrayList<Array<String?>> {
        var url: URL //URL 주소 객체
        var connection: URLConnection //URL접속을 가지는 객체
        var connection2: URLConnection //URL접속을 가지는 객체2
        var `is`: InputStream? //URL접속에서 내용을 읽기위한 Stream
        var is2: InputStream? //URL접속에서 내용을 읽기위한 Stream
        var isr: InputStreamReader
        var br: BufferedReader
        val cities = arrayOf("Seoul", "Paris", "London", "Tokyo", "Sydney", "Chicago")
        val weatherArray = ArrayList<Array<String?>>()
        try {
            for (i in cities.indices) {
                val result = arrayOfNulls<String>(7)

                //URL객체를 생성하고 해당 URL로 접속한다
                url = URL(
                    "https://api.openweathermap.org/data/2.5/weather?q="
                            + cities[i] + "&appid=" + GlobalApplication.WEATHER_API_KEY
                )
                connection = url.openConnection()

                //내용을 읽어오기위한 InputStream객체를 생성
                `is` = connection.getInputStream()
                isr = InputStreamReader(`is`)
                br = BufferedReader(isr)
                val jsonText = readAll(br)
                val json = JSONObject(jsonText)
                try {
                    // 날씨 설명
                    result[0] =
                        json.getJSONArray("weather").getJSONObject(0)["description"].toString()
                    // 온도 - API 출력결과에서 temp:296.48 켈빈을 섭씨로 변환하기 위해 273.15 를 뺀 결과를 출력
                    val temp = json.getJSONObject("main")["temp"] as Double
                    val celsius = temp - 273.15
                    result[1] = Math.floor(celsius).toString()
                    result[2] = cities[i] // 도시명
                    result[3] = json.getJSONObject("main")["humidity"].toString() // 도시명
                    result[4] = json.getJSONObject("wind")["speed"].toString() // 바람
                    result[5] = json.getJSONObject("clouds")["all"].toString() // 구름
                    val weatherIcon =
                        json.getJSONArray("weather").getJSONObject(0)["icon"].toString() // 아이콘

                    // 아이콘 이미지 URL을 가져오기 위한 작업
                    val url2 = URL("https://openweathermap.org/img/w/$weatherIcon.png")
                    connection2 = url2.openConnection()
                    is2 = connection2.getInputStream()
                    bitmap = BitmapFactory.decodeStream(is2) // Bitmap으로 반환
                    result[6] = getBase64String(bitmap)
                    weatherArray.add(result)
                } catch (e: Exception) {
                    Log.e(TAG, "openweathermap error ( api ? ) : ", e)
                }
            }
        } catch (mue: MalformedURLException) {
            System.err.println("잘못된 URL입니다.")
        } catch (ioe: IOException) {
            System.err.println("IOException $ioe")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return weatherArray
    }

    // Bitmap타입을 String 타입변환
    fun getBase64String(bitmap: Bitmap?): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val DELAY_WEATHER = 0 //날씨 슬라이딩 스레드시작 딜레이 시간
        private const val PERIOD_WEATHER = 5000 //날씨 슬라이딩 시간 간격

        // Firebase 연동을 위한 선언
        var database: FirebaseDatabase? = null
        @JvmField
        var databaseReference: DatabaseReference? = null

        init {
            database = FirebaseDatabase.getInstance()
        }

        @Throws(IOException::class)
        private fun readAll(rd: Reader): String {
            val sb = StringBuilder()
            var cp: Int
            while (rd.read().also { cp = it } != -1) {
                sb.append(cp.toChar())
            }
            return sb.toString()
        }

        // String타입을 Bitmap타입으로 변환
        @JvmStatic
        fun getStringToBitmap(stringBitmap: String?): Bitmap {
            val decodedByteArray =
                Base64.decode(stringBitmap, Base64.NO_WRAP)
            return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
        }

        // 유티크 키값 생성
        @JvmStatic
        val uuid: String
            get() = UUID.randomUUID().toString().replace("-".toRegex(), "")
    }
}