package com.getbooks.android.ui.widget.left_menu_items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.getbooks.android.R;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.BaseFragment;

/**
 * Created by avi on 18.08.17.
 */

public class AlertDialogInstruction extends BaseFragment{
    WebView myBrowser;
    public static AlertDialogInstruction newInstance(){
        AlertDialogInstruction f = new AlertDialogInstruction();
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
    public int getLayout() {
        return R.id.webview_story;
    }

    @Override
    public <T extends BaseActivity> T getAct() {
        return null;
    }
}
