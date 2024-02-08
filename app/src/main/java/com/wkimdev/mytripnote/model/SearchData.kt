package com.wkimdev.mytripnote.model

// 유튜브 검색 후 결과값을 담는 모델
data class SearchData(var videoId: String, var title: String, var thumnailImage: String) {
    var publishDate: String? = null

}