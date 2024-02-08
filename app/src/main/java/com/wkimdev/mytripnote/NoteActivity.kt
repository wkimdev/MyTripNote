package com.wkimdev.mytripnote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.wkimdev.mytripnote.config.PreferenceManager
import com.wkimdev.mytripnote.databinding.ActivityHotelSearchBinding
import com.wkimdev.mytripnote.databinding.ActivityNoteBinding
import com.wkimdev.mytripnote.model.TravelItem


/**
 * 유튜브 첨부가능한 일반 여행 노트 화면
 */
class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private var videoId: String? = null
    private var thumnailImage: String? = null

    /**
     * - 뷰 선언
     * - 유튜브리스트 화면으로 부터 인텐트값을 받도록 처리
     * - 여행노트 화면으로 부터 인텐트값을 받도록 처리
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 유튜브 영상 리스트에서 선택한 값을 인텐트로 받는다
        val intent = intent
        if (!TextUtils.isEmpty(intent.getStringExtra("videoId"))) {
            videoId = intent.getStringExtra("videoId")
            thumnailImage = intent.getStringExtra("thumnailImage")
            Glide.with(this).load(thumnailImage).into(binding.ivThumnailImage)
            binding.tvYoutubeTitle.text = intent.getStringExtra("title")
        }

        // 여행 노트 화면에서 선택한 값을 인텐트로 받는다
        if (intent.getBooleanExtra("isListClick", false)) {
            binding.noteTitle.setText(intent.getStringExtra("noteTitle"))
            binding.noteContent.setText(intent.getStringExtra("noteContent"))
            videoId = intent.getStringExtra("videoId")
            binding.tvYoutubeTitle.text = intent.getStringExtra("youtubeTitle")
            thumnailImage = intent.getStringExtra("thumnailImage")
            Glide.with(this).load(thumnailImage).into(binding.ivThumnailImage)
            binding.btnCompleted.text = "수정"
        }
    }

    /**
     * - 버튼 이벤트 등록
     */
    override fun onResume() {
        super.onResume()

        // 유튜브 영상 검색 버튼 클릭 후 유튜브 검색 화면 이동
        binding.ivYoutube.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@NoteActivity,
                    YoutubeSearchActivity::class.java
                )
            )
        })

        // 첨부한 영상 타이틀을 클릭하면, 유튜브앱으로 영상 재생
        binding.tvYoutubeTitle.setOnClickListener(View.OnClickListener {
            if (!TextUtils.isEmpty(videoId)) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=$videoId")
                )
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@NoteActivity, "유튜브ID가 없습니다!", Toast.LENGTH_SHORT).show()
            }
        })


        // 여행 노트 저장 버튼 클릭
        binding.btnCompleted.setOnClickListener(View.OnClickListener {
            val noteTitle = binding.noteTitle.text.toString()
            val noteContent = binding.noteContent.text.toString()
            val youtubeTitle = binding.tvYoutubeTitle.text.toString()
            Log.e(TAG, "저장시 유튜브 제목 확인 !! : $youtubeTitle")

            //firebase에 노트내용 저장
            val travelItem = TravelItem()
            travelItem.noteTitle = noteTitle
            travelItem.noteContent = noteContent
            travelItem.youtubeId = videoId
            travelItem.youtubeTitle = youtubeTitle
            travelItem.youtubeThumnailImage = thumnailImage
            saveNoteItem(travelItem)
            val intent = Intent(this@NoteActivity, TripNoteActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        })
    }

    // 여행ID와 노트ID 기준으로 firebase에 여행노트 저장
    fun saveNoteItem(travelItem: TravelItem?) {
        val currentTravelId = PreferenceManager.getString(this, "currentTravelId")
        val noteId = "note-" + MainActivity.Companion.uuid
        MainActivity.Companion.databaseReference!!.child("$currentTravelId/noteData/$noteId")
            .setValue(travelItem)
            .addOnSuccessListener(
                this,
                OnSuccessListener<Void?> { Log.e(TAG, "Firebase에 여행노트가 업데이트 됨!") })
    }

    companion object {
        private const val TAG = "NoteActivity"
    }
}