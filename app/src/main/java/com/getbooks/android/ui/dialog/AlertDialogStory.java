package com.getbooks.android.ui.dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
    private AlertDialog mProgressBar;

    private WebView myBrowser;

    public static AlertDialogStory newInstance(){
        AlertDialogStory alertDialogStory = new AlertDialogStory();
        return alertDialogStory;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.webview_layout, container, false);

        mProgressBar = new MaterialDialog(getContext());

        myBrowser = (WebView)v.findViewById(R.id.webview);
        setUpWebView();
        close = (ImageView) v.findViewById(R.id.img_close_catalog);
        close.setVisibility(View.INVISIBLE);
        close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                close.setVisibility(View.GONE);
                dismiss();
            }
        });

        return v;
    }

    private void setUpWebView() {
        myBrowser.setHorizontalScrollBarEnabled(false);
        myBrowser.setVerticalScrollBarEnabled(false);
        WebSettings webSettings = myBrowser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myBrowser.setWebViewClient(new CatalogWebViewClient());

        HashMap<String, String> map = new HashMap<String, String>();
        String token = Prefs.getToken(getActivity());
        LogUtil.log(this, "token: "+token);
        map.put("deviceTok", token);
        myBrowser.loadUrl(Const.BOOK_STORY_DIALOG_URL, map);
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
        mProgressBar.show();
    }
    @Override
    public void onStop() {
        super.onStop();
        close.setVisibility(View.GONE);
    }

    private class CatalogWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            close.setVisibility(View.INVISIBLE);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.log(this, "Redirecting URL " + url);
            if (url.startsWith(Const.REDIRECT_URL)) {
                return true;
            }
            return false;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            LogUtil.log(this, "Redirecting URL " + url);
            if (url.startsWith(Const.REDIRECT_URL)) {
                return true;
            }
            return false;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mProgressBar.dismiss();
            close.setVisibility(View.VISIBLE);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            mProgressBar.dismiss();
            close.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.dismiss();
            close.setVisibility(View.VISIBLE);
        }
    }
}