package com.webviewmarker.drag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

public class DragController {
    private Context mContext;
    private Rect mRectTemp = new Rect();
    private final int[] mCoordinatesTemp = new int[2];
    private boolean mDragging;
    private float mMotionDownX;
    private float mMotionDownY;
    private DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    /**
     * Original view that is being dragged.
     */
    private View mOriginator;
    /**
     * X offset from the upper-left corner of the cell to where we touched.
     */
    private float mTouchOffsetX;
    /**
     * Y offset from the upper-left corner of the cell to where we touched.
     */
    private float mTouchOffsetY;
    /**
     * Where the drag originated
     */
    private DragSource mDragSource;
    /**
     * The data associated with the object being dragged
     */
    private Object mDragInfo;
    /**
     * The view that moves around while you drag.
     */
    private DragView mDragView;
    /**
     * Who can receive drop events
     */
    private ArrayList<DropTarget> mDropTargets = new ArrayList<DropTarget>();
    private DragListener mListener;
    /**
     * The window token used as the parent for the DragView.
     */
    private IBinder mWindowToken;
    private View mMoveTarget;
    private DropTarget mLastDropTarget;
    private InputMethodManager mInputMethodManager;


    public enum DragBehavior {
        MOVE,
        COPY
    }

    /**
     * Used to create a new DragLayer from XML.
     *
     * @param context The application's context.
     */
    public DragController(Context context) {
        mContext = context;
    }

    /**
     * Draw the view into a bitmap.
     */
    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("DragController", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    private DropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
        final Rect r = mRectTemp;
        final ArrayList<DropTarget> dropTargets = mDropTargets;
        final int count = dropTargets.size();
        for (int i = count - 1; i >= 0; i--) {
            final DropTarget target = dropTargets.get(i);
            target.getHitRect(r);
            target.getLocationOnScreen(dropCoordinates);
            r.offset(dropCoordinates[0] - target.getLeft(), dropCoordinates[1] - target.getTop());
            if (r.contains(x, y)) {
                dropCoordinates[0] = x - dropCoordinates[0];
                dropCoordinates[1] = y - dropCoordinates[1];
                return target;
            }
        }
        return null;
    }

