package com.getbooks.android.util;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
            mMaterialDialog.dismiss();
            mMaterialDialog = null;
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

    public static void showToast(Context context, int resString) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(() -> {
            if (mToast == null || !mToast.getView().isShown())
                mToast = Toast.makeText(context, resString, Toast.LENGTH_SHORT);
            mToast.show();
        });
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public static void hideView(View view) {
        showView(view, false);
    }

    public static void showView(View view) {
        showView(view, true);
    }

    private static void showView(final View view, final boolean show) {
        view.animate().alpha(show ? 1 : 0).setDuration(400).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public static void openActivity(Activity context, Class<?> tClass, boolean isCloseCurrentActivity,
                                    String key, String value, String keyExtra, String valueExtra){
        Intent intent = new Intent(context, tClass);
        if (!TextUtils.isEmpty(value))
        intent.putExtra(key, value);
        intent.putExtra(keyExtra, valueExtra);
        context.startActivity(intent);
        if (isCloseCurrentActivity) context.finish();
    }
}
