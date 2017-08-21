package com.webviewmarker.webmarker;

import android.webkit.JavascriptInterface;

public class TextSelectionController {
    public static final String INTERFACE_NAME = "TextSelection";
    public static final String TAG = "TextSelectionController";
    private TextSelectionControlListener textSelectionControlListener;

    public TextSelectionController(TextSelectionControlListener textSelectionControlListener) {
        this.textSelectionControlListener = textSelectionControlListener;
    }

    @JavascriptInterface
    public void endSelectionMode() {
        if (this.textSelectionControlListener != null) {
            this.textSelectionControlListener.endSelectionMode();
        }
    }

    @JavascriptInterface
    public void selectionChanged(String str, String str2, String str3, String str4, boolean z) {
        int i = 0;
        try {
            i = (int) Double.parseDouble(str4);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (this.textSelectionControlListener != null) {
            this.textSelectionControlListener.selectionChanged(str, str2, str3, i, z);
        }
    }

    @JavascriptInterface
    public void setContentWidth(float f) {
        if (this.textSelectionControlListener != null) {
            this.textSelectionControlListener.setContentWidth(f);
        }
    }

    @JavascriptInterface
    public void setSelectionMode(boolean z) {
        if (this.textSelectionControlListener != null) {
            this.textSelectionControlListener.setSelectionMode(true);
        }
    }

    @JavascriptInterface
    public void startSelectionMode() {
        if (this.textSelectionControlListener != null) {
            this.textSelectionControlListener.startSelectionMode();
        }
    }
}
