package com.wkimdev.mytripnote.model;

// 유튜브 검색 후 결과값을 담는 모델
public class SearchData {

    public String videoId;
    public String title;
    public String publishDate;
    public String thumnailImage;

    public SearchData(String videoId, String title, String thumnailImage) {
        this.videoId = videoId;
        this.title = title;
        this.thumnailImage = thumnailImage;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getThumnailImage() {
        return thumnailImage;
    }

    public void setThumnailImage(String thumnailImage) {
        this.thumnailImage = thumnailImage;
    }
}
