package com.getbooks.android.util;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by marina on 11.07.17.
 */

public class UiUtil {

    public static void clearStack(Activity activity) {
        new Handler().postDelayed(activity::finishAffinity, 2000);
    }

    public static String decode(String str) {
        String result = "";
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("error in decoding", e.getMessage());
        }
        return result;
    }


    public static String encode(String str) {
        String result = "";
        try {
            return URLEncoder.encode(str.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.d("error in encoding", e.getMessage());
        }
        return result;
    }
}
