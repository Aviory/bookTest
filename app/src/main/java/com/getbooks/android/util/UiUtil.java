package com.getbooks.android.util;

import android.animation.Animator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.reader.UnderlinedTextView;
import com.getbooks.android.skyepubreader.BookViewActivity;
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.ui.dialog.MaterialDialog;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;

/**
 * Created by marina on 11.07.17.
 */

public class UiUtil {

    private static MaterialDialog mMaterialDialog;
    private static Toast mToast;

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
        try {
            if (!TextUtils.isEmpty(str)) {
                str = URLEncoder.encode(str, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            Log.d("error in encoding", e.getMessage());
            e.printStackTrace();
        }
        return str;
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
                                    String key, String value, String keyExtra, String valueExtra) {
        Intent intent = new Intent(context, tClass);
        if (!TextUtils.isEmpty(value))
            intent.putExtra(key, value);
        intent.putExtra(keyExtra, valueExtra);
        context.startActivity(intent);
        if (isCloseCurrentActivity) context.finish();
    }

    public static void openViewReaderActivity(Activity context, Class<?> tClass, int bookCode,
                                              String bookTitle, String author, String bookName,
                                              double position, boolean doubledPaged, int transitionType,
                                              boolean globalPagination, boolean rtl, boolean  verticalWriting,
                                              String directoryPath, String bookSku, int userId) {

        Intent intent = new Intent(context, tClass);
        intent.putExtra(Const.BOOK_CODE, bookCode);
        intent.putExtra(Const.TITLE, bookTitle);
        intent.putExtra(Const.AUTHOR, author);
        intent.putExtra(Const.BOOK_NAME, bookName);
        intent.putExtra(Const.POSITION, position);
        intent.putExtra(Const.DOUBLE_PAGED, doubledPaged);
        intent.putExtra(Const.TRANSITION_TYPE, transitionType);
        intent.putExtra(Const.GLOBAL_PAGINATION, globalPagination);
        intent.putExtra(Const.RTL, rtl);
        intent.putExtra(Const.VERTICAL_WRITING, verticalWriting);
        intent.putExtra(Const.DIRECTORY_PATH, directoryPath);
        intent.putExtra(Const.BOOK_SKU, bookSku);
        intent.putExtra(Const.USER_ID, userId);
        context.startActivity(intent);
    }

    public static int parseIntTiString(String bookCode) {
        String number = bookCode.replaceAll("[^\\d.]", "");
        return Integer.parseInt(number);
    }

    public static int getRandomGeneratedBookCode() {
        return new Random().nextInt(Integer.MAX_VALUE);
    }


    ///////////////////////////


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy", text);
        clipboard.setPrimaryClip(clip);
    }

    public static void share(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent,
                context.getResources().getText(R.string.send_to)));
    }

}
