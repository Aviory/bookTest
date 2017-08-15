package com.getbooks.android.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by marina on 24.07.17.
 */

public abstract class FrankRuehlTextView extends android.support.v7.widget.AppCompatTextView {
    public FrankRuehlTextView(Context context) {
        super(context);
        init();
    }

    public FrankRuehlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrankRuehlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected abstract Typeface getFont();

    private void init() {
        setTypeface(getFont());
    }
}
