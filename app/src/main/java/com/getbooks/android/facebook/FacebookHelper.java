package com.getbooks.android.facebook;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by marinaracu on 08.09.17.
 */

public class FacebookHelper {
    private Context mContext;
    private CallbackManager mCallbackManager;
    private Activity mActivity;
    private Fragment mFragment;

    private String TAG = "FacebookLog";

    public FacebookHelper(Activity activity) {
        this.mContext = activity.getApplicationContext();
        mActivity = activity;
    }

    public FacebookHelper(Fragment fragment) {
        this.mContext = fragment.getActivity().getApplicationContext();
        mFragment = fragment;
    }

    public void shareLink(String url) {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url)).build();

        showShareDialog(content);
    }

    /**
     * Shows Share Dialog
     *
     * @param content content to share
     */
    private void showShareDialog(ShareContent content) {
        if (mActivity != null) {
            ShareDialog.show(mActivity, content);
        } else if (mFragment != null) {
            ShareDialog.show(mFragment, content);
        }
    }

}
