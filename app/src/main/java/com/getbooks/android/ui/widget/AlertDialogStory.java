package com.getbooks.android.ui.widget;

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
import android.webkit.WebView;
import android.widget.ImageView;

import com.getbooks.android.R;
import com.getbooks.android.api.QueriesTexts;
import com.getbooks.android.model.RequestModel;
import com.getbooks.android.model.Text;
import com.getbooks.android.util.LogUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by avi on 17.08.17.
 */

public class AlertDialogStory extends DialogFragment {
    public AlertDialogStory(){}
    WebView myBrowser;
    public static AlertDialogStory newInstance(){
        AlertDialogStory f = new AlertDialogStory();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.left_menu_story, container, false);
        myBrowser = (WebView)v.findViewById(R.id.webview_story);
        myBrowser.loadUrl("https://pelephone.getbooks.co.il/dev/glibrary/bookrent/showrentbook");
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
    }
}