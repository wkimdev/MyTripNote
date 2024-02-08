package com.wkimdev.mytripnote.model

// 여행 노트들에 대한 정보를 담는 모델
data class TravelItem(var travelId: String = "0") {
    //var travelId: String? = null
    var destination: String? = null
    var travelDate: String? = null
    var travelTitle: String? = null
    var travelType // 국내/해외
            : String? = null

    // 여행노트 내용
    var noteTitle: String? = null
    var noteContent: String? = null
    var youtubeId: String? = null
    var youtubeTitle: String? = null
    var youtubeThumnailImage: String? = null

    // 예약내용
    var placeName: String? = null
    var placeAddress: String? = null
    var reservationDate: String? = null
    var placeType //hotel or restaurant
            : String? = null
    var latlng: String? = null
}