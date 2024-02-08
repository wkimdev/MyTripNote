package com.wkimdev.mytripnote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.wkimdev.mytripnote.config.GlobalApplication
import com.wkimdev.mytripnote.databinding.ActivityHotelSearchBinding
import java.util.*


// 예약할 호텔을 찾는 화면
class HotelSearchActivity : AppCompatActivity() {
    //private var btn_map_search: Button? = null
    private lateinit var binding: ActivityHotelSearchBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHotelSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_hotel_search)
        Log.e(TAG, "onCreate: HotelSearchActivity !!! ")
        //btn_map_search = findViewById(R.id.btn_map_search)

        // Initialize the SDK
        Places.initialize(applicationContext, GlobalApplication.Companion.GOOGLE_API_KEY)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        // 필드를 설정하여 사용자가 선택한 후 반환할 장소 데이터 유형을 지정합니다.
        val fields = Arrays.asList(
            Place.Field.ID, Place.Field.NAME,
            Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.LAT_LNG
        )

        // 구글 지도 검색 위젯을 발생시키는 인텐트
        // Start the autocomplete intent.
        // 정확한 주소가 있는 결과만 반환하는 필터를 설정
        // Places API는 최대 5개의 결과를 반환합니다.
        binding.btnMapSearch.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fields
            ) //.setTypeFilter(TypeFilter.ADDRESS)
                .build(this@HotelSearchActivity)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
        /*btn_map_search.setOnClickListener(View.OnClickListener {
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fields
            ) //.setTypeFilter(TypeFilter.ADDRESS)
                .build(this@HotelSearchActivity)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        })*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e(TAG, "onCreate: onActivityResult 1111 !!! ")
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            Log.e(TAG, "onCreate: onActivityResult 222 !!! ")
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data)
                // - 한국이 아니면 -> 구글 지도 클릭
                Log.e(
                    TAG, "Place: " + place.name + ", " + place.id + ", "
                            + place.types + ", " + place.address + ", " + place.latLng
                )
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data)
                Log.i(TAG, status.statusMessage)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val TAG = "HotelSearchActivity"
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}