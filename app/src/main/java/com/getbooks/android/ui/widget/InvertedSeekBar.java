package com.getbooks.android.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by marinaracu on 19.08.17.
 */

public class InvertedSeekBar extends AppCompatSeekBar {

    public InvertedSeekBar(Context context) {
        super(context);
    }

    public InvertedSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public InvertedSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-180.0f);
        canvas.translate((float) (-getWidth()), (float) (-getHeight()));
        super.onDraw(canvas);
    }
}
