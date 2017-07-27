package com.getbooks.android.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.getbooks.android.R;
import com.getbooks.android.ui.dialog.MaterialDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by marina on 11.07.17.
 */

public class UiUtil {

    private static MaterialDialog mMaterialDialog;
    private static Toast mToast;

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

    public static void showDialog(Context context) {
        if (mMaterialDialog != null) {
            mMaterialDialog.hide();
        }
        mMaterialDialog = new MaterialDialog(context);
        mMaterialDialog.setCancelable(false);
        mMaterialDialog.show();
    }

    public static void hideDialog() {
        if (mMaterialDialog != null && mMaterialDialog.isShowing()) {
            mMaterialDialog.dismiss();
            mMaterialDialog = null;
        }
    }

    public static void showConnectionErrorToast(Context context) {
        if (mToast == null || !mToast.getView().isShown()) {
            mToast = Toast.makeText(context, R.string.error_connection, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }
}
