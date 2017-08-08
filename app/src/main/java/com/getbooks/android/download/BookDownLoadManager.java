package com.getbooks.android.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.getbooks.android.encryption.EncryptionAndDownloadManager;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

/**
 * Created by marina on 04.08.17.
 */

public class BookDownLoadManager {

    public interface DownloadInterface{
        int setProgress();
    }

    private DownloadInterface downloadInterface;
    public static double progress;

    String Download_ID = "DOWNLOAD_ID";
    private Context context;
    DownloadManager downloadManager;

    public BookDownLoadManager(Context context) {
        this.context = context;
    }

    public void setDownloadInterface(DownloadInterface downloadInterface){
        this.downloadInterface = downloadInterface;
    }

    public void downloadBook(String urlEpub, String title) {
        String url = urlEpub;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        // only download via WIFI
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Example");
        request.setDescription("Downloading a very large zip");

        // we just want to download silently
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setDestinationInExternalFilesDir(context, null, title);
        Log.d("Downloaded", title);

        // enqueue this request
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadID = downloadManager.enqueue(request);

        //Save the download id
        Prefs.putDownloadBookId(context, downloadID, Download_ID);

//        startProgressChecker();
    }

    public void onResume() {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(downloadReceiver, intentFilter);
    }

    protected void onPause() {
        context.unregisterReceiver(downloadReceiver);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(Prefs.getDownloadBookId(context, Download_ID));
            Cursor cursor = downloadManager.query(query);

            cursor.moveToFirst();

            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);
                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                int reason = cursor.getInt(columnReason);

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    //Retrieve the saved download id
                    long downloadID = Prefs.getDownloadBookId(context, Download_ID);

                    ParcelFileDescriptor file;
                    try {
                        file = downloadManager.openDownloadedFile(downloadID);
                        Toast.makeText(context, "File Downloaded: " + file.toString(), Toast.LENGTH_LONG).show();
//                        stopProgressChecker();
                        try {
                            EncryptionAndDownloadManager.encrypt("/sdcard/Android/data/com.getbooks.android/files/מילה.epub", "/sdcard/Android/data/com.getbooks.android/encrypt.epub");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } finally {
                            FileUtil.deleteDir(new File("/sdcard/Android/data/com.getbooks.android/files/"));
                        }
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    }

                } else if (status == DownloadManager.STATUS_FAILED) {
                    Toast.makeText(context, "FAILED!\n" + "reason of " + reason, Toast.LENGTH_LONG).show();
                } else if (status == DownloadManager.STATUS_PAUSED) {
                    Toast.makeText(context, "PAUSED!\n" + "reason of " + reason, Toast.LENGTH_LONG).show();
                } else if (status == DownloadManager.STATUS_PENDING) {
                    Toast.makeText(context, "PENDING!", Toast.LENGTH_LONG).show();
                } else if (status == DownloadManager.STATUS_RUNNING) {
                    Toast.makeText(context, "RUNNING!", Toast.LENGTH_LONG).show();
                }
            }
        }

    };



    ///////////////////////////////////////////////////////////////////
    private static final int PROGRESS_DELAY = 1000;
    Handler handler = new Handler();
    private boolean isProgressCheckerRunning = false;

    // when the first download starts
//    startProgressChecker();

    // when the last download finishes or the Activity is destroyed
//    stopProgressChecker();

    /**
     * Checks download progress.
     */
    private void checkProgress() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(~(DownloadManager.STATUS_FAILED | DownloadManager.STATUS_SUCCESSFUL));
        Cursor cursor = downloadManager.query(query);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }
        do {
            long reference = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            long progress = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            // do whatever you need with the progress
            Log.d("Download", String.valueOf(progress));
        } while (cursor.moveToNext());
        cursor.close();
    }

    /**
     * Starts watching download progress.
     *
     * This method is safe to call multiple times. Starting an already running progress checker is a no-op.
     */
    private void startProgressChecker() {
        if (!isProgressCheckerRunning) {
            progressChecker.run();
            isProgressCheckerRunning = true;
        }
    }

    /**
     * Stops watching download progress.
     */
    private void stopProgressChecker() {
        handler.removeCallbacks(progressChecker);
        isProgressCheckerRunning = false;
    }

    /**
     * Checks download progress and updates status, then re-schedules itself.
     */
    private Runnable progressChecker = new Runnable() {
        @Override
        public void run() {
            try {
                checkProgress();
//                downloadManager.refresh();
            } finally {
                handler.postDelayed(progressChecker, PROGRESS_DELAY);
            }
        }
    };
}
