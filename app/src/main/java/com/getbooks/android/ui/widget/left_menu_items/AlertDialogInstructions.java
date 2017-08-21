package com.getbooks.android.ui.widget.left_menu_items;

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
import com.getbooks.android.ui.widget.ArialNormalTextView;

/**
 * Created by avi on 21.08.17.
 */

public class AlertDialogInstructions extends DialogFragment {

        public static AlertDialogInstructions newInstance(){

            AlertDialogInstructions f = new AlertDialogInstructions();
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.left_menu_story, container, false);
            WebView myBrowser = (WebView)v.findViewById(R.id.webview_story);
            myBrowser.loadUrl("https://pelephone.getbooks.co.il/dev/how-it-works");

            ImageView close = (ImageView) v.findViewById(R.id.about_us_close);
            ArialNormalTextView text = (ArialNormalTextView) v.findViewById(R.id.txt_about);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
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
    }

