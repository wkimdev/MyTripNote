package com.wkimdev.mytripnote.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wkimdev.mytripnote.OnClickListener
import com.wkimdev.mytripnote.R
import com.wkimdev.mytripnote.adapter.HomeTravelListAdapter.HomeTravelListViewHolder
import com.wkimdev.mytripnote.model.TravelItem
import java.util.*

/**
 * 홈 화면 여행 리스트 리사이클러뷰 어댑터
 */
class HomeTravelListAdapter(
    private val travelItems: ArrayList<TravelItem>?,
    private val onClickListener: OnClickListener,
    private val context: Context
) : RecyclerView.Adapter<HomeTravelListViewHolder>() {
    private val backgroundImageArray: IntArray
    private var randomNumber = 0

    // 어댑터 생성시 등록할 아이템 객체를 파라미터로 받는다
    init {
        backgroundImageArray = intArrayOf(
            R.drawable.travel_background1,
            R.drawable.travel_background2,
            R.drawable.travel_background3,
            R.drawable.travel_background4
        )
    }

    // LayoutInflater를 통해 뷰 객체를 만들고,
    // 뷰홀더 객체를 생성하여 해당 뷰를 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeTravelListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_travel, parent, false)
        return HomeTravelListViewHolder(view)
    }

    // position에 해당하는 데이터를 뷰들에 바인딩
    override fun onBindViewHolder(holder: HomeTravelListViewHolder, position: Int) {
        holder.tv_travel_title.text = travelItems!![position].travelTitle
        holder.tv_destination.text = travelItems[position].destination
        holder.tv_travel_date.text = travelItems[position].travelDate
        val random = Random()
        randomNumber = random.nextInt(4)
        holder.layout_card.background =
            context.resources.getDrawable(backgroundImageArray[randomNumber])
        holder.itemView.tag = position
        val selectPosition = holder.adapterPosition
        holder.tv_travel_title.setOnClickListener(View.OnClickListener {
            onClickListener.onClickTravelList(
                travelItems[selectPosition].travelId
            )
        })
    }

    // 생성자를 통해 전달받은 전체 데이터 갯수 리턴
    override fun getItemCount(): Int {
        return travelItems?.size ?: 0
    }

    // 뷰홀더를 내부클래스로 선언
    inner class HomeTravelListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout_card: LinearLayout
        val tv_travel_title: TextView
        val tv_destination: TextView
        val tv_travel_date: TextView

        init {
            layout_card = itemView.findViewById(R.id.layout_card)
            tv_travel_title = itemView.findViewById(R.id.tv_travel_title)
            tv_destination = itemView.findViewById(R.id.tv_destination)
            tv_travel_date = itemView.findViewById(R.id.tv_travel_date)
        }
    }
}