package com.getbooks.android.servises;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.getbooks.android.encryption.Encryption;
import com.getbooks.android.events.Events;
import com.getbooks.android.util.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by marina on 07.08.17.
 */

public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_PROGRESS = 3;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "DownloadService";

    private static int progress = 0;
    private boolean isLibraryClose = false;
    private String mBookName;
    private String mDirectoryPath;


    public DownloadService() {
        super(DownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        String urls = intent.getStringExtra("url");

        mBookName = intent.getStringExtra("bookName");
        mDirectoryPath = intent.getStringExtra("directoryPath");

        Bundle bundle = new Bundle();
        if (urls != null) {
            progress = 0;
            // Update UI: Download Service is Running
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try {
                downloadData(urls, receiver, mBookName);
            } catch (Exception e) {
                // Sending error message back to activity
                bundle.putString(Intent.EXTRA_TEXT, e.getClass().getSimpleName());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    private void downloadData(String requestUrl, ResultReceiver resultReceiver, String bookName) throws IOException, DownloadException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
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

            // Output stream encoded
            CipherOutputStream output = Encryption.encryptStream(mDirectoryPath
                    + "/" + bookName + ".epub");

//            FileOutputStream output =   new FileOutputStream(mDirectoryPath
//                    + "/" + bookName + ".epub");

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

            resultReceiver.send(STATUS_FINISHED, Bundle.EMPTY);

        } else {
            throw new DownloadException("Failed to fetch data!!");
        }
    }

    @Subscribe
    public void onMessageEvent(Events.StateLibrary stateLibrary) {
        if (stateLibrary.isLibraryClose()) {
            isLibraryClose = true;
            if (progress != 100) {
                FileUtil.deleteDir(new File(mDirectoryPath
                        + "/" + mBookName + ".epub"));
            }
            Log.d("Library", "true");
        } else {
            isLibraryClose = false;
            Log.d("Library", "false");
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

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

