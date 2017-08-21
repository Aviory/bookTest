package com.getbooks.android.ui.widget.left_menu_items;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;


/**
 * Created by avi on 21.08.17.
 */

public class CustomSeekBar extends AppCompatSeekBar {
    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        int thumb_y = (int) (( (double)this.getProgress()/this.getMax() ) * (double)this.getHeight());
        int middle = this.getHeight()/2;
        // your drawing code here, ie Canvas.drawText();
    }
}
