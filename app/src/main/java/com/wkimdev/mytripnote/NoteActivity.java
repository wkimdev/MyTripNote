package com.wkimdev.mytripnote;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.wkimdev.mytripnote.config.PreferenceManager;
import com.wkimdev.mytripnote.model.TravelItem;

/**
 * 유튜브 첨부가능한 일반 여행 노트 화면
 */
public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "NoteActivity";
    private ImageView iv_youtube;
    private ImageView iv_thumnailImage;
    private TextView tv_youtubeTitle;
    private String videoId;
    private String thumnailImage;
    private Button btn_completed;
    private EditText note_title;
    private EditText note_content;

    /**
     *  - 뷰 선언
     *  - 유튜브리스트 화면으로 부터 인텐트값을 받도록 처리
     *  - 여행노트 화면으로 부터 인텐트값을 받도록 처리
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        iv_youtube = findViewById(R.id.iv_youtube);
        iv_thumnailImage = findViewById(R.id.iv_thumnailImage);
        tv_youtubeTitle = findViewById(R.id.tv_youtubeTitle);
        btn_completed = findViewById(R.id.btn_completed);
        note_title = findViewById(R.id.note_title);
        note_content = findViewById(R.id.note_content);


        // 유튜브 영상 리스트에서 선택한 값을 인텐트로 받는다
        Intent intent = getIntent();
        if (!TextUtils.isEmpty(intent.getStringExtra("videoId"))) {
            videoId = intent.getStringExtra("videoId");
            thumnailImage = intent.getStringExtra("thumnailImage");
            Glide.with(this).load(thumnailImage).into(iv_thumnailImage);
            tv_youtubeTitle.setText(intent.getStringExtra("title"));
        }

        // 여행 노트 화면에서 선택한 값을 인텐트로 받는다
        if (intent.getBooleanExtra("isListClick", false)) {
            note_title.setText(intent.getStringExtra("noteTitle"));
            note_content.setText(intent.getStringExtra("noteContent"));

            videoId = intent.getStringExtra("videoId");
            tv_youtubeTitle.setText(intent.getStringExtra("youtubeTitle"));
            thumnailImage = intent.getStringExtra("thumnailImage");
            Glide.with(this).load(thumnailImage).into(iv_thumnailImage);
            btn_completed.setText("수정");
        }
    }

    /**
     * - 버튼 이벤트 등록
     * */
    @Override
    protected void onResume() {
        super.onResume();

        // 유튜브 영상 검색 버튼 클릭 후 유튜브 검색 화면 이동
        iv_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoteActivity.this, YoutubeSearchActivity.class));
            }
        });

        // 첨부한 영상 타이틀을 클릭하면, 유튜브앱으로 영상 재생
        tv_youtubeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(videoId)) {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse( "https://www.youtube.com/watch?v=" + videoId ));
                    startActivity( intent );
                    finish();
                } else {
                    Toast.makeText(NoteActivity.this, "유튜브ID가 없습니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 여행 노트 저장 버튼 클릭
        btn_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String noteTitle = note_title.getText().toString();
                String noteContent = note_content.getText().toString();
                String youtubeTitle = tv_youtubeTitle.getText().toString();
                Log.e(TAG, "저장시 유튜브 제목 확인 !! : "+ youtubeTitle);

                //firebase에 노트내용 저장
                TravelItem travelItem = new TravelItem();
                travelItem.setNoteTitle(noteTitle);
                travelItem.setNoteContent(noteContent);
                travelItem.setYoutubeId(videoId);
                travelItem.setYoutubeTitle(youtubeTitle);
                travelItem.setYoutubeThumnailImage(thumnailImage);
                saveNoteItem(travelItem);

                Intent intent = new Intent(NoteActivity.this, TripNoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    // 여행ID와 노트ID 기준으로 firebase에 여행노트 저장
    public void saveNoteItem(TravelItem travelItem) {

        String currentTravelId = PreferenceManager.getString(this, "currentTravelId");
        String noteId = "note-" + MainActivity_bakk.getUuid();

        MainActivity_bakk.databaseReference.child(currentTravelId + "/noteData/" + noteId)
                .setValue(travelItem)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "Firebase에 여행노트가 업데이트 됨!");
                    }
                });

    }

}