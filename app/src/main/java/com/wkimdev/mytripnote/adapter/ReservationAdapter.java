package com.wkimdev.mytripnote.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wkimdev.mytripnote.OnClickListener;
import com.wkimdev.mytripnote.R;
import com.wkimdev.mytripnote.model.ReservationItem;

import java.util.ArrayList;

/**
 * 예약 리스트 리사이클러뷰 어댑터
 */
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationAdapterViewHolder>{

    private ArrayList<ReservationItem> reservationItems;
    private OnClickListener onClickListener;

    public ReservationAdapter(ArrayList<ReservationItem> reservationItems, OnClickListener onClickListener) {
        this.reservationItems = reservationItems;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ReservationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_hotel_reservation, parent, false);

        return new ReservationAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationAdapterViewHolder holder, int position) {

        if (!"hotel".equals(reservationItems.get(position).getPlaceType())) {
            holder.tv_reservation_date_title.setText("🍔 예약일");
            holder.tv_placeName_title.setText("식당 이름");
        }

        holder.tv_reservation_date.setText(reservationItems.get(position).getReservationDate());
        holder.tv_hotel_name.setText(reservationItems.get(position).getPlaceName());
        holder.tv_hotel_address.setText(reservationItems.get(position).getPlaceAddress());
        holder.itemView.setTag(position);

        holder.layout_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id를 기준으로 던져서 조회 후 호출
                //onClickListener.onClickListView(reservationItems.get((int)holder.itemView.getTag()).getReservationId());
                onClickListener.onClickReservationList(reservationItems.get((int)holder.itemView.getTag()));

            }
        });

    }

    @Override
    public int getItemCount() {
        return ( null != reservationItems ? reservationItems.size() : 0);
    }

    class ReservationAdapterViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layout_reservation;
        // 타이틀
        private TextView tv_reservation_date_title;
        private TextView tv_placeName_title;

        // 입력영역
        private TextView tv_reservation_date;
        private TextView tv_hotel_name;
        private TextView tv_hotel_address;

        public ReservationAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            this.layout_reservation = itemView.findViewById(R.id.layout_reservation);
            this.tv_reservation_date = itemView.findViewById(R.id.tv_reservation_date);
            this.tv_hotel_name = itemView.findViewById(R.id.tv_hotel_name);
            this.tv_hotel_address = itemView.findViewById(R.id.tv_hotel_address);
            this.tv_reservation_date_title = itemView.findViewById(R.id.tv_reservation_date_title);
            this.tv_placeName_title = itemView.findViewById(R.id.tv_placeName_title);
        }
    }

}
