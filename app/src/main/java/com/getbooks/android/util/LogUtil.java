package com.getbooks.android.util;

import android.util.Log;

/**
 * Created by marina on 11.07.17.
 */

public class LogUtil {

    public static void log(String tag, String log){
        Log.d(tag, log);
    }

    public static void log(Object object, String log){
        Log.d(object.getClass().getName(), log);
    }
}
