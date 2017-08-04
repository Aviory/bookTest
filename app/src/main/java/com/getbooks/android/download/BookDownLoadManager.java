package com.getbooks.android.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.getbooks.android.prefs.Prefs;

/**
 * Created by marina on 04.08.17.
 */

public class BookDownLoadManager {

    String Download_ID = "DOWNLOAD_ID";
    private Context context;
    DownloadManager downloadManager;

    public BookDownLoadManager(Context context) {
        this.context = context;
    }

    public void downloadBook(String urlEpub) {
        String url = urlEpub;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

// only download via WIFI
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Example");
        request.setDescription("Downloading a very large zip");

// we just want to download silently
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setDestinationInExternalFilesDir(context, null, Environment.DIRECTORY_DOCUMENTS);
        Log.d("Downloaded", Environment.DIRECTORY_DOCUMENTS);

// enqueue this request
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadID = downloadManager.enqueue(request);

        //Save the download id
        Prefs.putDownloadBookId(context, downloadID, Download_ID);
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

            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);
                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                int reason = cursor.getInt(columnReason);

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    //Retrieve the saved download id
                    long downloadID = Prefs.getDownloadBookId(context, Download_ID);

                    Toast.makeText(context, "File Downloaded: " , Toast.LENGTH_LONG).show();
                    ParcelFileDescriptor file;
//                    try {
//                        file = downloadManager.openDownloadedFile(downloadID);
//                        Toast.makeText(context, "File Downloaded: " + file.toString(), Toast.LENGTH_LONG).show();
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
//                    }

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
