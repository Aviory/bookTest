package com.getbooks.android.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.getbooks.android.util.TypefaceUtil;

/**
 * Created by marina on 24.07.17.
 */

public class ArialNormalTextView extends ArialTextView {
    public ArialNormalTextView(Context context) {
        super(context);
    }

    public ArialNormalTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ArialNormalTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected Typeface getFont() {
        return TypefaceUtil.getArialTypeface(getContext());
    }
}
