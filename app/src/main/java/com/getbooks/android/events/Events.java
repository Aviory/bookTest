package com.getbooks.android.events;

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

    public static class ShowTutorialsScreens {
        boolean isShowTutorials;

        public ShowTutorialsScreens(boolean isShowTutorials) {
            this.isShowTutorials = isShowTutorials;
        }

        public boolean isShowTutorials() {
            return isShowTutorials;
        }

        public void setShowTutorials(boolean showTutorials) {
            isShowTutorials = showTutorials;
        }
    }

    public static class RemoveTutorialsScreens{

    }
}
