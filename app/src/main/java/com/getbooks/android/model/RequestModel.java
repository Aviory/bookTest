package com.getbooks.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by avi on 17.08.17.
 */

public class RequestModel {
    @SerializedName("RetCode")
    @Expose
    String RetCode;

    public String getRetCode() {
        return RetCode;
    }

    public void setRetCode(String retCode) {
        RetCode = retCode;
    }

    public String getRetDesc() {
        return RetDesc;
    }

    public void setRetDesc(String retDesc) {
        RetDesc = retDesc;
    }

    public List<Text> getPopUps() {
        return PopUps;
    }

    public void setPopUps(List<Text> popUps) {
        PopUps = popUps;
    }

    @SerializedName("RetDesc")
    @Expose
    String RetDesc;
    @SerializedName("PopUps")
    @Expose
    List<Text> PopUps;
}
