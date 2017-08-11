package com.getbooks.android.util;

import java.util.Calendar;

/**
 * Created by marina on 11.08.17.
 */

public class DateUtil {

    public static Calendar getDate(long date) {
        if (date <= 0) {
            return null;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(date);
        return instance;
    }
}
