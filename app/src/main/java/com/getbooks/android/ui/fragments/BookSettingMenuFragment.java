package com.getbooks.android.ui.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.ReaderActivity;

import butterknife.BindView;

/**
 * Created by marinaracu on 15.08.17.
 */

public class BookSettingMenuFragment extends BaseFragment implements View.OnTouchListener {

    public static final String TAG_SETTING_BOOK_FRAGMENT = "book_setting_tag";

    @BindView(R.id.layout_book_settings_root)
    protected FrameLayout mRootLayoutBookSettings;
    @BindView(R.id.layout_book_settings)
    protected LinearLayout mLayoutBookSettings;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootLayoutBookSettings.setOnTouchListener(this);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_menu_book_settings;
    }


    @Override
    public ReaderActivity getAct() {
        return (ReaderActivity) getActivity();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Rect rect = new Rect();
        mLayoutBookSettings.getGlobalVisibleRect(rect);
        if (!rect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) {
            getAct().getSupportFragmentManager().beginTransaction().remove(BookSettingMenuFragment.this).commit();
        }
        return false;
    }
}
