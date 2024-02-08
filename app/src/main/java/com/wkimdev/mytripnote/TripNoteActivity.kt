package com.wkimdev.mytripnote

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wkimdev.mytripnote.adapter.NoteAdapter
import com.wkimdev.mytripnote.adapter.ReservationAdapter
import com.wkimdev.mytripnote.config.PreferenceManager
import com.wkimdev.mytripnote.model.NoteItem
import com.wkimdev.mytripnote.model.ReservationItem
import com.wkimdev.mytripnote.model.SearchData
import com.wkimdev.mytripnote.model.TravelItem

/**
 * 여행 노트 화면
 */
class TripNoteActivity : AppCompatActivity(), OnClickListener {

    // 여행 노트 화면 뷰 필드 선언
    private val iv_thumnailImage: ImageView? = null
    private val tv_youtubeTitle: TextView? = null
    private val videoId: String? = null
    private var btn_add_reservation: Button? = null
    private var btn_add_note: Button? = null

    // 노트 타이틀 상단
    private var tv_note_title: TextView? = null
    private var tv_travle_date: TextView? = null

    // 예약 내용 리사이클러뷰
    private var rv_reservation: RecyclerView? = null
    private var reservationLinearLayoutManager: LinearLayoutManager? = null

    // 여행노트 리사이클러뷰
    private var rv_note: RecyclerView? = null
    private var notelinearLayoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_note)
        Log.e(TAG, "onCreate: ENTER")
        val currentTravelId = PreferenceManager.getString(this, "currentTravelId") //진입한 노트의 여행ID
        val userAndroidId = PreferenceManager.getString(this, "userAndroidId") //단말유니크ID

        // 노트내용 조회를 위해 파이어베이스 레퍼런스 새로 선언
        database = FirebaseDatabase.getInstance()
        val databaseRefPath = "Trip/$userAndroidId/$currentTravelId"
        databaseReferenceByTripId = database!!.getReference(databaseRefPath)

        //iv_thumnailImage = findViewById(R.id.iv_thumnailImage);
        //tv_youtubeTitle = findViewById(R.id.tv_youtubeTitle);
        btn_add_reservation = findViewById(R.id.btn_add_reservation)
        btn_add_note = findViewById(R.id.btn_add_note)
        tv_note_title = findViewById(R.id.tv_note_title)
        tv_travle_date = findViewById(R.id.tv_travle_date)
        rv_reservation = findViewById(R.id.rv_reservation)
        rv_note = findViewById(R.id.rv_note)
        reservationLinearLayoutManager = LinearLayoutManager(this)
        notelinearLayoutManager = LinearLayoutManager(this)
        val intent = intent
        // 여행노트팝업에서 노트 생성 후 받는 값
        if (!TextUtils.isEmpty(intent.getStringExtra("travelNoteTitle"))) {
            tv_note_title.setText(intent.getStringExtra("travelNoteTitle"))
            tv_travle_date.setText(intent.getStringExtra("travelDate"))
        }

        // 예약추가 화면에서 예약내용을 추가한 뒤, 어뎁터를 통해 예약리스트를 추가하는 구문
        if (!TextUtils.isEmpty(intent.getStringExtra("placeName"))) {
            val placeName = intent.getStringExtra("placeName")
            val placeAddress = intent.getStringExtra("placeAddress")
            val reservationDate = intent.getStringExtra("reservationDate")
            val placeType = intent.getStringExtra("placeType")
            val reservationItems = ArrayList<ReservationItem>()
            val reservationItem = ReservationItem()
            reservationItem.placeName = placeName
            reservationItem.placeAddress = placeAddress
            reservationItem.reservationDate = reservationDate
            reservationItem.placeType = placeType
            reservationItems.add(reservationItem)

            // 예약리스트 어뎁터 처리
            val reservationAdapter = ReservationAdapter(reservationItems, this)
            rv_reservation.setLayoutManager(reservationLinearLayoutManager)
            rv_reservation.setAdapter(reservationAdapter)
        }

        // 노트추가 화면에서 노트내용을 추가한 뒤, 어뎁터를 통해 노트리스트를 추가하는 구문
        if (!TextUtils.isEmpty(intent.getStringExtra("noteTitle"))) {
            val noteTitle = intent.getStringExtra("noteTitle")
            val noteContent = intent.getStringExtra("noteContent")
            val youtubeThumnail = intent.getStringExtra("youtubeThumnail")
            val noteItems = ArrayList<NoteItem>()
            val noteItem = NoteItem()
            noteItem.noteTitle = noteTitle
            noteItem.noteContent = noteContent
            noteItem.youtubeThumnailImage = youtubeThumnail
            noteItems.add(noteItem)
            val noteAdapter = NoteAdapter(noteItems, this, this)
            rv_note.setLayoutManager(notelinearLayoutManager)
            rv_note.setAdapter(noteAdapter)
        }

        // 여행노트 화면의 예약/노트 리스트를 조회하는 이벤트
        databaseReferenceByTripId!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val reservationItems = ArrayList<ReservationItem>()
                val noteItems = ArrayList<NoteItem>()
                for (snapshot in dataSnapshot.children) {
                    val key = snapshot.key
                    if ("reservationData" == key) {
                        for (snapshot1 in snapshot.children) {
                            val travelItem = snapshot1.getValue(
                                TravelItem::class.java
                            )
                            // 예약정보 리스트에 넣기
                            val reservationItem = ReservationItem()
                            reservationItem.reservationId = snapshot1.key
                            reservationItem.placeName = travelItem.getPlaceName()
                            reservationItem.placeAddress = travelItem.getPlaceAddress()
                            reservationItem.reservationDate = travelItem.getReservationDate()
                            reservationItem.placeType = travelItem.getPlaceType()
                            reservationItem.latLng = travelItem.getLatlng()
                            reservationItems.add(reservationItem)
                        }
                    } else if ("noteData" == key) {
                        for (snapshot1 in snapshot.children) {
                            val travelItem = snapshot1.getValue(
                                TravelItem::class.java
                            )

                            // 노트정보 리스트에 넣기
                            val noteItem = NoteItem()
                            noteItem.noteId = snapshot1.key
                            noteItem.noteTitle = travelItem.getNoteTitle()
                            noteItem.noteContent = travelItem.getNoteContent()
                            noteItem.youtubeId = travelItem.getYoutubeId()
                            noteItem.youtubeTitle = travelItem.getYoutubeTitle()
                            noteItem.youtubeThumnailImage = travelItem.getYoutubeThumnailImage()
                            noteItems.add(noteItem)
                        }
                    } else if ("travelTitle" == key) {
                        tv_note_title.setText(snapshot.value.toString())
                    } else if ("travelDate" == key) {
                        tv_travle_date.setText("여행 날짜 : " + snapshot.value.toString())
                    }
                }
                val reservationAdapter = ReservationAdapter(
                    reservationItems,
                    this@TripNoteActivity
                )
                rv_reservation.setLayoutManager(reservationLinearLayoutManager)
                rv_reservation.setAdapter(reservationAdapter)
                val noteAdapter = NoteAdapter(
                    noteItems,
                    this@TripNoteActivity, this@TripNoteActivity
                )
                rv_note.setLayoutManager(notelinearLayoutManager)
                rv_note.setAdapter(noteAdapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, databaseError.toException().toString())
            }
        })
    }

    // 버튼 이벤트 등록
    override fun onResume() {
        super.onResume()

        // 예약 추가 버튼 클릭 후 예약 화면으로 이동
        btn_add_reservation!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@TripNoteActivity,
                    AddReservationPopupActivity::class.java
                )
            )
        })

        // 여행 노트 추가 클릭 후 노트 작성 화면으로 이동
        btn_add_note!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@TripNoteActivity,
                    NoteActivity::class.java
                )
            )
        })
    }

    override fun onClickYoutubeList(searchData: SearchData) {}

    // 예약항목 리스트뷰 클릭시, 예약 화면으로 이동되도록 처리하는 이벤트
    override fun onClickReservationList(reservationItem: ReservationItem) {
        val intent = Intent(this@TripNoteActivity, ReservationActivity::class.java)
        intent.putExtra("reservationId", reservationItem.reservationId) //id
        intent.putExtra("placeType", reservationItem.placeType) // reservationType
        intent.putExtra("placeName", reservationItem.placeName)
        intent.putExtra("placeAddress", reservationItem.placeAddress)
        intent.putExtra("reservationDate", reservationItem.reservationDate)
        intent.putExtra("latLng", reservationItem.latLng)
        intent.putExtra("isListClick", true)
        startActivity(intent)
        finish()
    }

    // 노트 항목 리스트뷰 클릭시, 노트 화면으로 이동되도록 처리하는 이벤트
    override fun onClickNoteList(noteItem: NoteItem) {
        val intent = Intent(this@TripNoteActivity, NoteActivity::class.java)
        intent.putExtra("noteTitle", noteItem.noteTitle)
        intent.putExtra("noteContent", noteItem.noteContent)
        intent.putExtra("videoId", noteItem.youtubeId)
        intent.putExtra("youtubeTitle", noteItem.youtubeTitle)
        intent.putExtra("thumnailImage", noteItem.youtubeThumnailImage)
        intent.putExtra("isListClick", true)
        startActivity(intent)
    }

    override fun onClickTravelList(travelId: String?) {}

    companion object {
        private const val TAG = "TripNoteActivity"

        // 파이어베이스 레퍼런스 생성
        var database: FirebaseDatabase? = null
        var databaseReferenceByTripId: DatabaseReference? = null
    }
}