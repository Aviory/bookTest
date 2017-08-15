package com.getbooks.android.ui.activities;

import android.os.Bundle;
import android.os.Handler;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.util.UiUtil;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startTheActivity(AuthorizationActivity.class);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_splash;
    }

    private void startTheActivity(final Class<?> activity) {
        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            // Start your app main activity
            checkUserAuthorization();
            //close this activity
            finish();
        }, Const.SPLASH_TIME_OUT);
    }

    private void checkUserAuthorization() {
        if (Prefs.getBooleanProperty(this, Const.IS_USER_AUTHORIZE)) {
            if (Prefs.getCountTutorialsShow(this) < Prefs.MAX_COUNT_VIEWS_TUTORIALS) {
                UiUtil.openActivity(this, TutorialsActivity.class, false);
            } else {
                UiUtil.openActivity(this, LibraryActivity.class, false);
            }
        } else {
            UiUtil.openActivity(this, AuthorizationActivity.class, false);
        }
    }
}
