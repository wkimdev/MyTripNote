package com.wkimdev.mytripnote

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * 호텔의 위치를 알려주는 맵 화면
 */
class MapActivity : FragmentActivity(), OnMapReadyCallback {
    // 지도 데이터 및 뷰에 대한 액세스 권한을 제공
    private var mMap: GoogleMap? = null

    // UI settings
    private var mUiSettings: UiSettings? = null

    // 지도에 위치와 마커표시를 위한 필드 선언
    private var latValue: String? = null
    private var lngValue: String? = null
    private var placeName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        //지도 기본 요소
        //지도 수명 주기 관리 및 앱 UI 상위요소
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        // 지도에 위치와 마커표시를 위한 인텐트값 처리
        val intent = intent
        if (!TextUtils.isEmpty(intent.getStringExtra("latLng"))) {
            val latLng = intent.getStringExtra("latLng")
            placeName = intent.getStringExtra("placeName")
            val latlngValue = latLng!!.replace("[()]".toRegex(), "").split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            latValue = latlngValue[0].replace("lat/lng: ", "")
            lngValue = latlngValue[1]
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mUiSettings = mMap!!.uiSettings

        // 지도의 줌인 아웃, 스크롤, 줌제스쳐 가능 설정
        mUiSettings!!.isZoomControlsEnabled = true
        mUiSettings!!.isScrollGesturesEnabled = true
        mUiSettings!!.isZoomGesturesEnabled = true
        mUiSettings!!.isCompassEnabled = true

        // 전달받은 값들로 카메라 위치를 이동시키고 마커를 표기한다
        val doubleLatValue = latValue!!.toDouble()
        val doubleLngValue = lngValue!!.toDouble()
        val place = LatLng(doubleLatValue, doubleLngValue)
        val marker = mMap!!.addMarker(
            MarkerOptions()
                .position(place)
                .title(placeName) //.snippet("Population: 4,137,400")
        )
        // 마커가 보이도록 표기
        marker!!.showInfoWindow()
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(place)) // 좌표에 맞춰 지도의 중심을 맞춰준다
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 14f)) //지도를 14배율로 확대해서 보여줌
    }

    /**
     * Checks if the map is ready (which depends on whether the Google Play services APK is
     * available. This should be called prior to calling any methods on GoogleMap.
     */
    private fun checkReady(): Boolean {
        if (mMap == null) {
            Toast.makeText(this, "맵이 준비 안되어있음", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {
        private const val TAG = "MapActivity"
    }
}