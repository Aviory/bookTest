package com.getbooks.android.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.getbooks.android.Const;

/**
 * Created by marina on 13.07.17.
 */

public class Prefs {
    private static final String GERBOOKS_PREF = "getbooks_pref";
    public static final int MAX_COUNT_VIEWS_TUTORIALS = 3;
    private static final String PREF_TOKEN = "preferences_token";


    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(GERBOOKS_PREF, Context.MODE_PRIVATE);
    }

    public static void setBooleanProperty(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = getPrefs(context);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanProperty(Context context, String key) {
        return getPrefs(context).getBoolean(key, false);
    }

    public static void putToken(Context context, String token) {
        putString(context, PREF_TOKEN, token);
    }

    public static String getToken(Context context) {
        return getString(context, PREF_TOKEN, null);
    }

    public static String getString(Context context, String preferenceKey, String preferenceDefaultValue) {
        return getPrefs(context).getString(preferenceKey, preferenceDefaultValue);
    }

    public static void putString(Context context, String preferenceKey, String preferenceValue) {
        getPrefs(context).edit().putString(preferenceKey, preferenceValue).commit();
    }

    public static void putDownloadBookId(Context context, long downloadId, String key){
        getPrefs(context).edit().putLong(key, downloadId).apply();
    }

    public static long getDownloadBookId(Context context, String key){
        return getPrefs(context).getLong(key, 0);
    }

    public static void  saveUserSession(Context context, String key, int value){
        getPrefs(context).edit().putInt(key, value).commit();
    }

    public static int getUserSession(Context context, String key){
        return getPrefs(context).getInt(key, 0);
    }

    public static void putActivityState(Context context, String key, boolean isActivityAlive){
        getPrefs(context).edit().putBoolean(key, isActivityAlive).apply();
    }

    public static boolean isActivityAlive(Context context, String key){
      return getPrefs(context).getBoolean(key, false);
    }

    public static int getCountTutorialsShow(Context context) {
        return getPrefs(context).getInt(Const.SHOW_TUTORIALS_COUNT, 0);
    }

    public static void addCountTutorialsView(Context context) {
        SharedPreferences sharedPreferences = getPrefs(context);
        int count = sharedPreferences.getInt(Const.SHOW_TUTORIALS_COUNT, 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (count < MAX_COUNT_VIEWS_TUTORIALS) {
            editor.putInt(Const.SHOW_TUTORIALS_COUNT, count + 1);
        }

        editor.apply();
    }

    public static void completeTutorialsShow(Context context) {
        getPrefs(context).edit().putInt(Const.SHOW_TUTORIALS_COUNT, MAX_COUNT_VIEWS_TUTORIALS).apply();
    }

    public static void clearPrefs(Context context) {
        getPrefs(context).edit().clear().apply();
    }

}
