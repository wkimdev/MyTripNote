package com.wkimdev.mytripnote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.wkimdev.mytripnote.config.GlobalApplication;

import java.util.Arrays;
import java.util.List;

public class HotelSearchActivity extends AppCompatActivity {

    private static final String TAG = "HotelSearchActivity";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    private Button btn_map_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_search);

        Log.e(TAG, "onCreate: HotelSearchActivity !!! ");

        btn_map_search = findViewById(R.id.btn_map_search);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), GlobalApplication.GOOGLE_API_KEY);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // 필드를 설정하여 사용자가 선택한 후 반환할 장소 데이터 유형을 지정합니다.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.LAT_LNG);

        // 구글 지도 검색 위젯을 발생시키는 인텐트
        // Start the autocomplete intent.
        // 정확한 주소가 있는 결과만 반환하는 필터를 설정
        // Places API는 최대 5개의 결과를 반환합니다.
        btn_map_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        //.setTypeFilter(TypeFilter.ADDRESS)
                        .build(HotelSearchActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e(TAG, "onCreate: onActivityResult 1111 !!! ");
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            Log.e(TAG, "onCreate: onActivityResult 222 !!! ");
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                // - 한국이 아니면 -> 구글 지도 클릭
                Log.e(TAG, "Place: " + place.getName() + ", " + place.getId()+ ", "
                        + place.getTypes() + ", " + place.getAddress() + ", " + place.getLatLng());

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



}