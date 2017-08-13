package com.getbooks.android;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by marina on 04.08.17.
 */

public class GetbooksApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

}
