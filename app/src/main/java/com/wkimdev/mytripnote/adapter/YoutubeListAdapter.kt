package com.wkimdev.mytripnote.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wkimdev.mytripnote.OnClickListener
import com.wkimdev.mytripnote.R
import com.wkimdev.mytripnote.adapter.YoutubeListAdapter.YoutubeListAdapterViewHolder
import com.wkimdev.mytripnote.databinding.ItemYoutubeBinding
import com.wkimdev.mytripnote.model.SearchData

/**
 * 유튜브 리스트를 그리는 어댑터
 *
 * OnClickListener의 경우, 인터페이스 대신 Kotlin's higher-order function 를 사용해 람다식으로 변경
 *  이를 통해 클릭이벤트 처리를 더 간소화 했다
 *
 */
class YoutubeListAdapter(
    private val searchResultData: List<SearchData>, // 널 가능성 제거
    private val context: Context,
    private val onClickListener: (SearchData) -> Unit // 람다식 사용
) : RecyclerView.Adapter<YoutubeListAdapter.YoutubeListAdapterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeListAdapterViewHolder {
        // View Binding 사용
        // ItemYoutubeBinding 를 사용해 각뷰의 인스턴스를 직접 찾는대신, 바인딩 클래스에서 제공하는 프로퍼티에 접근
        val binding = ItemYoutubeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return YoutubeListAdapterViewHolder(binding)
    }


    override fun onBindViewHolder(holder: YoutubeListAdapterViewHolder, position: Int) {
        val searchData = searchResultData[position]
        with(holder.binding) {
            //SearchData 클래스의 thumnailImage 속성이 주어지면
            //getThumnailImage()를 사용하지 않고 Kotlin에서 속성 이름으로 직접 액세스한다
            //searchData.getThumnailImage() X ->
            Glide.with(context).load(searchData.thumnailImage).into(ivThumnailImage)
            tvYoutubeTitle.text = searchData.title
            root.setOnClickListener { // 람다식 사용
                onClickListener.invoke(searchData)
            }
        }
    }


    override fun getItemCount(): Int = searchResultData.size

    /**
     * binding을 이용한 view holder class
     * @param ItemYoutubeBinding - 각뷰의 인스턴스를 직접 찾는대신, 바인딩 클래스에서 제공하는 프로퍼티에 접근한다.
     *  이 방식을 사용해 타입 안정성을 보장하고, 코드를 더 간결하게 만든다
     *
     * */
    inner class YoutubeListAdapterViewHolder(val binding: ItemYoutubeBinding) : RecyclerView.ViewHolder(binding.root)

    /*inner class YoutubeListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_thumnailImage: ImageView
        val tv_youtubeTitle: TextView

        init {
            iv_thumnailImage = itemView.findViewById(R.id.iv_thumnailImage)
            tv_youtubeTitle = itemView.findViewById(R.id.tv_youtubeTitle)
        }
    }*/

    companion object {
        private const val TAG = "YoutubeListAdapter"
    }
}