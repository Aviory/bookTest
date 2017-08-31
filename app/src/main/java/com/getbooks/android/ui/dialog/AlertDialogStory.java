package com.getbooks.android.ui.dialog;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.activities.CatalogActivity;
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.util.LogUtil;

import java.util.HashMap;

/**
 * Created by avi on 17.08.17.
 */

public class AlertDialogStory extends DialogFragment {
    ImageView close;

    private WebView myBrowser;

    public static AlertDialogStory newInstance(){
        AlertDialogStory alertDialogStory = new AlertDialogStory();
        return alertDialogStory;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.webview_layout, container, false);
        myBrowser = (WebView)v.findViewById(R.id.webview);
        myBrowser.setHorizontalScrollBarEnabled(false);
        myBrowser.setVerticalScrollBarEnabled(false);
        WebSettings webSettings = myBrowser.getSettings();
        webSettings.setJavaScriptEnabled(true);

        HashMap<String, String> map = new HashMap<String, String>();
        String token = Prefs.getToken(getActivity());
        LogUtil.log(this, "token: "+token);
        map.put("deviceTok", token);
        myBrowser.loadUrl("https://pelephone.getbooks.co.il/dev/glibrary/bookrent/showrentbook", map);
        close = (ImageView) v.findViewById(R.id.img_close_catalog);
        close.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dismiss();
                close.setVisibility(View.GONE);
            }
        });
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        close.setVisibility(View.VISIBLE);
    }
    @Override
    public void onStop() {
        super.onStop();
        close.setVisibility(View.GONE);
    }
}