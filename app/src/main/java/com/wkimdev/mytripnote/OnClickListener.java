package com.wkimdev.mytripnote;

import com.wkimdev.mytripnote.model.NoteItem;
import com.wkimdev.mytripnote.model.ReservationItem;
import com.wkimdev.mytripnote.model.SearchData;

/**
 * 클릭리스너 인터페이스
 * - 리사이클러뷰 어댑터에서 클릭리스너를 사용해 이 인터페이스를 구현한 클래스에 인자값을 전달한다.
 */
public interface OnClickListener {
    void onClickYoutubeList(SearchData searchData);
    void onClickReservationList(ReservationItem reservatiionItem);
    void onClickNoteList(NoteItem noteItem);
    void onClickTravelList(String travelId);
}
