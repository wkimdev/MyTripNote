package com.wkimdev.mytripnote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import com.google.api.client.json.JsonFactory;
import com.wkimdev.mytripnote.adapter.YoutubeListAdapter;
import com.wkimdev.mytripnote.config.GlobalApplication;
import com.wkimdev.mytripnote.model.NoteItem;
import com.wkimdev.mytripnote.model.ReservationItem;
import com.wkimdev.mytripnote.model.SearchData;
import com.wkimdev.mytripnote.model.TravelItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 유튜브 API 연동 구현 화면
 * 유튜브 영상 검색 후 응답결과 리턴
 */
public class YoutubeSearchActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "YoutubeSearchActivity";

    private EditText editText_search; // 검색어 입력뷰
    private Button btn_search_q; // 검색어 버튼뷰

    private RecyclerView rv_youtubeList; // 유트브 결과를 그리는 리사이클러뷰
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<SearchData> searchResultData = new ArrayList<SearchData>(); // 유튜브 검색 결과값을 담음


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search);

        editText_search = findViewById(R.id.editText_search);
        btn_search_q = findViewById(R.id.btn_search_q);
        rv_youtubeList = findViewById(R.id.rv_youtubeList);
        linearLayoutManager = new LinearLayoutManager(this);


        // 검색어 입력 후 검색 버튼 클릭
        btn_search_q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResultData.clear();
                // Youtube에 검색 결과 요청
                YoutubeAsyncTask youtubeAsyncTask = new YoutubeAsyncTask();
                youtubeAsyncTask.execute();
            }
        });
    }

    // 유튜브 결과리스트 어뎁터에서 받은 클릭 이벤트
    @Override
    public void onClickYoutubeList(SearchData searchData) {
        Log.e(TAG, "onClickYoutubeList: SearchData 요청을 받음!!!! ");

        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("videoId", searchData.getVideoId());
        intent.putExtra("thumnailImage", searchData.getThumnailImage());
        intent.putExtra("title", searchData.getTitle());
        startActivity(intent);
        finish();
    }

    @Override
    public void onClickReservationList(ReservationItem reservationItem) {

    }

    @Override
    public void onClickNoteList(NoteItem noteItem) {

    }

    @Override
    public void onClickTravelList(String travelId) {

    }


    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class YoutubeAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Background task to call YouTube Data API.
         */
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // 구글에서 만든 https, json 등의 통신방식 활용
                HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
                final JsonFactory JSON_FACTORY = new JacksonFactory();
                final long NUMBER_OF_VIDEOS_RETURNED = 10;

                // API 요청을 만들기 위해 Youtube 객체를 생성한다
                YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("youtube-search-sample").build();

                // API 요청에 지정된 쿼리 매개변수와 일치하는 검색결과의 모음을 반환
                // 유튜브 data api 중 검색 이용
                // 필수 매개변수로 id,snippet 값을 넣어야 한다.
                YouTube.Search.List search = youtube.search().list("id,snippet");

                search.setKey(GlobalApplication.GOOGLE_API_KEY);

                // 검색어 입력
                // Log.e(TAG, "doInBackground: 검색어 >>>>> "+ editText_search.getText().toString());
                search.setQ(editText_search.getText().toString());

                search.setOrder("relevance"); //date relevance - 검색 쿼리에 대한 관련성을 기준으로 리소스를 정렬
                search.setType("video"); // 특정 리소스 유형만 검색되도록 검색 쿼리 제한

                //반환된 정보를 우리가 필요로 하는 필드로만 줄이고 더 많이 호출합니다.
                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                // 검색결과 리턴
                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResultList = searchResponse.getItems();

                if (searchResultList != null) {
                    // 결과값을 바인딩 하는 메소드
                    prettyPrint(searchResultList.iterator());
                }
            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
                System.err.println("There was a service error 2: " + e.getLocalizedMessage() + " , " + e.toString());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return null;
        }

        // 검색어 결과값을 받아, 유튜브어뎁터에 요청해 리스트뷰를 그린다
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            rv_youtubeList.setLayoutManager(linearLayoutManager);
            YoutubeListAdapter youtubeListAdapter = new YoutubeListAdapter(searchResultData,
                    YoutubeSearchActivity.this, YoutubeSearchActivity.this);
            rv_youtubeList.setAdapter(youtubeListAdapter);
            youtubeListAdapter.notifyDataSetChanged();

        }

        // Iterator의 모든 SearchResult를 출력합니다. 인쇄된 각 줄에는 제목 및 썸네일이 포함
        public void prettyPrint(Iterator<SearchResult> iteratorSearchResults) {
            if (!iteratorSearchResults.hasNext()) {
                System.out.println(" There aren't any results for your query.");
            }

            while (iteratorSearchResults.hasNext()) {
                SearchResult singleVideo = iteratorSearchResults.next();
                ResourceId rId = singleVideo.getId();

                // Double checks the kind is video.
                if (rId.getKind().equals("youtube#video")) {
                    Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");

                    // 검색결과를 모델에 담음
                    searchResultData.add(new SearchData(rId.getVideoId(),
                            singleVideo.getSnippet().getTitle(), thumbnail.getUrl()));
                }
            }
        }
    }

}