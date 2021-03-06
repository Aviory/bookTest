package com.getbooks.android.receivers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by marina on 07.08.17.
 */

@SuppressLint("ParcelCreator")
public class DownloadResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public DownloadResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
