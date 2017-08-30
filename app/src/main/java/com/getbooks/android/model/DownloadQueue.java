package com.getbooks.android.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by marina on 08.08.17.
 */

public class DownloadQueue {

    private List<BookModel> downloadQueue;

    public DownloadQueue() {
        downloadQueue = new ArrayList<>();
    }

    public void addToDownloadQueue(BookModel bookModel) {
        downloadQueue.add(bookModel);
    }

    public void clearDownloadQueue() {
        downloadQueue.clear();
        downloadQueue = null;
    }

    public int getDownloadQueueSize() {
        return downloadQueue.size();
    }

    public boolean isDownloadQueueEmpty() {
        return downloadQueue.isEmpty();
    }

    public BookModel getBookFromDownloadQueue(int position) {
        return downloadQueue.get(position);
    }

    public boolean queueContainsBook(BookModel bookModel){
        return downloadQueue.contains(bookModel);
    }

    public Iterator<BookModel> getIteratorQueue(){
        return downloadQueue.iterator();
    }

    public void removeFromDownloadQueue(BookModel bookModel){
        downloadQueue.remove(bookModel);
    }
}
