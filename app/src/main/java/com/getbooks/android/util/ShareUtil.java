package com.getbooks.android.util;

import android.app.Activity;

import com.getbooks.android.facebook.FacebookHelper;

/**
 * Created by marinaracu on 08.09.17.
 */

public class ShareUtil {

    public static void shareToFacebook(Activity activity, String url) {
        FacebookHelper helper = new FacebookHelper(activity);
        helper.shareLink(url);
    }

}
