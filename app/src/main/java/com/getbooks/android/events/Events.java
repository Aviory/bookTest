package com.getbooks.android.events;

import com.getbooks.android.model.enums.NetworkState;

/**
 * Created by marina on 12.07.17.
 */

public class Events {

    public static class NotificationReceived {

        String message;

        public NotificationReceived(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class RemoveTutorialsScreens {

    }

    public static class NetworkStateChange {
        NetworkState networkState;

        public NetworkStateChange(NetworkState networkState) {
            this.networkState = networkState;
        }

        public NetworkState getNetworkState() {
            return networkState;
        }

        public void setNetworkState(NetworkState networkState) {
            this.networkState = networkState;
        }
    }

    public static class StateLibrary {
        boolean isLibraryClose;

        public StateLibrary(boolean isLibraryClose) {
            this.isLibraryClose = isLibraryClose;
        }

        public boolean isLibraryClose() {
            return isLibraryClose;
        }

        public void setLibraryClose(boolean libraryClose) {
            isLibraryClose = libraryClose;
        }
    }

    public static class CloseContentMenuSetting {
        boolean isSettingMenuContentShow;

        public CloseContentMenuSetting(boolean isSettingMenuContentShow) {
            this.isSettingMenuContentShow = isSettingMenuContentShow;
        }

        public boolean isSettingMenuContentShow() {
            return isSettingMenuContentShow;
        }

        public void setSettingMenuContentShow(boolean settingMenuContentShow) {
            isSettingMenuContentShow = settingMenuContentShow;
        }
    }

    public static class UpDateLibrary {
        String bookName;

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }
    }
}
