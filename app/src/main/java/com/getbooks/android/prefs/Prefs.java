package com.getbooks.android.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by marina on 13.07.17.
 */

public class Prefs {
    private static final String GERBOOKS_PREF = "getbooks_pref";

    private static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(GERBOOKS_PREF, Context.MODE_PRIVATE);
    }

    public static void setBooleanProperty(Context context, String key, boolean value){
        SharedPreferences sharedPreferences = getPrefs(context);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanProperty(Context context ,String key){
        return getPrefs(context).getBoolean(key, false);
    }

    public static void clearPrefs(Context context){
        getPrefs(context).edit().clear().apply();
    }

}
