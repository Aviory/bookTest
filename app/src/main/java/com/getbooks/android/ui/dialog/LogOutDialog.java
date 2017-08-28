package com.getbooks.android.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbooks.android.R;

/**
 * Created by marinaracu on 23.08.17.
 */

public class LogOutDialog extends AlertDialog implements View.OnClickListener {

    private OnItemLogOutListener mOnItemLogOutListener;
    private TextView mTextCancel;
    private TextView mTextLogOut;
    private ImageView mImageCancel;
    private ImageView mImageLogOut;

    public void setOnItemLogOutListener(OnItemLogOutListener onItemLogOutListener) {
        this.mOnItemLogOutListener = onItemLogOutListener;
    }

    public interface OnItemLogOutListener {
        void cancelLogOut();

        void logOutClick();
    }

    public LogOutDialog(Context context) {
        super(context);
    }

    protected LogOutDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected LogOutDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.dialog_log_out);

        mTextCancel = (TextView) findViewById(R.id.txt_cancel);
        mTextLogOut = (TextView) findViewById(R.id.txt_sign_out);
        mImageCancel = (ImageView) findViewById(R.id.img_cancel);
        mImageLogOut = (ImageView) findViewById(R.id.img_log_out);

        setCancelable(false);

        mTextCancel.setOnClickListener(this);
        mTextLogOut.setOnClickListener(this);
        mImageCancel.setOnClickListener(this);
        mImageLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_cancel:
            case R.id.img_cancel:
                if (mOnItemLogOutListener != null) {
                    mOnItemLogOutListener.cancelLogOut();
                }
                break;
            case R.id.txt_sign_out:
            case R.id.img_log_out:
                if (mOnItemLogOutListener != null) {
                    mOnItemLogOutListener.logOutClick();
                }
                break;
        }
    }
}
