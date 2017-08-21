package com.webviewmarker.drag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * A DragView is a special view used by a DragController. During a drag operation, what is actually moving
 * on the screen is a DragView. A DragView is constructed using a bitmap of the view the user really
 * wants to move.
 */
public class DragView extends View {
    private final int mRegistrationX;
    private final int mRegistrationY;
    private Bitmap mBitmap;
    private Paint mDebugPaint = new Paint();
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;


//    private WindowManager.LayoutParams mLayoutParams;

    /**
     * Construct the drag view.
     * <p>
     * The registration point is the point inside our view that the touch events should
     * be centered upon.
     *
     * @param context       A context
     * @param bitmap        The view that we're dragging around.  We scale it up when we draw it.
     * @param registrationX The x coordinate of the registration point.
     * @param registrationY The y coordinate of the registration point.
     */
    public DragView(Context context, Bitmap bitmap, int registrationX, int registrationY,
                    int left, int top, int width, int height) {
        super(context);
        mDebugPaint = new Paint();
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mRegistrationX = registrationX + 0;
        mRegistrationY = registrationY + 0;
        float scaleFactor = (((float) width) + 0.0f) / ((float) width);
        Matrix scale = new Matrix();
        scale.setScale(scaleFactor, scaleFactor);
        this.mBitmap = Bitmap.createBitmap(bitmap, left, top, width, height, scale, true);
    }

    void remove() {
        mWindowManager.removeView(this);
    }

    void move(int touchX, int touchY) {
        WindowManager.LayoutParams layoutParams = mLayoutParams;
        layoutParams.x = touchX - mRegistrationX;
        layoutParams.y = touchY - mRegistrationY;
        this.mWindowManager.updateViewLayout(this, layoutParams);
    }

    void show(IBinder windowToken, int touchX, int touchY) {
        WindowManager.LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                touchX - this.mRegistrationX, touchY - this.mRegistrationY,
                WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.token = windowToken;
        layoutParams.setTitle("DragView");
        this.mLayoutParams = layoutParams;
        this.mWindowManager.addView(this, layoutParams);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBitmap.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0.0f, 0.0f, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
    }
}
