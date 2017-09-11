package com.getbooks.android.ui.fragments;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

        new RequestAsynTask().execute();
    }


    @Override
    public int getLayout() {
        return R.layout.webview_layout;
    }

    @Override
    public AuthorizationActivity getAct() {
        return (AuthorizationActivity) getActivity();
    }

    private class RequestAsynTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {
            return getUserToken();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("null")) {
                setUpWebView();
                mAuthorizationWebView.loadUrl(Const.PELEPHONE_MAGENTO_LOGIN_URI);
            } else {
                setUpWebView();
                mAuthorizationWebView.loadUrl(Const.PELEPHONE_MAGENTO_LOGIN_URI + "?UserToken=" + s);
            }
        }
    }

    private String getUserToken() {
        HttpURLConnection httpcon;
        String url = Const.PELE_API_GET_USER_TOKEN_BY_HI_ENDPOINT;
        String data = "{ApplicationID:" + Const.APPLICATION_ID + "}";
        String result = null;
        String userToken = null;
        try {
            //Connect
            httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();

            //Write
            OutputStream os = httpcon.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.close();
            os.close();

            //Read
            BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            result = sb.toString();

            JSONObject jsonObject = new JSONObject(result);
            jsonObject.get("UserToken");
            userToken = jsonObject.get("UserToken").toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userToken;
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
        webSettings.setUseWideViewPort(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        mAuthorizationWebView.setWebChromeClient(new WebChromeClient());
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
            super.onPageFinished(view, "onPageFinished" + url);
            LogUtil.log(this, url);
            String cookies = CookieManager.getInstance().getCookie(url);
            if (null != getAct())
                Prefs.saveCookieUserSession(getAct(), cookies);
            LogUtil.log(this, "Save user cookies session");
            mProgressBar.dismiss();
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
        LogUtil.log(this, "Save user token authorization" + token);
        if (Prefs.getCountTutorialsShow(getAct()) < Prefs.MAX_COUNT_VIEWS_TUTORIALS) {
            UiUtil.openActivity(getAct(), TutorialsActivity.class, true, "", "", "", "");
        } else {
            UiUtil.openActivity(getAct(), LibraryActivity.class, true, "", "", "", "");
        }
    }

    private void createUserSession(int userSessionId) {
        List<Integer> allUsersId = new ArrayList<>();
        allUsersId.addAll(BookDataBaseLoader.getInstanceDb(getAct()).getUsersIdSession());
        if (!allUsersId.isEmpty()) {
            if (!allUsersId.contains(userSessionId)) {
                BookDataBaseLoader.getInstanceDb(getAct()).createUserSession(userSessionId);
                Prefs.saveUserSession(getAct(), Const.USER_SESSION_ID, userSessionId);
            }
        } else {
            BookDataBaseLoader.getInstanceDb(getAct()).createUserSession(userSessionId);
            Prefs.saveUserSession(getAct(), Const.USER_SESSION_ID, userSessionId);
        }
    }
}
