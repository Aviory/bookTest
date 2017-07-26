package com.getbooks.android.ui.activities;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.dialog.MaterialDialog;
import com.getbooks.android.util.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marina on 26.07.17.
 */

public class CatalogActivity extends BaseActivity {

    @BindView(R.id.webview)
    protected WebView mCatalogWebView;
    @BindView(R.id.img_close_catalog)
    protected ImageView mImageCloseCatalog;
    private AlertDialog mProgressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressBar = new MaterialDialog(this);

        mImageCloseCatalog.setVisibility(View.VISIBLE);

        setUpWebView();
    }

    @Override
    public int getLayout() {
        return R.layout.webview_layout;
    }

    @OnClick(R.id.img_close_catalog)
    protected void closeCatalog() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mImageCloseCatalog.setVisibility(View.GONE);
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }

    private void setUpWebView() {
//        clearHash();
        CookieManager.getInstance().removeAllCookie();
        mCatalogWebView.setHorizontalScrollBarEnabled(false);
        mCatalogWebView.setVerticalScrollBarEnabled(false);
        mCatalogWebView.setWebViewClient(new CatalogWebViewClient());
        WebSettings webSettings = mCatalogWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mCatalogWebView.loadUrl(Const.CATALOG_URL);
    }

    private void clearHash() {
        mCatalogWebView.clearCache(true);
        mCatalogWebView.clearHistory();
        mCatalogWebView.clearFormData();
    }

    private void finishWithError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class CatalogWebViewClient extends WebViewClient {
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
