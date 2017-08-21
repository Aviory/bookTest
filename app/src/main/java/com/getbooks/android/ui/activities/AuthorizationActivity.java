package com.getbooks.android.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.fragments.AuthorizationFragment;

/**
 * Created by marina on 12.07.17.
 */

public class AuthorizationActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseActivity.addFragment(this, AuthorizationFragment.class, R.id.coordinator_layout,
                null, false, false, true, null);
    }

    @Override
    public int getLayout() {
        return R.layout.coordinator_web_layout;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }
}
