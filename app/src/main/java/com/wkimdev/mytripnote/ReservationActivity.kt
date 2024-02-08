package com.wkimdev.mytripnote

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.wkimdev.mytripnote.config.GlobalApplication
import com.wkimdev.mytripnote.config.PreferenceManager
import com.wkimdev.mytripnote.databinding.ActivityReservationBinding
import com.wkimdev.mytripnote.model.TravelItem
import java.util.*

/**
 * 호텔/식당 예약 화면
 */
class ReservationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservationBinding

    // 예약 타입 (호텔 또는 식당)
    private var reservationType = "hotel"
    //LatLng 클래스는 문자열이 아닌 위도 및 경도 쌍을 나타냅니다
    private var latLng: LatLng? = null //구글 Place API에서 리턴받은 위도 경도값 (구글 위도경도값 타입처리는 LatLng)
    private var placeName: String? = null
    private var intentPlaceName: String? = null // 인텐트로 부터 받은 장소값
    private var intentLatLngValue: String? = null // 인텐트로 부터 받은 위도경도값

    /**
     * - 뷰 바인딩
     * - 예약 타입별로 화면구성
     * - 구글 Place API Initialize
     * - 주소검색창 클릭시 지도 검색창 뜨도록 설정
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인텐트값 처리
        handleIntent()

        //
        initializePlacesApi()

        //
        setupListeners()

    }


    private fun setupListeners() {

        // 추가 버튼을 누르면, 여행 노트 첫화면 리사이클러뷰에 여행 내역 아이템이 추가된다.
        binding.btnAddTripNote.setOnClickListener {
            // Implementation for adding a trip note
            val travelItem = TravelItem().apply {
                placeName = binding.etHotelName.text.toString()
                placeAddress = binding.etHotelAddress.text.toString()
                reservationDate = binding.etHotelCheckDate.text.toString()
                placeType = reservationType
                latlng = latLng?.toString() // Assuming TravelItem.latlng is a String
            }

            saveReservationItem(travelItem)
            val intent = Intent(this@ReservationActivity, TripNoteActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            finish()

        }


        //@TODO 숙소(식당)위치 확인 영역 클릭시 구글Map 화면으로 이동!!
        binding.layoutCheckLocation.setOnClickListener {
            if (latLng != null || !intentLatLngValue.isNullOrEmpty()) {
                val intent = Intent(this@ReservationActivity, MapActivity::class.java).apply {
                    // Directly put Parcelable LatLng
                    putExtra("latLng", latLng)
                    putExtra("placeName", placeName ?: intentPlaceName)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this@ReservationActivity, "No required value (latitude/longitude)", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun initializePlacesApi() {

        // 구글 Place API Initialize the SDK
        Places.initialize(applicationContext, GlobalApplication.Companion.GOOGLE_API_KEY) // Replace YOUR_GOOGLE_API_KEY with your actual API Key

        // 필드를 설정하여 사용자가 선택한 후 반환할 장소 데이터 유형을 지정합니다.
        val fields = listOf(
            Place.Field.ID, Place.Field.NAME,
            Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.LAT_LNG
        )

        // 지도 검색 위젯을 발생시키는 인텐트
        // 최대 5개의 결과를 반환
        binding.etHotelAddress.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }


    private fun handleIntent() {
        //의미 -> Kotlin의 안전 호출 및 Elvis 연산자를 사용하여 인텐트 추가 처리를 간소화
        //let이 무엇인지에 대한 설명 -> https://kotlinlang.org/docs/scope-functions.html#let
        intent?.let {
            if (!it.getStringExtra("placeType").isNullOrEmpty()) { //값이 있다면
                if ("restaurant" == it.getStringExtra("placeType")) {
                    reservationType = "restaurant"
                    binding.popupTitle.text = "Restaurant 🍔"
                    binding.etHotelName.hint = "Restaurant Name"
                    binding.etHotelAddress.hint = "Restaurant Address"
                    binding.etHotelCheckDate.hint = "Please enter the restaurant reservation date\n (ex: 2021.11.20)"
                    binding.tvCheckLocation.text = "Check restaurant location "
                }
            }

            // 인텐트로 값을 받아, 예약 조회 화면을 보여주기 위한 처리
            if (it.getBooleanExtra("isListClick", false)) {
                intentPlaceName = it.getStringExtra("placeName")
                binding.etHotelName.setText(intentPlaceName)
                binding.etHotelAddress.setText(it.getStringExtra("placeAddress"))
                binding.etHotelCheckDate.setText(it.getStringExtra("reservationDate"))
                intentLatLngValue = it.getStringExtra("latLng")
                binding.btnAddTripNote.text = "Edit"
            }
        }
    }


    // 구글 Place API에서 장소 선택 후 리턴되는 결과 처리
    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data)
                //Log.e(TAG, "Place: " + place.getName() + ", " + place.getId()+ ", " + place.getTypes() + ", " + place.getAddress() + ", " + place.getLatLng()); // lat/lng: (36.09551,-115.1760672)

                //EditText의 text 프로퍼티 타입은 Editable이기 때문에, 문자열을 직접 할당할 수 없다.
                // CharSequence를 받는 setText 메소드를 사용해야 한다
                binding.etHotelName.setText(place.name)
                binding.etHotelAddress.setText(place.address)
                latLng = place.latLng
                placeName = place.name

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

    // 여행ID와 하위의 예약ID 기준으로 firebase에 예약내역 저장
    @SuppressLint("LongLogTag")
    fun saveReservationItem(travelItem: TravelItem?) {
        val currentTravelId = PreferenceManager.getString(this, "currentTravelId")
        val reservationId = "reservation-" + MainActivity.Companion.uuid
        Log.e(
            TAG, "저장되는 iD확인 : "
                    + "$currentTravelId/reservationData/$reservationId"
        )
        MainActivity.Companion.databaseReference!!.child("$currentTravelId/reservationData/$reservationId")
            .setValue(travelItem)
            .addOnSuccessListener(
                this,
                OnSuccessListener<Void?> { Log.e(TAG, "Firebase에 여행노트가 업데이트 됨!") })
    }

    companion object {
        private const val TAG = "HotelReservationActivity"
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}