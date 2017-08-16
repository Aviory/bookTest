package com.webviewmarker.webmarker;

public interface TextSelectionControlListener {
    void endSelectionMode();

    void selectionChanged(String str, String str2, String str3, int i, boolean z);

    void setContentWidth(float f);

    void setSelectionMode(boolean z);

    void startSelectionMode();
}
