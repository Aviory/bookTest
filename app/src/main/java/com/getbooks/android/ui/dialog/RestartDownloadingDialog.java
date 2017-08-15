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
 * Created by marina on 09.08.17.
 */

public class RestartDownloadingDialog extends AlertDialog implements View.OnClickListener {

    private Button mButtonYes;
    private Button mButtonNo;
    private OnItemRestartDownloadClick mOnItemRestartDownloadClick;

    public interface OnItemRestartDownloadClick {
        void restartDownloadClick();

        void disableDownloadClick();
    }

    public void setOnRestartDownloadClick(OnItemRestartDownloadClick onItemTutorialsClick) {
        mOnItemRestartDownloadClick = onItemTutorialsClick;
    }


    public RestartDownloadingDialog(@NonNull Context context) {
        super(context);
    }

    protected RestartDownloadingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected RestartDownloadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.dialog_restart_download);
        mButtonYes = (Button) findViewById(R.id.txt_yes);
        mButtonNo = (Button) findViewById(R.id.txt_no);

        setCancelable(false);

        mButtonYes.setOnClickListener(this);
        mButtonNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_yes:
                if (mOnItemRestartDownloadClick != null) {
                    mOnItemRestartDownloadClick.restartDownloadClick();
                }
                break;
            case R.id.txt_no:
                if (mOnItemRestartDownloadClick != null) {
                    mOnItemRestartDownloadClick.disableDownloadClick();
                }
        }
    }
}
