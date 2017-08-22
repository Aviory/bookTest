package com.getbooks.android.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marina on 07.08.17.
 */

public class FileUtil {

    // Create our own directory
    public static String isCreatedDirectory(Context context, int userId) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File bookFolder = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        LogUtil.log("FileUtil", bookFolder.getAbsolutePath());
        boolean isPresent = true;
        if (!bookFolder.exists()) {
            LogUtil.log("FileUtil", "Directory created");
            isPresent = bookFolder.mkdir();
            File myDir = new File(bookFolder, "/Books-" + userId);
            myDir.mkdir();
            return myDir.getAbsolutePath();
        } else {
            LogUtil.log("FileUtil", "Directory present");
            File myDir = new File(bookFolder, "/Books-" + userId);
            myDir.mkdir();
            return myDir.getAbsolutePath();
        }
    }

    public static List<String> getDownloadedBookNamesList(String path) {
        List<String> namesBooks = new ArrayList<>();
        File file = new File(path);
        File[] booksNames = file.listFiles();
        for (int i = 0; i < booksNames.length; i++) {

        }
        return namesBooks;
    }

    public static boolean deleteCacheFiles(String dir) {
        int i = 0;
        if (TextUtils.isEmpty(dir)) {
            return false;
        }
        File file = new File(dir);
        if (file.isDirectory()) {
            String[] list = file.list();
            while (i < list.length) {
                File file2 = new File(dir + "/" + list[i]);
                if (file2 == null || !file2.isDirectory()) {
                    file2.delete();
                } else {
                    FileUtil.deleteCacheFiles(dir + "/" + list[i]);
                }
                i++;
            }
        }
        boolean delete = file.delete();
        LogUtil.log("GETBOOKS", file.getName() + " was deleted");
        return delete;
    }

    public static void clearCache(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                FileUtil.deleteCacheFiles(cacheDir.getAbsolutePath());
            }
        } catch (Exception e) {
            LogUtil.log("trimCache", e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
                }
            }
        }
    }

}
