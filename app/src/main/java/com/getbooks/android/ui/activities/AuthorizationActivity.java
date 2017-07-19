package com.getbooks.android.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.fragments.AuthorizationFragment;
import com.getbooks.android.ui.fragments.LibraryFragment;

/**
 * Created by marina on 12.07.17.
 */

public class AuthorizationActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Prefs.clearPrefs(this);

        checkUserAuthorization();
    }

    private void checkUserAuthorization() {
        if (Prefs.getBooleanProperty(this, Const.IS_USER_AUTHORIZE)) {
            BaseActivity.addFragment(this, LibraryFragment.class, R.id.coordinator_layout, null,
                    false, false, true, null);
        } else {
            BaseActivity.addFragment(this, AuthorizationFragment.class, R.id.coordinator_layout,
                    null, false, false, true, null);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.coordinator_layout;
    }
}
