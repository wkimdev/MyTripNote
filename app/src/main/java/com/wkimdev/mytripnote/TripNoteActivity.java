package com.wkimdev.mytripnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wkimdev.mytripnote.adapter.NoteAdapter;
import com.wkimdev.mytripnote.adapter.ReservationAdapter;
import com.wkimdev.mytripnote.config.PreferenceManager;
import com.wkimdev.mytripnote.model.NoteItem;
import com.wkimdev.mytripnote.model.ReservationItem;
import com.wkimdev.mytripnote.model.SearchData;
import com.wkimdev.mytripnote.model.TravelItem;

import java.util.ArrayList;

/**
 * 여행 노트 화면
 */
public class TripNoteActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "TripNoteActivity";

    // 여행 노트 화면 뷰 필드 선언
    private ImageView iv_thumnailImage;
    private TextView tv_youtubeTitle;
    private String videoId;
    private Button btn_add_reservation;
    private Button btn_add_note;

    // 노트 타이틀 상단
    private TextView tv_note_title;
    private TextView tv_travle_date;

    // 예약 내용 리사이클러뷰
    private RecyclerView rv_reservation;
    private LinearLayoutManager reservationLinearLayoutManager;

    // 여행노트 리사이클러뷰
    private RecyclerView rv_note;
    private LinearLayoutManager notelinearLayoutManager;

    // 파이어베이스 레퍼런스 생성
    public static FirebaseDatabase database;
    public static DatabaseReference databaseReferenceByTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_note);

        Log.e(TAG, "onCreate: ENTER");

        String currentTravelId = PreferenceManager.getString(this, "currentTravelId"); //진입한 노트의 여행ID
        String userAndroidId = PreferenceManager.getString(this, "userAndroidId"); //단말유니크ID

        // 노트내용 조회를 위해 파이어베이스 레퍼런스 새로 선언
        database = FirebaseDatabase.getInstance();
        String databaseRefPath = "Trip/" + userAndroidId + "/" + currentTravelId;
        databaseReferenceByTripId = database.getReference(databaseRefPath);

        //iv_thumnailImage = findViewById(R.id.iv_thumnailImage);
        //tv_youtubeTitle = findViewById(R.id.tv_youtubeTitle);
        btn_add_reservation = findViewById(R.id.btn_add_reservation);
        btn_add_note = findViewById(R.id.btn_add_note);
        tv_note_title = findViewById(R.id.tv_note_title);
        tv_travle_date = findViewById(R.id.tv_travle_date);
        rv_reservation = findViewById(R.id.rv_reservation);
        rv_note = findViewById(R.id.rv_note);

        reservationLinearLayoutManager = new LinearLayoutManager(this);
        notelinearLayoutManager = new LinearLayoutManager(this);


        Intent intent = getIntent();
        // 여행노트팝업에서 노트 생성 후 받는 값
        if (!TextUtils.isEmpty(intent.getStringExtra("travelNoteTitle"))) {
            tv_note_title.setText(intent.getStringExtra("travelNoteTitle"));
            tv_travle_date.setText(intent.getStringExtra("travelDate"));
        }

        // 예약추가 화면에서 예약내용을 추가한 뒤, 어뎁터를 통해 예약리스트를 추가하는 구문
        if (!TextUtils.isEmpty(intent.getStringExtra("placeName"))) {
            String placeName = intent.getStringExtra("placeName");
            String placeAddress = intent.getStringExtra("placeAddress");
            String reservationDate = intent.getStringExtra("reservationDate");
            String placeType = intent.getStringExtra("placeType");

            ArrayList<ReservationItem> reservationItems = new ArrayList<>();
            ReservationItem reservationItem = new ReservationItem();

            reservationItem.setPlaceName(placeName);
            reservationItem.setPlaceAddress(placeAddress);
            reservationItem.setReservationDate(reservationDate);
            reservationItem.setPlaceType(placeType);
            reservationItems.add(reservationItem);

            // 예약리스트 어뎁터 처리
            ReservationAdapter reservationAdapter = new ReservationAdapter(reservationItems, this);
            rv_reservation.setLayoutManager(reservationLinearLayoutManager);
            rv_reservation.setAdapter(reservationAdapter);
        }

        // 노트추가 화면에서 노트내용을 추가한 뒤, 어뎁터를 통해 노트리스트를 추가하는 구문
        if (!TextUtils.isEmpty(intent.getStringExtra("noteTitle"))) {
            String noteTitle = intent.getStringExtra("noteTitle");
            String noteContent = intent.getStringExtra("noteContent");
            String youtubeThumnail = intent.getStringExtra("youtubeThumnail");

            ArrayList<NoteItem> noteItems = new ArrayList<>();
            NoteItem noteItem = new NoteItem();

            noteItem.setNoteTitle(noteTitle);
            noteItem.setNoteContent(noteContent);
            noteItem.setYoutubeThumnailImage(youtubeThumnail);
            noteItems.add(noteItem);

            NoteAdapter noteAdapter = new NoteAdapter(noteItems, this, this);
            rv_note.setLayoutManager(notelinearLayoutManager);
            rv_note.setAdapter(noteAdapter);
        }

        // 여행노트 화면의 예약/노트 리스트를 조회하는 이벤트
        databaseReferenceByTripId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<ReservationItem> reservationItems = new ArrayList<>();
                ArrayList<NoteItem> noteItems = new ArrayList<>();

                for(DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    String key = snapshot.getKey();
                    if ("reservationData".equals(key)) {
                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                            TravelItem travelItem = snapshot1.getValue(TravelItem.class);
                            // 예약정보 리스트에 넣기
                            ReservationItem reservationItem = new ReservationItem();

                            reservationItem.setReservationId(snapshot1.getKey());
                            reservationItem.setPlaceName(travelItem.getPlaceName());
                            reservationItem.setPlaceAddress(travelItem.getPlaceAddress());
                            reservationItem.setReservationDate(travelItem.getReservationDate());
                            reservationItem.setPlaceType(travelItem.getPlaceType());
                            reservationItem.setLatLng(travelItem.getLatlng());
                            reservationItems.add(reservationItem);
                        }
                    } else if ("noteData".equals(key)) {
                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                            TravelItem travelItem = snapshot1.getValue(TravelItem.class);

                            // 노트정보 리스트에 넣기
                            NoteItem noteItem = new NoteItem();
                            noteItem.setNoteId(snapshot1.getKey());
                            noteItem.setNoteTitle(travelItem.getNoteTitle());
                            noteItem.setNoteContent(travelItem.getNoteContent());
                            noteItem.setYoutubeId(travelItem.getYoutubeId());
                            noteItem.setYoutubeTitle(travelItem.getYoutubeTitle());
                            noteItem.setYoutubeThumnailImage(travelItem.getYoutubeThumnailImage());
                            noteItems.add(noteItem);

                        }
                    } else if ("travelTitle".equals(key)) {
                        tv_note_title.setText(snapshot.getValue().toString());
                    } else if ("travelDate".equals(key)) {
                        tv_travle_date.setText("여행 날짜 : " + snapshot.getValue().toString());
                    }
                }

                ReservationAdapter reservationAdapter = new ReservationAdapter(reservationItems,
                        TripNoteActivity.this);
                rv_reservation.setLayoutManager(reservationLinearLayoutManager);
                rv_reservation.setAdapter(reservationAdapter);

                NoteAdapter noteAdapter = new NoteAdapter(noteItems,
                        TripNoteActivity.this, TripNoteActivity.this);
                rv_note.setLayoutManager(notelinearLayoutManager);
                rv_note.setAdapter(noteAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, String.valueOf(databaseError.toException()));
            }
        });
    }

    // 버튼 이벤트 등록
    @Override
    protected void onResume() {
        super.onResume();

        // 예약 추가 버튼 클릭 후 예약 화면으로 이동
        btn_add_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TripNoteActivity.this, AddReservationPopupActivity.class));
            }
        });

        // 여행 노트 추가 클릭 후 노트 작성 화면으로 이동
        btn_add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TripNoteActivity.this, NoteActivity.class));
            }
        });
    }

    @Override
    public void onClickYoutubeList(SearchData searchData) {

    }

    // 예약항목 리스트뷰 클릭시, 예약 화면으로 이동되도록 처리하는 이벤트
    @Override
    public void onClickReservationList(ReservationItem reservationItem) {
        Intent intent = new Intent(TripNoteActivity.this, ReservationActivity.class);
        intent.putExtra("reservationId", reservationItem.getReservationId()); //id
        intent.putExtra("placeType", reservationItem.getPlaceType()); // reservationType
        intent.putExtra("placeName", reservationItem.getPlaceName());
        intent.putExtra("placeAddress", reservationItem.getPlaceAddress());
        intent.putExtra("reservationDate", reservationItem.getReservationDate());
        intent.putExtra("latLng", reservationItem.getLatLng());
        intent.putExtra("isListClick", true);

        startActivity(intent);
        finish();

    }

    // 노트 항목 리스트뷰 클릭시, 노트 화면으로 이동되도록 처리하는 이벤트
    @Override
    public void onClickNoteList(NoteItem noteItem) {
        Intent intent = new Intent(TripNoteActivity.this, NoteActivity.class);
        intent.putExtra("noteTitle", noteItem.getNoteTitle());
        intent.putExtra("noteContent", noteItem.getNoteContent());
        intent.putExtra("videoId", noteItem.getYoutubeId());
        intent.putExtra("youtubeTitle", noteItem.getYoutubeTitle());
        intent.putExtra("thumnailImage", noteItem.getYoutubeThumnailImage());
        intent.putExtra("isListClick", true);

        startActivity(intent);
    }

    @Override
    public void onClickTravelList(String travelId) {

    }

}