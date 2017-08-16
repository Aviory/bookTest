package com.webviewmarker.drag;

import com.webviewmarker.drag.DragController.DragBehavior;

/**
 * Interface to receive notifications when a drag starts or stops
 */
public interface DragListener {
    void onDragEnd();

    void onDragStart(DragSource dragSource, Object info, DragBehavior dragBehavior);
}
