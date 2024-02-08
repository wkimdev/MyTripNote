package com.wkimdev.mytripnote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wkimdev.mytripnote.OnClickListener;
import com.wkimdev.mytripnote.R;
import com.wkimdev.mytripnote.model.TravelItem;

import java.util.ArrayList;
import java.util.Random;

/**
 * 홈 화면 여행 리스트 리사이클러뷰 어댑터
 */
public class HomeTravelListAdapter extends RecyclerView.Adapter<HomeTravelListAdapter.HomeTravelListViewHolder>{

    private ArrayList<TravelItem> travelItems;
    private OnClickListener onClickListener;
    private Context context;
    private int[] backgroundImageArray;
    private int randomNumber;

    // 어댑터 생성시 등록할 아이템 객체를 파라미터로 받는다
    public HomeTravelListAdapter(ArrayList<TravelItem> travelItems, OnClickListener onClickListener, Context context) {
        this.travelItems = travelItems;
        this.onClickListener = onClickListener;
        this.context = context;

        backgroundImageArray = new int[]{
                R.drawable.travel_background1,
                R.drawable.travel_background2,
                R.drawable.travel_background3,
                R.drawable.travel_background4};
    }

    // LayoutInflater를 통해 뷰 객체를 만들고,
    // 뷰홀더 객체를 생성하여 해당 뷰를 리턴
    @NonNull
    @Override
    public HomeTravelListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_travel, parent, false);

        return new HomeTravelListViewHolder(view);
    }

    // position에 해당하는 데이터를 뷰들에 바인딩
    @Override
    public void onBindViewHolder(@NonNull HomeTravelListViewHolder holder, int position) {
        holder.tv_travel_title.setText(travelItems.get(position).getTravelTitle());
        holder.tv_destination.setText(travelItems.get(position).getDestination());
        holder.tv_travel_date.setText(travelItems.get(position).getTravelDate());

        Random random = new Random();
        randomNumber = random.nextInt(4);
        holder.layout_card.setBackground(context.getResources().getDrawable(backgroundImageArray[randomNumber]));

        holder.itemView.setTag(position);

        int selectPosition = holder.getAdapterPosition();

        holder.tv_travel_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickTravelList(travelItems.get(selectPosition).getTravelId());
            }
        });
    }

    // 생성자를 통해 전달받은 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        return ( null != travelItems ? travelItems.size() : 0);
    }

    // 뷰홀더를 내부클래스로 선언
    public class HomeTravelListViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout_card;
        private TextView tv_travel_title;
        private TextView tv_destination;
        private TextView tv_travel_date;

        public HomeTravelListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.layout_card = itemView.findViewById(R.id.layout_card);
            this.tv_travel_title = itemView.findViewById(R.id.tv_travel_title);
            this.tv_destination = itemView.findViewById(R.id.tv_destination);
            this.tv_travel_date = itemView.findViewById(R.id.tv_travel_date);
        }
    }

}
