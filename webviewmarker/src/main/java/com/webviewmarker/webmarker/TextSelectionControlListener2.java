package com.webviewmarker.webmarker;

public interface TextSelectionControlListener2 {
    void endSelectionMode();

    void jsError(String str);

    void jsLog(String str);

    void selectionChanged(String str, String str2, String str3, boolean z);

    void setContentWidth(float f);

    void startSelectionMode();
}
