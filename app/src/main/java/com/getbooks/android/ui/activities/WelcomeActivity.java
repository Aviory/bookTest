package com.getbooks.android.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.util.UiUtil;

import butterknife.OnClick;

/**
 * Created by marinaracu on 14.09.17.
 */

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Prefs.setBooleanProperty(getAct(), Const.SHOW_WELCOME_SCREEN, true);
    }

    @OnClick(R.id.txt_next)
    protected void goToLibrary() {
        checkUserAuthorization();
    }

    private void checkUserAuthorization() {
        if (Prefs.getBooleanProperty(this, Const.IS_USER_AUTHORIZE)) {
            if (Prefs.getCountTutorialsShow(this) < Prefs.MAX_COUNT_VIEWS_TUTORIALS) {
                UiUtil.openActivity(this, TutorialsActivity.class, false, "", "", "", "");
            } else {
                UiUtil.openActivity(this, LibraryActivity.class, false, "", "", "", "");
            }
        } else {
            UiUtil.openActivity(this, AuthorizationActivity.class, false, "", "", "", "");
        }
        finish();
    }

    @Override
    public int getLayout() {
        return R.layout.activity_welcome;
    }
}
