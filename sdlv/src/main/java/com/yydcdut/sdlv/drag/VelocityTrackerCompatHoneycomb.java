package com.yydcdut.sdlv.drag;

import android.view.VelocityTracker;

/**
 * Created by yuyidong on 15/12/8.
 */
class VelocityTrackerCompatHoneycomb {
    public static float getXVelocity(VelocityTracker tracker, int pointerId) {
        return tracker.getXVelocity(pointerId);
    }

    public static float getYVelocity(VelocityTracker tracker, int pointerId) {
        return tracker.getYVelocity(pointerId);
    }
}
