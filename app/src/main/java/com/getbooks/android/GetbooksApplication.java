package com.getbooks.android;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.skytree.epub.BookInformation;
import com.skytree.epub.SkyKeyManager;
import com.getbooks.android.skyepubreader.CustomFont;
import com.getbooks.android.skyepubreader.SkyDatabase;
import com.getbooks.android.skyepubreader.SkySetting;

import java.util.ArrayList;

/**
 * Created by marina on 04.08.17.
 */

public class GetbooksApplication extends Application {

    public String message = "We are the world.";
    public ArrayList<BookInformation> bis;
    public ArrayList<CustomFont> customFonts = new ArrayList<CustomFont>();
    public SkySetting setting;
    public SkyDatabase sd = null;
    public int sortType = 0;
    public SkyKeyManager keyManager;

    public String getApplicationName() {
        int stringId = this.getApplicationInfo().labelRes;
        return this.getString(stringId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String appName = this.getApplicationName();
        if (SkySetting.getStorageDirectory() == null) {
//			 All book related data will be stored /data/data/com....../files/appName/
            SkySetting.setStorageDirectory(getFilesDir().getAbsolutePath(), appName);
            // All book related data will be stored /sdcard/appName/...
//			SkySetting.setStorageDirectory(Environment.getExternalStorageDirectory().getAbsolutePath(),appName);
        }
        sd = new SkyDatabase(this);
        reloadBookInformations();
        loadSetting();
        createSkyDRM();
    }

    public void reloadBookInformations() {
        this.bis = sd.fetchBookInformations(sortType, "");
    }

    public void reloadBookInformations(String key) {
        this.bis = sd.fetchBookInformations(sortType, key);
    }

    public void loadSetting() {
        this.setting = sd.fetchSetting();
    }

    public void saveSetting() {
        sd.updateSetting(this.setting);
    }

    public void createSkyDRM() {
        this.keyManager = new SkyKeyManager("A3UBZzJNCoXmXQlBWD4xNo", "zfZl40AQXu8xHTGKMRwG69");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

}
