package com.getbooks.android.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by marina on 24.07.17.
 */

public abstract class ArialTextView extends android.support.v7.widget.AppCompatTextView {

    public ArialTextView(Context context) {
        super(context);
        init();
    }

    public ArialTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArialTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected abstract Typeface getFont();

    private void init(){
        setTypeface(getFont());
    }
}
