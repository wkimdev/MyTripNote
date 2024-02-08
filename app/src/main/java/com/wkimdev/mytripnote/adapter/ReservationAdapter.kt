package com.wkimdev.mytripnote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wkimdev.mytripnote.OnClickListener
import com.wkimdev.mytripnote.R
import com.wkimdev.mytripnote.adapter.ReservationAdapter.ReservationAdapterViewHolder
import com.wkimdev.mytripnote.model.ReservationItem

/**
 * 예약 리스트 리사이클러뷰 어댑터
 */
class ReservationAdapter(
    private val reservationItems: ArrayList<ReservationItem>?,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<ReservationAdapterViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReservationAdapterViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_hotel_reservation, parent, false)
        return ReservationAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationAdapterViewHolder, position: Int) {
        if ("hotel" != reservationItems!![position].placeType) {
            holder.tv_reservation_date_title.text = "🍔 예약일"
            holder.tv_placeName_title.text = "식당 이름"
        }
        holder.tv_reservation_date.text = reservationItems[position].reservationDate
        holder.tv_hotel_name.text = reservationItems[position].placeName
        holder.tv_hotel_address.text = reservationItems[position].placeAddress
        holder.itemView.tag = position
        holder.layout_reservation.setOnClickListener(View.OnClickListener { //id를 기준으로 던져서 조회 후 호출
            //onClickListener.onClickListView(reservationItems.get((int)holder.itemView.getTag()).getReservationId());
            onClickListener.onClickReservationList(reservationItems[holder.itemView.tag as Int])
        })
    }

    override fun getItemCount(): Int {
        return reservationItems?.size ?: 0
    }

    inner class ReservationAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout_reservation: LinearLayout

        // 타이틀
        val tv_reservation_date_title: TextView
        val tv_placeName_title: TextView

        // 입력영역
        val tv_reservation_date: TextView
        val tv_hotel_name: TextView
        val tv_hotel_address: TextView

        init {
            layout_reservation = itemView.findViewById(R.id.layout_reservation)
            tv_reservation_date = itemView.findViewById(R.id.tv_reservation_date)
            tv_hotel_name = itemView.findViewById(R.id.tv_hotel_name)
            tv_hotel_address = itemView.findViewById(R.id.tv_hotel_address)
            tv_reservation_date_title = itemView.findViewById(R.id.tv_reservation_date_title)
            tv_placeName_title = itemView.findViewById(R.id.tv_placeName_title)
        }
    }
}