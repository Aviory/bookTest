package com.getbooks.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by avi on 16.08.17.
 */

public class Text {
    @SerializedName("PopupID")
    @Expose
   String PopupID;
    @SerializedName("PopupText")
    @Expose
   String PopupText;
    @SerializedName("PopupUrl")
    @Expose
   String PopupUrl;
    @SerializedName("MandatoryAppvalFlag")
    @Expose
   String MandatoryAppvalFlag;

    public String getPopupID() {
        return PopupID;
    }

    public void setPopupID(String popupID) {
        PopupID = popupID;
    }

    public String getPopupText() {
        return PopupText;
    }

    public void setPopupText(String popupText) {
        PopupText = popupText;
    }

    public String getPopupUrl() {
        return PopupUrl;
    }

    public void setPopupUrl(String popupUrl) {
        PopupUrl = popupUrl;
    }

    public String getMandatoryAppvalFlag() {
        return MandatoryAppvalFlag;
    }

    public void setMandatoryAppvalFlag(String mandatoryAppvalFlag) {
        MandatoryAppvalFlag = mandatoryAppvalFlag;
    }

    public String getPopupFile() {
        return PopupFile;
    }

    public void setPopupFile(String popupFile) {
        PopupFile = popupFile;
    }

    @SerializedName("PopupFile")
    @Expose
   String PopupFile;
}

