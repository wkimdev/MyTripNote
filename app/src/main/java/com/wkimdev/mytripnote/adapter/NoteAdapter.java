package com.wkimdev.mytripnote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wkimdev.mytripnote.model.NoteItem;
import com.wkimdev.mytripnote.OnClickListener;
import com.wkimdev.mytripnote.R;

import java.util.ArrayList;

/**
 * 일반 여행 노트 리스트 리사이클러뷰 어댑터
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteAdapterViewHolder>{

    private ArrayList<NoteItem> noteItems;
    private OnClickListener onClickListener;
    private Context context;

    public NoteAdapter(ArrayList<NoteItem> noteItems, Context context, OnClickListener onClickListener) {
        this.noteItems = noteItems;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public NoteAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);

        return new NoteAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapterViewHolder holder, int position) {

        holder.tv_note_title.setText(noteItems.get(position).getNoteTitle());
        holder.tv_note_content.setText(noteItems.get(position).getNoteContent());
        Glide.with(context).load(noteItems.get(position).getYoutubeThumnailImage()).into(holder.iv_youtube_thumnail);

        holder.itemView.setTag(position);

        holder.layout_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id를 기준으로 던져서 조회 후 호출
                onClickListener.onClickNoteList(noteItems.get((int)holder.itemView.getTag()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return ( null != noteItems ? noteItems.size() : 0);
    }

    class NoteAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_note_title;
        private TextView tv_note_content;
        private ImageView iv_youtube_thumnail;
        private LinearLayout layout_note;

        public NoteAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_note_title = itemView.findViewById(R.id.tv_note_title);
            this.tv_note_content = itemView.findViewById(R.id.tv_note_content);
            this.iv_youtube_thumnail = itemView.findViewById(R.id.iv_youtube_thumnail);
            this.layout_note = itemView.findViewById(R.id.layout_note);
        }
    }

}