    private void recordScreenSize() {
        ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mDisplayMetrics);
    }
    private static int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        }
        else if (val >= max) {
            return max - 1;
        }
        else {
            return val;
        }
    }



    /**
     * Starts a drag.
     *
     * @param b             The bitmap to display as the drag image.  It will be re-scaled to the
     *                      enlarged size.
     * @param screenX       The x position on screen of the left-top of the bitmap.
     * @param screenY       The y position on screen of the left-top of the bitmap.
     * @param textureLeft   The left edge of the region inside b to use.
     * @param textureTop    The top edge of the region inside b to use.
     * @param textureWidth  The width of the region inside b to use.
     * @param textureHeight The height of the region inside b to use.
     * @param source        An object representing where the drag originated
     * @param dragInfo      The data associated with the object that is being dragged
     * @param dragBehavior  The drag action: move or copy
     */
    private void startDrag(Bitmap b, int screenX, int screenY, int textureLeft, int textureTop, int textureWidth, int textureHeight, DragSource source, Object dragInfo, DragBehavior dragBehavior) {
        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        mInputMethodManager.hideSoftInputFromWindow(mWindowToken, 0);
        if (mListener != null) {
            mListener.onDragStart(source, dragInfo, dragBehavior);
        }
        final int registrationX = ((int) mMotionDownX) - screenX;
        final int registrationY = ((int) mMotionDownY) - screenY;
        mTouchOffsetX = mMotionDownX - screenX;
        mTouchOffsetY = mMotionDownY - screenY;
        mDragging = true;
        mDragSource = source;
        mDragInfo = dragInfo;
        mDragView = new DragView(mContext, b, registrationX, registrationY, textureLeft, textureTop, textureWidth, textureHeight);
        mDragView.show(mWindowToken, (int) mMotionDownX, (int) mMotionDownY);
    }

    private boolean drop(float x, float y) {
        final int[] coordinates = mCoordinatesTemp;
        final DropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);
        if (dropTarget != null) {
            dropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragView, mDragInfo);
            if (dropTarget.acceptDrop(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo)) {
                dropTarget.onDrop(mDragSource, coordinates[0], coordinates[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragView, mDragInfo);
                mDragSource.onDropCompleted((View)dropTarget, true);
            }
            else {
                mDragSource.onDropCompleted((View)dropTarget, false);
            }
            return true;
        }
        return false;
    }

    private void endDrag() {
        if (mDragging) {
            mDragging = false;
            if (mOriginator != null) {
                mOriginator.setVisibility(View.VISIBLE);
            }
            if (mListener != null) {
                mListener.onDragEnd();
            }
            if (mDragView != null) {
                mDragView.remove();
                mDragView = null;
            }
        }
    }

    public void cancelDrag() {
        endDrag();
    }

    public void startDrag(View v, DragSource source, Object dragInfo, DragBehavior dragBehavior) {
        if (source.allowDrag()) {
            mOriginator = v;
            final Bitmap b = getViewBitmap(v);
            if (b != null) {
                final int[] loc = mCoordinatesTemp;
                v.getLocationOnScreen(loc);
                final int screenX = loc[0];
                final int screenY = loc[1];
                startDrag(b, screenX, screenY, 0, 0, b.getWidth(), b.getHeight(), source, dragInfo, dragBehavior);
                b.recycle();
                if (dragBehavior == DragBehavior.MOVE) {
                    v.setVisibility(View.GONE);
                }
            }
        }
    }


    public void setDragListener(DragListener dragListener) {
        mListener = dragListener;
    }

    public void addDropTarget(DropTarget dropTarget) {
        mDropTargets.add(dropTarget);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return mDragging;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            recordScreenSize();
        }
        float screenX = DragController.clamp((int) motionEvent.getRawX(), 0, this.mDisplayMetrics.widthPixels);
        float screenY = DragController.clamp((int) motionEvent.getRawY(), 0, this.mDisplayMetrics.heightPixels);
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                mMotionDownX = screenX;
                mMotionDownY = screenY;
                mLastDropTarget = null;
                break;
            case  MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (this.mDragging) {
                    drop(screenX, screenY);
                }
                endDrag();
                break;
        }
        return this.mDragging;
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        return this.mMoveTarget != null && this.mMoveTarget.dispatchUnhandledMove(view, i);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!mDragging) {
            return false;
        }

        final int action = motionEvent.getAction();
        final int screenX = clamp((int) motionEvent.getRawX(), 0, mDisplayMetrics.widthPixels);
        final int screenY = clamp((int) motionEvent.getRawY(), 0, mDisplayMetrics.heightPixels);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mMotionDownX = screenX;
                mMotionDownY = screenY;
                break;
            case MotionEvent.ACTION_MOVE:
                mDragView.move((int)motionEvent.getRawX(), (int)motionEvent.getRawY());
                final int[] coordinates = mCoordinatesTemp;
                DropTarget dropTarget = findDropTarget(screenX, screenY, coordinates);
                if (dropTarget != null) {
                    if (mLastDropTarget == dropTarget) {
                        dropTarget.onDragOver(mDragSource, coordinates[0], coordinates[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragView, mDragInfo);
                    }
                    else {
                        if (mLastDropTarget != null) {
                            mLastDropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
                        }
                        dropTarget.onDragEnter(mDragSource, coordinates[0], coordinates[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragView, mDragInfo);
                    }
                }
                else {
                    if (mLastDropTarget != null) {
                        mLastDropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragView, mDragInfo);
                    }
                }
                mLastDropTarget = dropTarget;
                break;
            case MotionEvent.ACTION_UP:
                if (mDragging) {
                    drop(screenX, screenY);
                }
                endDrag();
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelDrag();
        }
        return true;
    }
}
