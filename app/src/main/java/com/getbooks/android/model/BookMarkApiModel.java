package com.getbooks.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by marinaracu on 07.09.17.
 */

public class BookMarkApiModel {
    @SerializedName("bookmark_id")
    @Expose
    private Integer bookmarkId;
    @SerializedName("bookmark_label")
    @Expose
    private String bookmarkLabel;
    @SerializedName("bookmark_page")
    @Expose
    private Integer bookmarkPage;
    @SerializedName("bookmark_text")
    @Expose
    private String bookmarkText;

    public Integer getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(Integer bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public String getBookmarkLabel() {
        return bookmarkLabel;
    }

    public void setBookmarkLabel(String bookmarkLabel) {
        this.bookmarkLabel = bookmarkLabel;
    }

    public Integer getBookmarkPage() {
        return bookmarkPage;
    }

    public void setBookmarkPage(Integer bookmarkPage) {
        this.bookmarkPage = bookmarkPage;
    }

    public String getBookmarkText() {
        return bookmarkText;
    }

    public void setBookmarkText(String bookmarkText) {
        this.bookmarkText = bookmarkText;
    }

}
