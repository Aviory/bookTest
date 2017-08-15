package com.getbooks.android.util;

import android.os.Build;

/**
 * Created by marina on 11.08.17.
 */

public class SystemUtil {

    public static String getSetting() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return model.startsWith(manufacturer) ? checkString(model) : checkString(manufacturer) + " " + model;
    }

    private static String checkString(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        char charAt = str.charAt(0);
        return !Character.isUpperCase(charAt) ? Character.toUpperCase(charAt) + str.substring(1) : str;
    }
}
