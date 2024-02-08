package com.wkimdev.mytripnote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.wkimdev.mytripnote.config.GlobalApplication;
import com.wkimdev.mytripnote.config.PreferenceManager;
import com.wkimdev.mytripnote.model.TravelItem;

import java.util.Arrays;
import java.util.List;

/**
 * 호텔/식당 예약 화면
 */
public class ReservationActivity extends AppCompatActivity {

    private static final String TAG = "HotelReservationActivity";

    // View 필드 선언
    private TextView popupTitle;
    private EditText et_hotel_name;
    private EditText et_hotel_address;
    private EditText et_hotel_check_date;
    private Button btn_add_trip_note;
    private LinearLayout layout_check_location;
    private TextView tv_check_location;

    // 예약 타입 (호텔 또는 식당)
    private String reservationType = "hotel";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private String latLng; //구글 Place API에서 리턴받은 위도 경도값
    private String placeName;
    private String intentPlaceName; // 인텐트로 부터 받은 장소값
    private String intentlatLngVaue; // 인텐트로 부터 받은 위도경도값


    /**
     *  - 뷰 바인딩
     *  - 예약 타입별로 화면구성
     *  - 구글 Place API Initialize
     *      - 주소검색창 클릭시 지도 검색창 뜨도록 설정
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        et_hotel_name = findViewById(R.id.et_hotel_name);
        et_hotel_address = findViewById(R.id.et_hotel_address);
        et_hotel_check_date = findViewById(R.id.et_hotel_check_date);
        btn_add_trip_note = findViewById(R.id.btn_add_trip_note);
        popupTitle = findViewById(R.id.popupTitle);
        layout_check_location = findViewById(R.id.layout_check_location);
        tv_check_location = findViewById(R.id.tv_check_location);

        Intent intent = getIntent();
        // 예약 타입(placeType)에 따라 화면을 다르게 그리기 위한 구문
        if (!TextUtils.isEmpty(intent.getStringExtra("placeType"))) {
            if ("restaurant".equals(intent.getStringExtra("placeType"))) {
                reservationType = "restaurant";
                popupTitle.setText("식당 🍔");
                et_hotel_name.setHint("식당 이름");
                et_hotel_address.setHint("식당 주소");
                et_hotel_check_date.setHint("식당 예약일을 입력해주세요 \n (ex: 2021.11.20)");
                tv_check_location.setText("식당 위치 확인 ");
            }
        }

        // 인텐트로 값을 받아, 예약 조회 화면을 보여주기 위한 처리
        if (intent.getBooleanExtra("isListClick", false)) {
            // TODO - 삭제기능 구현을 위해 reservationId 처리 필요
            intentPlaceName = intent.getStringExtra("placeName");
            et_hotel_name.setText(intentPlaceName);
            et_hotel_address.setText(intent.getStringExtra("placeAddress"));
            et_hotel_check_date.setText(intent.getStringExtra("reservationDate"));
            // db에 저장된 위치값 받아오기
            intentlatLngVaue = intent.getStringExtra("latLng");
            btn_add_trip_note.setText("수정");
        }


        // 구글 Place API Initialize the SDK
        Places.initialize(getApplicationContext(), GlobalApplication.GOOGLE_API_KEY);

        // 필드를 설정하여 사용자가 선택한 후 반환할 장소 데이터 유형을 지정합니다.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.LAT_LNG);

        // 지도 검색 위젯을 발생시키는 인텐트
        // 최대 5개의 결과를 반환
        et_hotel_name.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(ReservationActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    // 버튼 이벤트 등록
    @Override
    protected void onResume() {
        super.onResume();

        // 추가 버튼을 누르면, 여행 노트 첫화면 리사이클러뷰에 여행 내역 아이템이 추가된다.
        btn_add_trip_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hotelName = et_hotel_name.getText().toString();
                String hotelAddress = et_hotel_address.getText().toString();
                String reservationDate = et_hotel_check_date.getText().toString();

                TravelItem travelItem = new TravelItem();
                travelItem.setPlaceName(hotelName);
                travelItem.setPlaceAddress(hotelAddress);
                travelItem.setReservationDate(reservationDate);
                travelItem.setPlaceType(reservationType);
                travelItem.setLatlng(latLng);
                // firebase에 예약내역 저장
                saveReservationItem(travelItem);

                Intent intent = new Intent(ReservationActivity.this, TripNoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        // 숙소(식당)위치 확인 영역 클릭시 구글Map 화면으로 이동
        layout_check_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 선택한 주소의 위도 경도값을 받아 지도를 띄운다(구글 MAP API)
                if (!TextUtils.isEmpty(latLng) || !TextUtils.isEmpty(intentlatLngVaue)) {
                    Intent intent = new Intent(ReservationActivity.this, MapActivity.class);
                    intent.putExtra("latLng", ( null != latLng ) ? latLng : intentlatLngVaue);
                    intent.putExtra("placeName",  ( null != placeName ) ? placeName : intentPlaceName);
                    startActivity(intent);
                    //finish();
                } else {
                    Toast.makeText(ReservationActivity.this, "필수값이 없습니다(위도/경도)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 구글 Place API에서 장소 선택 후 리턴되는 결과 처리
    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //Log.e(TAG, "Place: " + place.getName() + ", " + place.getId()+ ", " + place.getTypes() + ", " + place.getAddress() + ", " + place.getLatLng()); // lat/lng: (36.09551,-115.1760672)

                et_hotel_name.setText(place.getName());
                et_hotel_address.setText(place.getAddress());
                latLng = place.getLatLng().toString();
                placeName = place.getName();

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 여행ID와 하위의 예약ID 기준으로 firebase에 예약내역 저장
    @SuppressLint("LongLogTag")
    public void saveReservationItem(TravelItem travelItem) {

        String currentTravelId = PreferenceManager.getString(this, "currentTravelId");
        String reservationId = "reservation-" + MainActivity_bakk.getUuid();

        Log.e(TAG, "저장되는 iD확인 : "
                + (currentTravelId + "/reservationData/" + reservationId));

        MainActivity_bakk.databaseReference.child(currentTravelId + "/reservationData/" + reservationId)
                .setValue(travelItem)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "Firebase에 여행노트가 업데이트 됨!");
                    }
                });

    }

}