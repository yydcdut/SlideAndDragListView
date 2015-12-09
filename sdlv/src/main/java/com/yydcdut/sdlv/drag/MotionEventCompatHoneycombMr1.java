package com.yydcdut.sdlv.drag;

import android.view.MotionEvent;

class MotionEventCompatHoneycombMr1 {
    static float getAxisValue(MotionEvent event, int axis) {
        return event.getAxisValue(axis);
    }

    static float getAxisValue(MotionEvent event, int axis, int pointerIndex) {
        return event.getAxisValue(axis, pointerIndex);
    }
}
