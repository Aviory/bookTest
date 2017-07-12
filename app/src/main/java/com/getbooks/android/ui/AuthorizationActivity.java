package com.getbooks.android.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.util.LogUtil;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;

/**
 * Created by marina on 12.07.17.
 */

public class AuthorizationActivity extends BaseActivity {

    private static final String ACCESS_TOKEN = "access_token";
    private static final String ERROR = "error";
    private static final String EQUAL = "=";
    @BindView(R.id.authorization_webview)
    protected WebView mAuthorizationWebView;
    private AlertDialog mProgressBar;
    private String mAuthUrl;
    private String mRedirectUrl;
    private AuthHelper mAuthHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuthUrl = Const.AUTH_URL;
        mRedirectUrl = Const.REDIRECT_URL + FirebaseInstanceId.getInstance().getToken();
        setUpWebView();
    }

    @Override
    public int getLayout() {
        return R.layout.authorization_activity_layout;
    }

    private void setUpWebView() {
        clearHash();
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
        mAuthorizationWebView.loadUrl(mAuthUrl);
    }

    private void clearHash() {
        mAuthorizationWebView.clearCache(true);
        mAuthorizationWebView.clearHistory();
        mAuthorizationWebView.clearFormData();
    }

    private void finishWithError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_CANCELED, null);
        finish();
    }

    private class LoginWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.log(this, "Redirecting URL 0" + url);
            if (url.startsWith(mRedirectUrl)) {
                if (url.contains(ACCESS_TOKEN)) {
                    String[] token = url.split(EQUAL);
                } else if (url.contains(ERROR)) {
                    String[] message = url.split(EQUAL);
                    finishWithError(message[message.length - 1]);
                }

                return true;
            }
            return false;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.startsWith(mRedirectUrl)) {
                if (url.contains(ACCESS_TOKEN)) {
                    String[] token = url.split(EQUAL);
                } else if (url.contains(ERROR)) {
                    String[] message = url.split(EQUAL);
                    finishWithError(message[message.length - 1]);
                }

                return true;
            }
            return false;
        }
    }
}
