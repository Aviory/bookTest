package com.getbooks.android.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.api.ApiManager;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.dialog.MaterialDialog;
import com.getbooks.android.util.LogUtil;
import com.getbooks.android.util.UiUtil;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;

/**
 * Created by marina on 12.07.17.
 */

public class AuthorizationActivity extends BaseActivity {

    @BindView(R.id.authorization_webview)
    protected WebView mAuthorizationWebView;
    private AlertDialog mProgressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressBar = new MaterialDialog(this);

        checkUserAuthorization();
    }

    private void checkUserAuthorization() {
        if (Prefs.getBooleanProperty(this, Const.IS_USER_AUTHORIZE)) {
            Intent intent = new Intent(this, LibraryActivity.class);
            startActivity(intent);
            UiUtil.clearStack(this);
        } else {
            setUpWebView();
        }
    }

    @Override
    public int getLayout() {
        return R.layout.authorization_activity_layout;
    }

    private void setUpWebView() {
//        clearHash();
        CookieManager.getInstance().removeAllCookie();
        mAuthorizationWebView.setHorizontalScrollBarEnabled(false);
        mAuthorizationWebView.setVerticalScrollBarEnabled(false);
        mAuthorizationWebView.setWebViewClient(new LoginWebViewClient());
        WebSettings webSettings = mAuthorizationWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mAuthorizationWebView.loadUrl(Const.AUTH_URL);
    }

    private void clearHash() {
        mAuthorizationWebView.clearCache(true);
        mAuthorizationWebView.clearHistory();
        mAuthorizationWebView.clearFormData();
    }

    private void finishWithError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private class LoginWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!isFinishing()) {
                mProgressBar.show();
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.log(this, "Redirecting URL 0" + url);
            if (url.startsWith(Const.REDIRECT_URL)) {
                ApiManager.registerDeviseToken(FirebaseInstanceId.getInstance().getToken(), AuthorizationActivity.this);
                return true;
            }
            return false;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            LogUtil.log(this, "Redirecting URL 0" + url);
            if (url.startsWith(Const.REDIRECT_URL)) {
                ApiManager.registerDeviseToken(FirebaseInstanceId.getInstance().getToken(), AuthorizationActivity.this);
                return true;
            }
            return false;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mProgressBar.dismiss();
            finishWithError(description);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            mProgressBar.dismiss();
            finishWithError(error.getDescription().toString());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.dismiss();
        }
    }
}
