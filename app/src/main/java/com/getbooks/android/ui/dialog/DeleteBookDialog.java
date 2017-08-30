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

public class DeleteBookDialog extends AlertDialog implements View.OnClickListener {
    private OnItemDeleteDialogListener mOnItemDeleteDialogListener;
    private TextView mTextCancelBookDelete;
    private TextView mTextBookDelete;
    private ImageView mImageCancelBookDelete;
    private ImageView mImageDeleteBook;

    public void setOnItemLogOutListener(OnItemDeleteDialogListener onItemDeleteDialogListener) {
        this.mOnItemDeleteDialogListener = onItemDeleteDialogListener;
    }

    public interface OnItemDeleteDialogListener {
        void cancelBookDelete();

        void deleteBookClick();
    }

    public DeleteBookDialog(Context context) {
        super(context);
    }

    protected DeleteBookDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected DeleteBookDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.dialog_delete_book);

        mTextCancelBookDelete = (TextView) findViewById(R.id.txt_cancel);
        mTextBookDelete = (TextView) findViewById(R.id.txt_delete);
        mImageCancelBookDelete = (ImageView) findViewById(R.id.img_cancel);
        mImageDeleteBook = (ImageView) findViewById(R.id.img_delete);

        setCancelable(false);

        mTextCancelBookDelete.setOnClickListener(this);
        mTextBookDelete.setOnClickListener(this);
        mImageCancelBookDelete.setOnClickListener(this);
        mImageDeleteBook.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_cancel:
            case R.id.img_cancel:
                if (mOnItemDeleteDialogListener != null) {
                    mOnItemDeleteDialogListener.cancelBookDelete();
                }
                break;
            case R.id.txt_delete:
            case R.id.img_delete:
                if (mOnItemDeleteDialogListener != null) {
                    mOnItemDeleteDialogListener.deleteBookClick();
                }
                break;
        }
    }
}
