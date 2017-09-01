package com.getbooks.android.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.util.LogUtil;

/**
 * Created by avi on 29.08.17.
 */

public class DialogSettings extends AlertDialog implements View.OnClickListener{

    private ImageView mImageCancelBookDelete;
    private RadioButton mRadioButtonYes;
    private RadioButton mRadioButtonNo;

    public static DialogSettings newInstance(Context context){
        DialogSettings dialogSettings = new DialogSettings(context);
        return dialogSettings;
    }

    public DialogSettings(@NonNull Context context) {
        super(context);
    }

    public DialogSettings(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected DialogSettings(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    @Override
    public void show() {
        super.show();
        setContentView(R.layout.dialog_settings);
        mImageCancelBookDelete = (ImageView) findViewById(R.id.img_close);
        mRadioButtonYes = (RadioButton) findViewById(R.id.setting_dialog_yes);
        mRadioButtonNo = (RadioButton) findViewById(R.id.setting_dialog_no);
        mImageCancelBookDelete.setOnClickListener(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(mRadioButtonYes.isChecked()){
            Prefs.setBooleanProperty(getContext(), Const.PUSH_NOTIFY_BY_UPDATE, true);
        }else {
            Prefs.setBooleanProperty(getContext(), Const.PUSH_NOTIFY_BY_UPDATE, false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_close:
                dismiss();
                break;
    }
}
}
