package com.getbooks.android.skyepubreader;

public class SkySetting {
    public int userId;
    public int bookCode;
    public String fontName;
    public int fontSize;
    public int lineSpacing;
    public int foreground;
    public int background;
    public int theme;
    public double brightness;
    public int transitionType;
    public boolean lockRotation;
    public boolean doublePaged;
    public boolean allow3G;
    public boolean globalPagination;

    public boolean mediaOverlay;
    public boolean tts;
    public boolean autoStartPlaying;
    public boolean autoLoadNewChapter;
    public boolean highlightTextToVoice;

    public static String storageDirectory = null;

    public static String getStorageDirectory() {
        return storageDirectory;
    }

    public static void setStorageDirectory(String directory, String appName) {
        storageDirectory = directory + "/" + appName;
    }

    @Override
    public String toString() {
        return "SkySetting{" +
                "userId=" + userId +
                ", bookCode=" + bookCode +
                ", fontName='" + fontName + '\'' +
                ", fontSize=" + fontSize +
                ", lineSpacing=" + lineSpacing +
                ", foreground=" + foreground +
                ", background=" + background +
                ", theme=" + theme +
                ", brightness=" + brightness +
                ", transitionType=" + transitionType +
                ", lockRotation=" + lockRotation +
                ", doublePaged=" + doublePaged +
                ", allow3G=" + allow3G +
                ", globalPagination=" + globalPagination +
                ", mediaOverlay=" + mediaOverlay +
                ", tts=" + tts +
                ", autoStartPlaying=" + autoStartPlaying +
                ", autoLoadNewChapter=" + autoLoadNewChapter +
                ", highlightTextToVoice=" + highlightTextToVoice +
                '}';
    }
}
