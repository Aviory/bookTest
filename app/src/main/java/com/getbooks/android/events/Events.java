package com.getbooks.android.events;

import com.getbooks.android.model.enums.NetworkState;

import java.util.Calendar;

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
        String bookSku;
        Calendar dateLastReading;
        double position;
        String author;
        int bookItemViewPosition;

        public int getBookItemViewPosition() {
            return bookItemViewPosition;
        }

        public void setBookItemViewPosition(int bookItemViewPosition) {
            this.bookItemViewPosition = bookItemViewPosition;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public double getPosition() {
            return position;
        }

        public void setPosition(double position) {
            this.position = position;
        }

        public Calendar getDateLastReading() {
            return dateLastReading;
        }

        public void setDateLastReading(Calendar dateLastReading) {
            this.dateLastReading = dateLastReading;
        }

        public String getBookSku() {
            return bookSku;
        }

        public void setBookSku(String bookSku) {
            this.bookSku = bookSku;
        }
    }

    public static class UpDateMainScreen {
        boolean isUpDate;

        public boolean getStateUpDate() {
            return isUpDate;
        }

        public void setStateUpDate(boolean isUpDate) {
            this.isUpDate = isUpDate;
        }
    }
}
