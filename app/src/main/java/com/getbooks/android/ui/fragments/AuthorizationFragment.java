package com.getbooks.android.ui.fragments;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.AuthorizationActivity;
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.ui.activities.TutorialsActivity;
import com.getbooks.android.ui.dialog.MaterialDialog;
import com.getbooks.android.util.LogUtil;
import com.getbooks.android.util.UiUtil;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;

/**
 * Created by marina on 14.07.17.
 */

public class AuthorizationFragment extends BaseFragment {

    @BindView(R.id.webview)
    protected WebView mAuthorizationWebView;
    private AlertDialog mProgressBar;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = new MaterialDialog(getContext());

        setUpWebView();
    }


    @Override
    public int getLayout() {
        return R.layout.webview_layout;
    }

    @Override
    public AuthorizationActivity getAct() {
        return (AuthorizationActivity) getActivity();
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
        webSettings.setUseWideViewPort(true);
        mAuthorizationWebView.loadUrl(Const.AUTH_URL);
    }

    private void clearHash() {
        mAuthorizationWebView.clearCache(true);
        mAuthorizationWebView.clearHistory();
        mAuthorizationWebView.clearFormData();
    }

    private void finishWithError(String message) {
        LogUtil.log(this, message);
    }

    private class LoginWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!getActivity().isFinishing()) {
                mProgressBar.show();
                LogUtil.log(this, "onPageStarted " + url);
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.log(this, "Redirecting URL " + url);
            if (url.startsWith(Const.REDIRECT_URL)) {
                String token = FirebaseInstanceId.getInstance().getToken();

                registerToken(view, token);
                createUserSession(552288);
                goToUserLibrary(token);
                return true;
            }
            return false;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            LogUtil.log(this, "Redirecting URL " + url);
            if (url.equals(Const.REDIRECT_URL)) {
                String token = FirebaseInstanceId.getInstance().getToken();

                registerToken(view, token);
                createUserSession(552288);
                goToUserLibrary(token);

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
            LogUtil.log(this, url + "last");
            mProgressBar.dismiss();
            // Display the keyboard automatically when relevant
//            if (view.getOriginalUrl().equals(Const.AUTH_URL)) {
//                Log.d("AAAAAAAAAAA", "seeeeee");
//                InputMethodManager imm = (InputMethodManager) getAct().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(view, 0);
//            }
        }
    }

    private void registerToken(WebView view, String token) {
        String registerUrl = Const.BASE_URL_PELEPHONE_API + "DEVICE_TOKEN=" + token + "&DEVICE_OS=1";
        String urlParameters = "DEVICE_TOKEN=" + token + "&DEVICE_OS=1";
        LogUtil.log(this, registerUrl);
        view.postUrl(registerUrl, urlParameters.getBytes());
    }

    private void goToUserLibrary(String token) {
        Prefs.setBooleanProperty(getAct(), Const.IS_USER_AUTHORIZE, true);
        Prefs.putToken(getAct(), token);
        if (Prefs.getCountTutorialsShow(getAct()) < Prefs.MAX_COUNT_VIEWS_TUTORIALS) {
            UiUtil.openActivity(getAct(), TutorialsActivity.class, true);
        } else {
            UiUtil.openActivity(getAct(), LibraryActivity.class, true);
        }
    }

    private void createUserSession(int userSessionId) {
        BookDataBaseLoader.createBookDBLoader(getAct()).createUserSession(userSessionId);
        Prefs.saveUserSession(getAct(), Const.USER_SESSION_ID, userSessionId);
    }
}
