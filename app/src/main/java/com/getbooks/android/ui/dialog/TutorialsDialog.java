package com.getbooks.android.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.getbooks.android.R;

/**
 * Created by marina on 24.07.17.
 */

public class TutorialsDialog extends AlertDialog implements View.OnClickListener {

    private Button mButtonYes;
    private Button mButtonNo;
    private OnItemTutorialsClick mOnItemTutorialsClick;

    public interface OnItemTutorialsClick {
        void onYesButtonClick();

        void onNoButtonClick();
    }

    public void setOnItemTutorialsClick(OnItemTutorialsClick onItemTutorialsClick) {
        mOnItemTutorialsClick = onItemTutorialsClick;
    }

    public TutorialsDialog(@NonNull Context context) {
        super(context);
    }

    protected TutorialsDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected TutorialsDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    public void show() {
        super.show();
        setContentView(R.layout.dialog_tutorials);
        mButtonYes = (Button) findViewById(R.id.txt_yes);
        mButtonNo = (Button) findViewById(R.id.txt_no);

        mButtonYes.setOnClickListener(this);
        mButtonNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_yes:
                if (mOnItemTutorialsClick != null) {
                    mOnItemTutorialsClick.onYesButtonClick();
                }
                break;
            case R.id.txt_no:
                if (mOnItemTutorialsClick != null) {
                    mOnItemTutorialsClick.onNoButtonClick();
                }
        }
    }
}
