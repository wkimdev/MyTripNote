package com.wkimdev.mytripnote

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wkimdev.mytripnote.databinding.ActivityAddReservationPopupBinding

/**
 * 호텔 또는 식당예약 팝업 화면
 */
class AddReservationPopupActivity : AppCompatActivity() {

    // Using View Binding to replace findViewById
    private lateinit var binding: ActivityAddReservationPopupBinding

    /*private var hotel: ImageView? = null
    private var restaurant: ImageView? = null*/

    /**
     * 뷰 선언
     * 버튼 이벤트 등록
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //"ActivityAddReservationPopupBinding"는 레이아웃 파일에서 자동으로 생성된다.
        binding = ActivityAddReservationPopupBinding.inflate(layoutInflater)
        setContentView(binding.root) // activity_add_reservation_popup xml를 찾아간다.!
        //setContentView(R.layout.activity_add_reservation_popup)

        /*hotel = findViewById(R.id.hotel)
        restaurant = findViewById(R.id.restaurant)*/

        // 호텔 예약 버튼 클릭 후 호텔 예약 화면 이동
        // Simplifying click listeners with Kotlin lambda expressions
        //: Kotlin의 람다 표현식을 사용하면 클릭 리스너를 더욱 간결하게 표현한다
        binding.hotel.setOnClickListener {
            navigateToReservation("hotel")
        }

        /*hotel.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AddReservationPopupActivity, ReservationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("placeType", "hotel")
            startActivity(intent)
            finish()
        })*/

        // 식당 예약 버튼 클릭 후 식당 예약 화면 이동
        //before refactoring
        /*restaurant.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AddReservationPopupActivity, ReservationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("placeType", "restaurant")
            startActivity(intent)
            finish()
        })*/

        //after refactoring
        binding.restaurant.setOnClickListener {
            navigateToReservation("restaurant")
        }
    }


    //예약화면으로 이동하는 공통함수
    private fun navigateToReservation(placeType: String) {
        val intent = Intent(this, ReservationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("placeType", placeType)
        }
        startActivity(intent)
        finish()
    }

}