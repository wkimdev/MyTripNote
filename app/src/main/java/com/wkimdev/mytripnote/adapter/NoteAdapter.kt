package com.wkimdev.mytripnote.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wkimdev.mytripnote.OnClickListener
import com.wkimdev.mytripnote.R
import com.wkimdev.mytripnote.adapter.NoteAdapter.NoteAdapterViewHolder
import com.wkimdev.mytripnote.model.NoteItem

/**
 * 일반 여행 노트 리스트 리사이클러뷰 어댑터
 */
class NoteAdapter(
    private val noteItems: ArrayList<NoteItem>?,
    private val context: Context,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<NoteAdapterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapterViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteAdapterViewHolder, position: Int) {
        holder.tv_note_title.text = noteItems!![position].noteTitle
        holder.tv_note_content.text = noteItems[position].noteContent
        Glide.with(context).load(noteItems[position].youtubeThumnailImage)
            .into(holder.iv_youtube_thumnail)
        holder.itemView.tag = position
        holder.layout_note.setOnClickListener(View.OnClickListener { //id를 기준으로 던져서 조회 후 호출
            onClickListener.onClickNoteList(noteItems[holder.itemView.tag as Int])
        })
    }

    override fun getItemCount(): Int {
        return noteItems?.size ?: 0
    }

    inner class NoteAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_note_title: TextView
        val tv_note_content: TextView
        val iv_youtube_thumnail: ImageView
        val layout_note: LinearLayout

        init {
            tv_note_title = itemView.findViewById(R.id.tv_note_title)
            tv_note_content = itemView.findViewById(R.id.tv_note_content)
            iv_youtube_thumnail = itemView.findViewById(R.id.iv_youtube_thumnail)
            layout_note = itemView.findViewById(R.id.layout_note)
        }
    }
}