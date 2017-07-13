package com.getbooks.android.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;

import com.getbooks.android.R;

/**
 * Created by marina on 13.07.17.
 */

public class MaterialDialog extends AlertDialog {

    public MaterialDialog(@NonNull Context context) {
        super(context);
    }

    protected MaterialDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected MaterialDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.material_progress);
    }
}
