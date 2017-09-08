package com.getbooks.android.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.ui.widget.ArialNormalTextView;

/**
 * Created by avi on 21.08.17.
 */

public class AlertDialogInstructions extends DialogFragment {
    ImageView close;

    public static AlertDialogInstructions newInstance(){
        AlertDialogInstructions alertDialogInstructions = new AlertDialogInstructions();
        return alertDialogInstructions;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.webview_layout, container, false);
        WebView myBrowser = (WebView)v.findViewById(R.id.webview);
        myBrowser.setHorizontalScrollBarEnabled(false);
        myBrowser.setVerticalScrollBarEnabled(false);
        WebSettings webSettings = myBrowser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myBrowser.setWebViewClient(new WebViewClient());
        myBrowser.loadUrl(Const.BOOK_INSTRUCTIONS_DIALOG_URL);

        close = (ImageView) v.findViewById(R.id.img_close_catalog);
        close.setVisibility(View.VISIBLE);
        ArialNormalTextView text = (ArialNormalTextView) v.findViewById(R.id.txt_about);

        close.setOnClickListener(new View.OnClickListener() {
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
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        close.setVisibility(View.GONE);
    }
}

