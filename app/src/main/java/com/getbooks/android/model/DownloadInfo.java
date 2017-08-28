package com.getbooks.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by marina on 08.08.17.
 */

public class DownloadInfo implements Parcelable {
    public enum DownloadState {
        NOT_STARTED,
        QUEUED,
        DOWNLOADING,
        COMPLETE,
        INTERRUPTED,
        SELECTED_DELETING_BOOKS
    }

    private volatile DownloadState mDownloadState = DownloadState.NOT_STARTED;

    public void setDownloadState(DownloadState state) {
        mDownloadState = state;
    }

    public DownloadState getDownloadState() {
        return mDownloadState;
    }

    public DownloadInfo() {
    }

    protected DownloadInfo(Parcel in) {
        mDownloadState = (DownloadState) in.readValue(DownloadState.class.getClassLoader());
    }

    public static final Creator<DownloadInfo> CREATOR = new Creator<DownloadInfo>() {
        @Override
        public DownloadInfo createFromParcel(Parcel in) {
            return new DownloadInfo(in);
        }

        @Override
        public DownloadInfo[] newArray(int size) {
            return new DownloadInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(mDownloadState);
    }

}
