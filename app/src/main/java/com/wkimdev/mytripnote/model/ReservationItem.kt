package com.wkimdev.mytripnote.model

// 예약 내용을 담는 모델
data class ReservationItem(var reservationId: String = "0") {
    //var reservationId: String? = null
    var placeName: String? = null
    var placeAddress: String? = null
    var reservationDate: String? = null
    var placeType //hotel or restaurant
            : String? = null
    var latLng //위치정보
            : String? = null
}