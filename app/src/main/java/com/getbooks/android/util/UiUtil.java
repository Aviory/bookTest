package com.getbooks.android.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by marina on 11.07.17.
 */

public class UiUtil {

    public static void clearStack(Activity activity) {
        new Handler().postDelayed(activity::finishAffinity, 2000);
    }
}
