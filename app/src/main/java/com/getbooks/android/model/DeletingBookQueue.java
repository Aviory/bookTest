package com.getbooks.android.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by marinaracu on 28.08.17.
 */

public class DeletingBookQueue {

    private List<BookModel> mDeletingBookList;

    public DeletingBookQueue() {
        mDeletingBookList = new ArrayList<>();
    }

    public void addToDeletingQueue(BookModel bookModel) {
        mDeletingBookList.add(bookModel);
    }

    public void clearDeletingQueue() {
        mDeletingBookList.clear();
        mDeletingBookList = null;
    }

    public int getDeletingQueueSize() {
        return mDeletingBookList.size();
    }

    public boolean isDeletingQueueEmpty() {
        return mDeletingBookList.isEmpty();
    }

    public BookModel getBookFromDeletingQueue(int position) {
        return mDeletingBookList.get(position);
    }

    public boolean queueContainsBook(BookModel bookModel) {
        return mDeletingBookList.contains(bookModel);
    }

    public Iterator<BookModel> getIteratorQueue() {
        return mDeletingBookList.iterator();
    }

    public void removeFromDeletingQueue(BookModel bookModel) {
        mDeletingBookList.remove(bookModel);
    }
}
