package com.getbooks.android.model;

/**
 * Created by marinaracu on 03.09.17.
 */

public class BookMarkItemModel {

    private String bookChapter;
    private String date;
    private String highlightContent;
    private int viewId;
    private Object pageInformation;
    private int removeImagePosition;

    public BookMarkItemModel(String bookChapter, String date, String highlightContent, int viewId, Object pageInformation) {
        this.bookChapter = bookChapter;
        this.date = date;
        this.highlightContent = highlightContent;
        this.viewId = viewId;
        this.pageInformation = pageInformation;
    }

    public String getBookChapter() {
        return bookChapter;
    }

    public void setBookChapter(String bookChapter) {
        this.bookChapter = bookChapter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHighlightContent() {
        return highlightContent;
    }

    public void setHighlightContent(String highlightContent) {
        this.highlightContent = highlightContent;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public Object getPageInformation() {
        return pageInformation;
    }

    public void setPageInformation(Object pageInformation) {
        this.pageInformation = pageInformation;
    }

    public int getRemoveImagePosition() {
        return removeImagePosition;
    }

    public void setRemoveImagePosition(int removeImagePosition) {
        this.removeImagePosition = removeImagePosition;
    }
}
