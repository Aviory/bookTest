package com.webviewmarker.webmarker;

import android.webkit.JavascriptInterface;

public class TextSelectionController2 {
    public static final String INTERFACE_NAME = "TextSelection";
    public static final String TAG = "TextSelectionController";
    private TextSelectionControlListener2 textSelectionControlListener2;

    public TextSelectionController2(TextSelectionControlListener2 textSelectionControlListener2) {
        this.textSelectionControlListener2 = textSelectionControlListener2;
    }

    @JavascriptInterface
    public void endSelectionMode() {
        if (this.textSelectionControlListener2 != null) {
            this.textSelectionControlListener2.endSelectionMode();
        }
    }

    @JavascriptInterface
    public void jsError(String str) {
        if (this.textSelectionControlListener2 != null) {
            this.textSelectionControlListener2.jsError(str);
        }
    }

    @JavascriptInterface
    public void jsLog(String str) {
        if (this.textSelectionControlListener2 != null) {
            this.textSelectionControlListener2.jsLog(str);
        }
    }

    @JavascriptInterface
    public void selectionChanged(String str, String str2, String str3, boolean z) {
        if (this.textSelectionControlListener2 != null) {
            this.textSelectionControlListener2.selectionChanged(str, str2, str3, z);
        }
    }

    @JavascriptInterface
    public void setContentWidth(float f) {
        if (this.textSelectionControlListener2 != null) {
            this.textSelectionControlListener2.setContentWidth(f);
        }
    }

    @JavascriptInterface
    public void startSelectionMode() {
        if (this.textSelectionControlListener2 != null) {
            this.textSelectionControlListener2.startSelectionMode();
        }
    }
}
