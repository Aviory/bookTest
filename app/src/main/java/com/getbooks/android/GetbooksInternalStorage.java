package com.getbooks.android;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by avi on 01.09.17.
 */

public class GetbooksInternalStorage extends AsyncTask<Void, Void, List<File>>{


    @Override
    protected List<File> doInBackground(Void... voids) {
        List<File> fileList = new LinkedList<>();
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".epub");
            }
        };

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles(filter);
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                fileList.add(files[i]);
            }
            Log.d("StorageDirectory ", "Size: "+ files.length);
        }

        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        Log.d("Files", "Path: " + path);
        directory = new File(path);
        files = directory.listFiles(filter);
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                fileList.add(files[i]);
            }
            Log.d("PublicDawnloads size: ", String.valueOf(files.length));
        }

        return fileList;
    }
}//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//file type epub

//    GetbooksInternalStorage fileManager = new GetbooksInternalStorage();
//        fileManager.execute();
//        try {
//            List<File> mInternalLibrary = fileManager.get(3, TimeUnit.SECONDS);
//            Log.d("Files in ui size: ", String.valueOf(mInternalLibrary.size()));
//            if(mLibrary!=null){
//                for (File file:mInternalLibrary) {
//                    BookModel tmp = new BookModel();
//                    tmp.setBookName(file.getName());
//                    tmp.setBookState(BookState.INTERNAL_BOOK.getState());
//                    mLibrary.add(tmp);
//                }
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }