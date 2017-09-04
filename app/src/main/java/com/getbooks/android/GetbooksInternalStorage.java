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

        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        Log.d("Files", "Path: " + path);
        directory = new File(path);
        files = directory.listFiles(filter);
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                fileList.add(files[i]);
            }
            Log.d("PublicDirectory size: ", String.valueOf(files.length));
        }

        return fileList;
    }
}
//file type epub