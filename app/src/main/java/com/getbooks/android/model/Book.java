package com.getbooks.android.model;

import com.getbooks.android.model.enums.BookState;

/**
 * Created by marina on 27.07.17.
 */

public abstract class Book {

    private int mProgress = 0;
    private int mViewPosition = 0;
    private boolean mIsBookFirstOpen;

    public abstract String getBookImage();

    public abstract void setBookState(BookState bookState);

    public abstract BookState getBookState();

    public abstract String getBookDownloadLink();

    public abstract String getBookName();

    public int getProgress() {
        return mProgress;
    }

    public void setViewPosition(int viewPosition) {
        this.mViewPosition = viewPosition;
    }

    public int getViewPosition() {
        return mViewPosition;
    }

    public boolean isIsBookFirstOpen() {
        return mIsBookFirstOpen;
    }

    public void setIsBookFirstOpen(boolean mIsBookFirstOpen) {
        this.mIsBookFirstOpen = mIsBookFirstOpen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;

        Book book = (Book) o;

        if (mProgress != book.mProgress) return false;
        if (mViewPosition != book.mViewPosition) return false;
        return mIsBookFirstOpen == book.mIsBookFirstOpen;

    }

    @Override
    public int hashCode() {
        int result = mProgress;
        result = 31 * result + mViewPosition;
        result = 31 * result + (mIsBookFirstOpen ? 1 : 0);
        return result;
    }
}
