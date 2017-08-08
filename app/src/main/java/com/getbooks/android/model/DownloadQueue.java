package com.getbooks.android.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by marina on 08.08.17.
 */

public class DownloadQueue {

    private Map<Integer, Book> downloadQueue;

    public DownloadQueue(){
        downloadQueue = new HashMap<>();
    }

    public void addToDownloadQueue(Integer viewId, Book book){
        downloadQueue.put(viewId, book);
    }

    public void clearDownloadQueue(){
        downloadQueue.clear();
        downloadQueue = null;
    }

    public int getDownloadQueueSize(){
        return downloadQueue.size();
    }

    public boolean isDownloadQueueEmpty(){
        return downloadQueue.isEmpty();
    }

    public Book getBookFromDownloadQueue(int position){
       return downloadQueue.get(position);
    }

    public Set<Integer> getSetViewId(){
        return downloadQueue.keySet();
    }
}
