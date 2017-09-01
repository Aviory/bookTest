package com.getbooks.android;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by avi on 01.09.17.
 */

public class FileManager extends AsyncTask{

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }

   public void scanFiles(){
//       List<String> paths = new ArrayList<String>();
//       File directory = new File("/mnt/sdcard/folder");
//
//       File[] files = directory.listFiles();
//
//       for (int i = 0; i < files.length; ++i) {
//           paths.add(files[i].getAbsolutePath());
//       }

       File dir = new File("path");
       FileFilter filter = new FileFilter() {
           @Override
           public boolean accept(File file) {
               return file.getAbsolutePath().matches(".*\\.epub");
           }
       };
       File[] images = dir.listFiles(filter);
   }


}
//file type epub