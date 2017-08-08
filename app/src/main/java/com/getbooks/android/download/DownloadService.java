package com.getbooks.android.download;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by marina on 07.08.17.
 */

public class DownloadService extends IntentService {

    public interface OnProgressUpdateListener {

        void onProgressUpdate(int progress);
    }

    private static OnProgressUpdateListener progressListener;
    public static void setOnProgressChangedListener(OnProgressUpdateListener _listener) {
        progressListener = _listener;
    }

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_PROGRESS = 3;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "DownloadService";

    public static int progress = 0;

    public DownloadService() {
        super(DownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        String urls = intent.getStringExtra("url");

        Bundle bundle = new Bundle();
        if (urls != null) {
            progress = 0;
            // Update UI: Download Service is Running
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try {
                downloadData(urls, receiver);
            } catch (Exception e) {

                // Sending error message back to activity
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        receiver.send(STATUS_FINISHED, Bundle.EMPTY);
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    private void downloadData(String requestUrl, ResultReceiver resultReceiver) throws IOException, DownloadException {
        InputStream inputStream = null;

        HttpURLConnection urlConnection = null;
        byte[] results;

        URL url = new URL(requestUrl);
        urlConnection = (HttpURLConnection) url.openConnection();

        int statusCode = urlConnection.getResponseCode();
        //200 represents HTTP OK
        if (statusCode == 200) {


            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = urlConnection.getContentLength();

            inputStream = new BufferedInputStream(urlConnection.getInputStream());

            // Output stream
            OutputStream output = new FileOutputStream(Environment
                    .getExternalStorageDirectory().toString()
                    + "/first.epub");

            Log.d("Download", Environment
                    .getExternalStorageDirectory().toString());

            byte data[] = new byte[1024];

            long total = 0;

            Bundle bundleProgress = new Bundle();

            int count;
            while ((count = inputStream.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                progress = (int) ((total * 100) / lenghtOfFile);

                bundleProgress.putInt("progress", progress);
                resultReceiver.send(STATUS_PROGRESS, bundleProgress);

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            inputStream.close();


        } else {
            throw new DownloadException("Failed to fetch data!!");
        }
    }

    public class DownloadException extends Exception {

        public DownloadException(String message) {
            super(message);
        }

        public DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

