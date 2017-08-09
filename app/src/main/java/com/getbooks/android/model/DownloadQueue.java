package com.getbooks.android.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by marina on 08.08.17.
 */

public class DownloadQueue {

    private List<Book> downloadQueue;

    public DownloadQueue() {
        downloadQueue = new ArrayList<>();
    }

    public void addToDownloadQueue(Book book) {
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

    public Book getBookFromDownloadQueue(int position) {
        return downloadQueue.get(position);
    }

    public boolean queueContainsBook(Book book){
        return downloadQueue.contains(book);
    }

    public Iterator<Book> getIteratorQueue(){
        return downloadQueue.iterator();
    }

    public void removeFromDownloadQueue(Book book){
        downloadQueue.remove(book);
    }
}
