package com.webviewmarker.drag;

import android.graphics.Rect;

/**
 * Interface defining an object that can receive a view at the end of a drag operation.
 */
public interface DropTarget {

    /**
     * Handle an object being dropped on the DropTarget
     *
     * @param dragSource   DragSource where the drag started
     * @param x        X coordinate of the drop location
     * @param y        Y coordinate of the drop location
     * @param xOffset  Horizontal offset with the object being dragged where the original
     *                 touch happened
     * @param yOffset  Vertical offset with the object being dragged where the original
     *                 touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     */
    void onDrop(DragSource dragSource, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo);

    void onDragEnter(DragSource dragSource, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo);

    void onDragOver(DragSource dragSource, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo);

    void onDragExit(DragSource dragSource, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo);

    /**
     * Check if a drop action can occur at, or near, the requested location.
     * This may be called repeatedly during a drag, so any calls should return
     * quickly.
     *
     * @param dragSource   DragSource where the drag started
     * @param x        X coordinate of the drop location
     * @param y        Y coordinate of the drop location
     * @param xOffset  Horizontal offset with the object being dragged where the
     *                 original touch happened
     * @param yOffset  Vertical offset with the object being dragged where the
     *                 original touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     * @return True if the drop will be accepted, false otherwise.
     */
    boolean acceptDrop(DragSource dragSource, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo);

    // These methods are implemented in Views
    void getHitRect(Rect rect);

    int getLeft();

    void getLocationOnScreen(int[] iArr);

    int getTop();
}
