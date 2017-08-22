package com.getbooks.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.getbooks.android.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marinaracu on 19.08.17.
 */

public class AddNoteDialog extends Dialog {
    public AddNoteDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public AddNoteDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init();
    }

    protected AddNoteDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_add_note);
    }
}
