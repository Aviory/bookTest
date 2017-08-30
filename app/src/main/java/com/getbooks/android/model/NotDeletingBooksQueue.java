package com.getbooks.android.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by marinaracu on 30.08.17.
 */

public class NotDeletingBooksQueue {

    private List<BookModel> mNotDeletingBookList;

    public NotDeletingBooksQueue() {
        mNotDeletingBookList = new ArrayList<>();
    }

    public void addToQueue(BookModel bookModel) {
        mNotDeletingBookList.add(bookModel);
    }

    public void clearQueue() {
        if (mNotDeletingBookList == null) return;
        mNotDeletingBookList.clear();
    }

    public int getQueueSize() {
        return mNotDeletingBookList.size();
    }

    public boolean isQueueEmpty() {
        if (mNotDeletingBookList == null) return true;
        return mNotDeletingBookList.isEmpty();
    }

    public BookModel getBookQueue(int position) {
        return mNotDeletingBookList.get(position);
    }

    public boolean queueContainsBook(BookModel bookModel) {
        return mNotDeletingBookList.contains(bookModel);
    }

    public Iterator<BookModel> getIteratorQueue() {
        return mNotDeletingBookList.iterator();
    }

    public void removeBookFromQueue(BookModel bookModel) {
        if (mNotDeletingBookList == null) return;
        mNotDeletingBookList.remove(bookModel);
    }

    public List<BookModel> getAllBooksQueue() {
        List<BookModel> allBooks = new ArrayList<>();
        allBooks.addAll(mNotDeletingBookList);
        return allBooks;
    }
}
