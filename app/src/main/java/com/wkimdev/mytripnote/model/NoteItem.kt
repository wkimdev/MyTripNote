package com.wkimdev.mytripnote.model

/**
 *
- 여행 노트 아이템 모델
코틀린의 DTO
공홈설명 - https://kotlinlang.org/docs/data-classes.html
Kotlin의 데이터 클래스는 주로 데이터를 보관하는 데 사용된다
기본 생성자에는 매개변수가 하나 이상 있어야 합니다.= primary consturctor에 1개 이상의 파라미터가 존재해야합니다.
data class는 일반 class 앞에 data syntax를 붙이는 것만으로 생성이 되는 클래스이다.

*/
//data class User(val name: String = "", val age: Int = 0)
data class NoteItem(var noteId: String = "0") {
    //var noteId: String? = null
    var noteTitle: String? = null
    var noteContent: String? = null
    var youtubeId: String? = null
    var youtubeTitle: String? = null
    var youtubeThumnailImage: String? = null
}