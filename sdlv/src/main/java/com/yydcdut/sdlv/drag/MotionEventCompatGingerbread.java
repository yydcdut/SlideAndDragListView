package com.yydcdut.sdlv.drag;

import android.view.MotionEvent;

/**
 * Motion event compatibility class for API 8+.
 */
class MotionEventCompatGingerbread {
    public static int getSource(MotionEvent event) {
        return event.getSource();
    }
}