package com.wkimdev.mytripnote.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wkimdev.mytripnote.OnClickListener;
import com.wkimdev.mytripnote.R;
import com.wkimdev.mytripnote.model.SearchData;

import java.util.ArrayList;

/**
 * 유튜브 리스트를 그리는 어댑터
 */
public class YoutubeListAdapter extends RecyclerView.Adapter<YoutubeListAdapter.YoutubeListAdapterViewHolder> {

    private static final String TAG = "YoutubeListAdapter";
    private ArrayList<SearchData> searchResultData;
    private Context context;
    private OnClickListener onClickListener;

    public YoutubeListAdapter(ArrayList<SearchData> searchResultData,
                              Context context, OnClickListener onClickListener) {
        this.searchResultData = searchResultData;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public YoutubeListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_youtube, parent, false);

        return new YoutubeListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubeListAdapterViewHolder holder, int position) {
        String thumnailImageUrl = searchResultData.get(position).getThumnailImage();
        Glide.with(context).load(thumnailImageUrl).into(holder.iv_thumnailImage);

        holder.tv_youtubeTitle.setText(searchResultData.get(position).getTitle());
        holder.itemView.setTag(position);

        holder.tv_youtubeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 작성하던 노트에, 선택한 유튜브 영상에 대한 정보를 전달
                onClickListener.onClickYoutubeList(searchResultData.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != searchResultData ? searchResultData.size() : 0);
    }

    public class YoutubeListAdapterViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_thumnailImage;
        private TextView tv_youtubeTitle;

        public YoutubeListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            this.iv_thumnailImage = itemView.findViewById(R.id.iv_thumnailImage);
            this.tv_youtubeTitle = itemView.findViewById(R.id.tv_youtubeTitle);
        }
    }


}
