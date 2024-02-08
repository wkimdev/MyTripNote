package com.wkimdev.mytripnote

import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.wkimdev.mytripnote.config.PreferenceManager
import com.wkimdev.mytripnote.databinding.ActivityAddTripPopupBinding
import com.wkimdev.mytripnote.model.TravelItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * 여행노트를 만드는 팝업 화면
 */
class AddTripPopupActivity : AppCompatActivity() {

    // Using View Binding to replace findViewById
    private lateinit var binding: ActivityAddTripPopupBinding;
    private var myCalendar: Calendar = Calendar.getInstance()

    /**
     * 뷰 바인딩을 통한 뷰 선언
     * 버튼 이벤트 등록
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTripPopupBinding.inflate(layoutInflater);
        setContentView(binding.root)

        val travelItem = TravelItem()



        // 여행 만들기 버튼 클릭
        binding.btnAddTripNote.setOnClickListener{
            val destination = binding.etTravelCountry.text.toString()
            val travelTitle = binding.etTravelTitle.text.toString()
            val travelDate = binding.etTravelDate.text.toString()

            // 파이어베이스에 저장
            //travelItem.destination = destination
            travelItem.travelTitle = travelTitle
            travelItem.travelDate = travelDate
            travelItem.travelType = "domestic"
            saveItem(travelItem)


            // 여행노트 작성 화면으로 이동
            val intent = Intent(this@AddTripPopupActivity, TripNoteActivity::class.java)
            intent.putExtra("travelCountry", destination)
            intent.putExtra("travelNoteTitle", travelTitle)
            intent.putExtra("travelDate", travelDate)
            startActivity(intent)
            finish()
        }
    }


    /**
     * 버튼 이벤트 등록
     */
    override fun onResume() {
        super.onResume()
        binding.etTravelDate!!.setOnClickListener {
            val myDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
                myCalendar!![Calendar.YEAR] = year
                myCalendar!![Calendar.MONTH] = month
                myCalendar!![Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabel()
            }
        }
        /*et_travel_date!!.setOnClickListener(View.OnClickListener { // 캘린더를 띄우고 캘린더에서 이벤트 처리
            val myDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
                myCalendar!![Calendar.YEAR] = year
                myCalendar!![Calendar.MONTH] = month
                myCalendar!![Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabel()
            }
        })*/
    }

    private fun updateLabel() {
        val myFormat = "yyyy/MM/dd" // 출력형식   2018/11/28
        val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
        val et_date = findViewById<View>(R.id.et_travel_date) as EditText
        et_date.setText(sdf.format(myCalendar!!.time))
    }

    // 최초로 생성한 여행 노트를 firebase에 저장 하는 메소드
    fun saveItem(travelItem: TravelItem?) {
        //val travelId = "travel-" + MainActivity.Companion.getUuid()
        val travelId = "travel-" + MainActivity.Companion.uuid
        PreferenceManager.setString(this, "currentTravelId", travelId)
        MainActivity.Companion.databaseReference!!.child(travelId) // ex: travel1, travel2...
            .setValue(travelItem)
            .addOnSuccessListener(
                this,
                OnSuccessListener<Void?> { Log.e(TAG, "Firebase에 여행이 저장 / 수정됨!") })
    }

    companion object {
        private const val TAG = "AddTripPopupActivity"
    }
}