package com.getbooks.android.model;

import com.getbooks.android.model.enums.BookState;

/**
 * Created by marina on 27.07.17.
 */

public abstract class Book {

    public abstract String getBookImage();

    public abstract void setBookState(BookState bookState);

    public abstract BookState getBookState();
}
