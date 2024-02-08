package com.wkimdev.mytripnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * 호텔 또는 식당예약 팝업 화면
 */
public class AddReservationPopupActivity extends AppCompatActivity {

    private ImageView hotel;
    private ImageView restaurant;

    /**
     * 뷰 선언
     * 버튼 이벤트 등록
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation_popup);

        hotel = findViewById(R.id.hotel);
        restaurant = findViewById(R.id.restaurant);

        // 호텔 예약 버튼 클릭 후 호텔 예약 화면 이동
        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddReservationPopupActivity.this, ReservationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("placeType", "hotel");
                startActivity(intent);
                finish();
            }
        });

        // 식당 예약 버튼 클릭 후 식당 예약 화면 이동
        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddReservationPopupActivity.this, ReservationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("placeType", "restaurant");
                startActivity(intent);
                finish();
            }
        });


    }
}