package com.wkimdev.mytripnote

import com.wkimdev.mytripnote.model.NoteItem
import com.wkimdev.mytripnote.model.ReservationItem
import com.wkimdev.mytripnote.model.SearchData

/**
 * 클릭리스너 인터페이스
 * - 리사이클러뷰 어댑터에서 클릭리스너를 사용해 이 인터페이스를 구현한 클래스에 인자값을 전달한다.
 */
interface OnClickListener {
    fun onClickYoutubeList(searchData: SearchData)
    fun onClickReservationList(reservatiionItem: ReservationItem)
    fun onClickNoteList(noteItem: NoteItem)
    fun onClickTravelList(travelId: String?) //null이 들어갈 수 있다.
}