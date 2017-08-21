package com.getbooks.android.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by marina on 08.08.17.
 */

public class DownloadQueue {

    private List<BookDetail> downloadQueue;

    public DownloadQueue() {
        downloadQueue = new ArrayList<>();
    }

    public void addToDownloadQueue(BookDetail book) {
        downloadQueue.add(book);
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

    public BookDetail getBookFromDownloadQueue(int position) {
        return downloadQueue.get(position);
    }

    public boolean queueContainsBook(BookDetail book){
        return downloadQueue.contains(book);
    }

    public Iterator<BookDetail> getIteratorQueue(){
        return downloadQueue.iterator();
    }

    public void removeFromDownloadQueue(BookDetail book){
        downloadQueue.remove(book);
    }
}
