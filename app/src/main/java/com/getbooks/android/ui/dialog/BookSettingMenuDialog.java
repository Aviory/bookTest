package com.getbooks.android.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.ReaderActivity;

import butterknife.BindView;

/**
 * Created by marinaracu on 15.08.17.
 */

public class BookSettingMenuDialog extends BaseFragment implements View.OnTouchListener {

    public static final String TAG_SETTING_BOOK_FRAGMENT = "book_setting_tag";

    @BindView(R.id.layout_book_settings)
    protected LinearLayout mRootLayoutBookSettings;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.menu_book_settings;
    }


    @Override
    public ReaderActivity getAct() {
        return (ReaderActivity) getActivity();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Rect rect = new Rect();
        mRootLayoutBookSettings.getGlobalVisibleRect(rect);
        if (!rect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY())){
            getAct().getSupportFragmentManager().beginTransaction().remove(BookSettingMenuDialog.this).commit();
        }
        return false;
    }
}
