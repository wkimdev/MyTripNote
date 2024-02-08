package com.wkimdev.mytripnote.model;

// 여행 노트 아이템 모델
public class NoteItem {

    private String noteId;
    private String noteTitle;
    private String noteContent;
    private String youtubeId;
    private String youtubeTitle;
    private String youtubeThumnailImage;

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getYoutubeTitle() {
        return youtubeTitle;
    }

    public void setYoutubeTitle(String youtubeTitle) {
        this.youtubeTitle = youtubeTitle;
    }

    public String getYoutubeThumnailImage() {
        return youtubeThumnailImage;
    }

    public void setYoutubeThumnailImage(String youtubeThumnailImage) {
        this.youtubeThumnailImage = youtubeThumnailImage;
    }
}
