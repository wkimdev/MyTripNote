package com.wkimdev.mytripnote

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.Thumbnail
import com.wkimdev.mytripnote.adapter.YoutubeListAdapter
import com.wkimdev.mytripnote.config.GlobalApplication
import com.wkimdev.mytripnote.databinding.ActivityHotelSearchBinding
import com.wkimdev.mytripnote.databinding.ActivityYoutubeSearchBinding
import com.wkimdev.mytripnote.model.NoteItem
import com.wkimdev.mytripnote.model.ReservationItem
import com.wkimdev.mytripnote.model.SearchData
import java.io.IOException

/**
 * 유튜브 API 연동 구현 화면
 * 유튜브 영상 검색 후 응답결과 리턴
 */
class YoutubeSearchActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityYoutubeSearchBinding

//    private var editText_search // 검색어 입력뷰
//            : EditText? = null
//    private var btn_search_q // 검색어 버튼뷰
//            : Button? = null
//    private var rv_youtubeList // 유트브 결과를 그리는 리사이클러뷰
//            : RecyclerView? = null

    private var linearLayoutManager: LinearLayoutManager? = null
    private val searchResultData = ArrayList<SearchData>() // 유튜브 검색 결과값을 담음


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_search)

        binding = ActivityYoutubeSearchBinding.inflate(layoutInflater)

        linearLayoutManager = LinearLayoutManager(this)


        // 검색어 입력 후 검색 버튼 클릭
        binding.btnSearchQ.setOnClickListener(View.OnClickListener {
            searchResultData.clear()
            // Youtube에 검색 결과 요청
            val youtubeAsyncTask = YoutubeAsyncTask()
            youtubeAsyncTask.execute()
        })
    }

    // 유튜브 결과리스트 어뎁터에서 받은 클릭 이벤트
    override fun onClickYoutubeList(searchData: SearchData) {
        Log.e(TAG, "onClickYoutubeList: SearchData 요청을 받음!!!! ")
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra("videoId", searchData.videoId)
        intent.putExtra("thumnailImage", searchData.thumnailImage)
        intent.putExtra("title", searchData.title)
        startActivity(intent)
        finish()
    }

    override fun onClickReservationList(reservationItem: ReservationItem) {}
    override fun onClickNoteList(noteItem: NoteItem) {}
    override fun onClickTravelList(travelId: String?) {}

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     *
     * AsyncTask는 API 레벨 30 이후 deprecated!
     * 대신 코틀린에선 코루틴이란 비동기처리 방식을 사용한다.
     */
    private inner class YoutubeAsyncTask : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        /**
         * Background task to call YouTube Data API.
         */
        protected override fun doInBackground(vararg voids: Void): Void? {
            try {
                // 구글에서 만든 https, json 등의 통신방식 활용
                val HTTP_TRANSPORT: HttpTransport = NetHttpTransport()
                val JSON_FACTORY: JsonFactory = JacksonFactory()
                val NUMBER_OF_VIDEOS_RETURNED: Long = 10

                // API 요청을 만들기 위해 Youtube 객체를 생성한다
                val youtube =
                    YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, HttpRequestInitializer { })
                        .setApplicationName("youtube-search-sample").build()

                // API 요청에 지정된 쿼리 매개변수와 일치하는 검색결과의 모음을 반환
                // 유튜브 data api 중 검색 이용
                // 필수 매개변수로 id,snippet 값을 넣어야 한다.
                val search = youtube.search().list("id,snippet")
                search.key = GlobalApplication.Companion.GOOGLE_API_KEY

                // 검색어 입력
                // Log.e(TAG, "doInBackground: 검색어 >>>>> "+ editText_search.getText().toString());
                search.q = editText_search!!.text.toString()
                search.order = "relevance" //date relevance - 검색 쿼리에 대한 관련성을 기준으로 리소스를 정렬
                search.type = "video" // 특정 리소스 유형만 검색되도록 검색 쿼리 제한

                //반환된 정보를 우리가 필요로 하는 필드로만 줄이고 더 많이 호출합니다.
                search.fields =
                    "items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)"
                search.maxResults = NUMBER_OF_VIDEOS_RETURNED

                // 검색결과 리턴
                val searchResponse = search.execute()
                val searchResultList = searchResponse.items
                if (searchResultList != null) {
                    // 결과값을 바인딩 하는 메소드
                    prettyPrint(searchResultList.iterator())
                }
            } catch (e: GoogleJsonResponseException) {
                System.err.println(
                    "There was a service error: " + e.details.code + " : "
                            + e.details.message
                )
                System.err.println("There was a service error 2: " + e.localizedMessage + " , " + e.toString())
            } catch (e: IOException) {
                System.err.println("There was an IO error: " + e.cause + " : " + e.message)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            return null
        }

        // 검색어 결과값을 받아, 유튜브어뎁터에 요청해 리스트뷰를 그린다
        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            rv_youtubeList!!.layoutManager = linearLayoutManager
            val youtubeListAdapter = YoutubeListAdapter(
                searchResultData,
                this@YoutubeSearchActivity, this@YoutubeSearchActivity
            )
            rv_youtubeList!!.adapter = youtubeListAdapter
            youtubeListAdapter.notifyDataSetChanged()
        }

        // Iterator의 모든 SearchResult를 출력합니다. 인쇄된 각 줄에는 제목 및 썸네일이 포함
        fun prettyPrint(iteratorSearchResults: Iterator<SearchResult>) {
            if (!iteratorSearchResults.hasNext()) {
                println(" There aren't any results for your query.")
            }
            while (iteratorSearchResults.hasNext()) {
                val singleVideo = iteratorSearchResults.next()
                val rId = singleVideo.id

                // Double checks the kind is video.
                if (rId.kind == "youtube#video") {
                    val thumbnail = singleVideo.snippet.thumbnails["default"] as Thumbnail?

                    // 검색결과를 모델에 담음
                    searchResultData.add(
                        SearchData(
                            rId.videoId,
                            singleVideo.snippet.title, thumbnail!!.url
                        )
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "YoutubeSearchActivity"
    }
}