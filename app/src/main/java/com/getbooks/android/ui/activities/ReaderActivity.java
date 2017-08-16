package com.getbooks.android.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.fragments.BookContentFragment;
import com.getbooks.android.ui.fragments.BookSettingMenuFragment;

import butterknife.OnClick;

/**
 * Created by marinaracu on 14.08.17.
 */

public class ReaderActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_reader;
    }

    @OnClick(R.id.img_book_setting)
    protected void openBookSetting() {
        BaseActivity.addFragment(this, BookSettingMenuFragment.class, R.id.content_book_settings,
                null, false, true, true, null);
    }


    @OnClick(R.id.img_book_close)
    protected void closeBook() {
        super.onBackPressed();
    }

    @OnClick(R.id.img_book_murks_content)
    protected void openBookMurksContent() {
        BaseActivity.addFragment(this, BookContentFragment.class, R.id.content_book_settings,
                null, false, true, true, null);
    }
}
