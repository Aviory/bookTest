package com.getbooks.android.model;

/**
 * Created by marina on 08.08.17.
 */

public class DownloadInfo {

    public enum DownloadState {
        NOT_STARTED,
        QUEUED,
        DOWNLOADING,
        COMPLETE,
        INTERRUPTED
    }
    private volatile DownloadState mDownloadState = DownloadState.NOT_STARTED;

    public void setDownloadState(DownloadState state) {
        mDownloadState = state;
    }
    public DownloadState getDownloadState() {
        return mDownloadState;
    }
}
