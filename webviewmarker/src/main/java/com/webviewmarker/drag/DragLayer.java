package com.webviewmarker.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;


/**
 * A ViewGroup that coordinates dragging across its descendants.
 * <p>
 * <p> This class used DragLayer in the Android Launcher activity as a model.
 * It is a bit different in several respects:
 * (1) It extends MyAbsoluteLayout rather than FrameLayout; (2) it implements DragSource and DropTarget methods
 * that were done in a separate Workspace class in the Launcher.
 */
public class DragLayer extends MyAbsoluteLayout implements DragSource, DropTarget {
    private DragController mDragController;

    public DragLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Interfaces of DropTarget
    @Override
    public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
        View view = (View) dragInfo;
        updateViewLayout(view, new LayoutParams(view.getWidth(), view.getHeight(), x - xOffset, y - yOffset));
    }

    @Override
    public void onDragEnter(DragSource dragSource, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {

    }

    @Override
    public void onDragOver(DragSource dragSource, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {

    }

    @Override
    public void onDragExit(DragSource dragSource, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {

    }

    @Override
    public boolean acceptDrop(DragSource dragSource, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mDragController.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragController.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDragController.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        return mDragController.dispatchUnhandledMove(focused, direction);
    }

    @Override
    public void onDropCompleted(View target, boolean success) {

    }

    // Interfaces of DragSource
    @Override
    public boolean allowDrag() {
        return true;
    }

    public void setDragController(DragController dragController) {
        this.mDragController = dragController;
    }
}
