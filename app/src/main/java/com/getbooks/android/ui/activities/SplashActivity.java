package com.getbooks.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.ui.BaseActivity;

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

    private void startTheActivity(final Class<?> activity){
        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            // Start your app main activity
            Intent intent = new Intent(SplashActivity.this, activity);
            startActivity(intent);
            //close this activity
            finish();
        }, Const.SPLASH_TIME_OUT);
    }
}
