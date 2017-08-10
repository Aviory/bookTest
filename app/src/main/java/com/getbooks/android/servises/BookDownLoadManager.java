package com.getbooks.android.servises;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.getbooks.android.encryption.Encryption;
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

    String Download_ID = "DOWNLOAD_ID";
    private Context context;
    DownloadManager downloadManager;
    Thread backgroundThread;

    private long currentDownloadId;

    public BookDownLoadManager(Context context) {
        this.context = context;
    }

    public void downloadBook(String urlEpub, String title) {
        String url = urlEpub;
        DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(url));

        // only download via WIFI
        downloadRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        downloadRequest.setTitle("Example");
        downloadRequest.setDescription("Downloading a very large zip");

        // we just want to download silently
        downloadRequest.setVisibleInDownloadsUi(false);
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        downloadRequest.setDestinationInExternalFilesDir(context, null, title);
        Log.d("Downloaded", title);

        // enqueue this request
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        long downloadID = downloadManager.enqueue(downloadRequest);

        currentDownloadId = downloadManager.enqueue(downloadRequest);
        backgroundThread = Thread.currentThread();
        synchronized (Thread.currentThread()) {
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Save the download id
        Prefs.putDownloadBookId(context, currentDownloadId, Download_ID);
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
//            // TODO Auto-generated method stub
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
                            Encryption.encrypt("/sdcard/Android/data/com.getbooks.android/files/מילה.epub", "/sdcard/Android/data/com.getbooks.android/encrypt.epub");
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
                            Log.d("Download", "onReceive");
                            long downloadId = Prefs.getDownloadBookId(context, Download_ID);
                            if (downloadId == currentDownloadId) {
                                Log.d("Download", "downloadId");
                                synchronized (backgroundThread) {
                                    Log.d("Download", "notify");
                                    backgroundThread.notify();
                                }
                            }
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
}
