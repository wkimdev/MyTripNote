package com.wkimdev.mytripnote;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.wkimdev.mytripnote.config.PreferenceManager;
import com.wkimdev.mytripnote.model.TravelItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 여행노트를 만드는 팝업 화면
 */
public class AddTripPopupActivity extends AppCompatActivity{

    private static final String TAG = "AddTripPopupActivity";
    private EditText et_travel_country;
    private EditText et_travel_date;
    private EditText et_travel_title;
    private Button btn_add_trip_note;
    private Calendar myCalendar;

    /**
     * 뷰 선언
     * 버튼 이벤트 등록
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip_popup);

        et_travel_country = findViewById(R.id.et_travel_country);
        et_travel_date = findViewById(R.id.et_travel_date);
        et_travel_title = findViewById(R.id.et_travel_title);
        btn_add_trip_note = findViewById(R.id.btn_add_trip_note);
        myCalendar = Calendar.getInstance();
        TravelItem travelItem = new TravelItem();

        // 여행 만들기 버튼 클릭
        btn_add_trip_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String destination = et_travel_country.getText().toString();
                String travelTitle = et_travel_title.getText().toString();
                String travelDate = et_travel_date.getText().toString();

                // 파이어베이스에 저장
                travelItem.setDestination(destination);
                travelItem.setTravelTitle(travelTitle);
                travelItem.setTravelDate(travelDate);
                travelItem.setTravelType("domestic");
                saveItem(travelItem);

                // 여행노트 작성 화면으로 이동
                Intent intent = new Intent(AddTripPopupActivity.this, TripNoteActivity.class);
                intent.putExtra("travelCountry", destination);
                intent.putExtra("travelNoteTitle", travelTitle);
                intent.putExtra("travelDate", travelDate);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 버튼 이벤트 등록
     */
    @Override
    protected void onResume() {
        super.onResume();

        et_travel_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 캘린더를 띄우고 캘린더에서 이벤트 처리
                DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                };
            }
        });
    }

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        EditText et_date = (EditText) findViewById(R.id.et_travel_date);
        et_date.setText(sdf.format(myCalendar.getTime()));
    }

    // 최초로 생성한 여행 노트를 firebase에 저장 하는 메소드
    public void saveItem(TravelItem travelItem) {
        String travelId = "travel-" + MainActivity_bakk.getUuid();
        PreferenceManager.setString(this, "currentTravelId", travelId);
        MainActivity_bakk.databaseReference.child(travelId) // ex: travel1, travel2...
                .setValue(travelItem)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "Firebase에 여행이 저장 / 수정됨!");
                    }
                });
    }

}