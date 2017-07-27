package com.getbooks.android.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.fragments.LibraryFragment;

/**
 * Created by marina on 26.07.17.
 */

public class LibraryActivity extends BaseActivity  {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
        BaseActivity.addFragment(this, LibraryFragment.class, R.id.coordinator_layout, null,
                false, false, true, null);
    }

    @Override
    public int getLayout() {
        return R.layout.coordinator_layout;
    }
}
