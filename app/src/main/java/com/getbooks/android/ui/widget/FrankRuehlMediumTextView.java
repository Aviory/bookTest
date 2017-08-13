package com.getbooks.android.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.getbooks.android.util.TypefaceUtil;

/**
 * Created by marina on 24.07.17.
 */

public class FrankRuehlMediumTextView extends FrankRuehlTextView {
    public FrankRuehlMediumTextView(Context context) {
        super(context);
    }

    public FrankRuehlMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrankRuehlMediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected Typeface getFont() {
        return TypefaceUtil.getFrankRuehlMediumTypeface(getContext());
    }
}
