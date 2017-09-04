package com.getbooks.android.reader;

/**
 * Created by marinaracu on 01.09.17.
 */

public interface HtmlTaskCallback extends BaseMvpView {
    void onReceiveHtml(String html);
}

