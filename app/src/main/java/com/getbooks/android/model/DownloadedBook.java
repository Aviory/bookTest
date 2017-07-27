package com.getbooks.android.model;

import com.getbooks.android.model.enums.BookState;

/**
 * Created by marina on 26.07.17.
 */

public class DownloadedBook extends Book {

    private BookState bookState;

    @Override
    public String getBookImage() {
        return null;
    }

    @Override
    public void setBookState(BookState bookState) {
        this.bookState = bookState;
    }

    @Override
    public BookState getBookState() {
        return bookState;
    }
}
