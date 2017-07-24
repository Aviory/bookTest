package com.getbooks.android.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by marina on 24.07.17.
 */

public class TypefaceUtil {

    private static Typeface mArial;
    private static Typeface mFrankRuehlMedium;

    private static final String ARIAL = "font/roboto_light.ttf";
    private static final String FRANK_RUEHL_MEDIUM = "font/roboto_light.ttf";

    public static Typeface getArialTypeface(Context context) {
        if (mArial == null) {
            mArial = Typeface.createFromAsset(context.getAssets(), ARIAL);
        }
        return mArial;
    }

    public static Typeface getFrankRuehlMediumTypeface(Context context) {
        if (mFrankRuehlMedium == null) {
            mFrankRuehlMedium = Typeface.createFromAsset(context.getAssets(), FRANK_RUEHL_MEDIUM);
        }

        return mFrankRuehlMedium;
    }
}
