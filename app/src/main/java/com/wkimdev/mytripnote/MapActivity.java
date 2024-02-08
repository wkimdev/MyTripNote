package com.wkimdev.mytripnote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.daum.mf.map.api.MapView;


/**
 * 호텔의 위치를 알려주는 맵 화면
*/
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";

    // 지도 데이터 및 뷰에 대한 액세스 권한을 제공
    private GoogleMap mMap;
    // UI settings
    private UiSettings mUiSettings;

    // 지도에 위치와 마커표시를 위한 필드 선언
    private String latValue;
    private String lngValue;
    private String placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //지도 기본 요소
        //지도 수명 주기 관리 및 앱 UI 상위요소
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // 지도에 위치와 마커표시를 위한 인텐트값 처리
        Intent intent = getIntent();
        if (!TextUtils.isEmpty(intent.getStringExtra("latLng"))) {
            String latLng = intent.getStringExtra("latLng");
            placeName = intent.getStringExtra("placeName");
            String[] latlngValue = latLng.replaceAll("[()]", "").split(",");
            latValue = latlngValue[0].replace("lat/lng: ", "");
            lngValue = latlngValue[1];
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        // 지도의 줌인 아웃, 스크롤, 줌제스쳐 가능 설정
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setCompassEnabled(true);

        // 전달받은 값들로 카메라 위치를 이동시키고 마커를 표기한다
        Double doubleLatValue = Double.parseDouble(latValue);
        Double doubleLngValue = Double.parseDouble(lngValue);
        LatLng place = new LatLng( doubleLatValue, doubleLngValue);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(place)
                .title(placeName)
                //.snippet("Population: 4,137,400")
        );
        // 마커가 보이도록 표기
        marker.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(place)); // 좌표에 맞춰 지도의 중심을 맞춰준다
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place,14)); //지도를 14배율로 확대해서 보여줌
    }

    /**
     * Checks if the map is ready (which depends on whether the Google Play services APK is
     * available. This should be called prior to calling any methods on GoogleMap.
     */
    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, "맵이 준비 안되어있음", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}