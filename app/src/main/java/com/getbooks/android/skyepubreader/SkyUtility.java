package com.getbooks.android.skyepubreader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.apache.http.util.ByteArrayBuffer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class SkyUtility {
    Context context;
    final String TAG = "EPub";

    public SkyUtility(Context context) {
        this.context = context;
    }

    public static String getModelName() {
        return Build.MODEL;
    }

    public static String getDeviceName() {
        return Build.DEVICE;
    }

	/*
	Mako      – Google Nexus 4 
	Flo       – Google Nexus 7 (2013)
	Grouper   – Google Nexus 7
	Manta     – Google Nexus 10
	Maguro    – Google Galaxy Nexus
	Crespo    – Google Nexus S
	Steelhead – Google Nexus Q
	*/

    public static boolean isNexus() {
        String models[] = {"mako", "flo", "grouper", "maguro", "crespo", "hammerhead"};
        String model = SkyUtility.getModelName();
        String device = SkyUtility.getDeviceName();
        for (int i = 0; i < models.length; i++) {
            String name = models[i];
            if (name.equalsIgnoreCase(model) || name.equalsIgnoreCase(device)) {
                return true;
            }
        }
        return false;
    }


    @SuppressLint("InlinedApi")
    public static void makeFullscreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        } else if (Build.VERSION.SDK_INT >= 11) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

}

