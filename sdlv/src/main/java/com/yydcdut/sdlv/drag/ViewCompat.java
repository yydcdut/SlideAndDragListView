package com.yydcdut.sdlv.drag;


import android.view.View;

import java.lang.reflect.Field;

/**
 * Helper for accessing features in {@link View} introduced after API
 * level 4 in a backwards compatible fashion.
 */
public class ViewCompat {

    interface ViewCompatImpl {
        public boolean canScrollHorizontally(View v, int direction);

        public boolean canScrollVertically(View v, int direction);
    }

    static class BaseViewCompatImpl implements ViewCompatImpl {

        public boolean canScrollHorizontally(View v, int direction) {
            return (v instanceof ScrollingView) &&
                    canScrollingViewScrollHorizontally((ScrollingView) v, direction);
        }

        public boolean canScrollVertically(View v, int direction) {
            return (v instanceof ScrollingView) &&
                    canScrollingViewScrollVertically((ScrollingView) v, direction);
        }

        private boolean canScrollingViewScrollHorizontally(ScrollingView view, int direction) {
            final int offset = view.computeHorizontalScrollOffset();
            final int range = view.computeHorizontalScrollRange() -
                    view.computeHorizontalScrollExtent();
            if (range == 0) {
                return false;
            }
            if (direction < 0) {
                return offset > 0;
            } else {
                return offset < range - 1;
            }
        }

        private boolean canScrollingViewScrollVertically(ScrollingView view, int direction) {
            final int offset = view.computeVerticalScrollOffset();
            final int range = view.computeVerticalScrollRange() -
                    view.computeVerticalScrollExtent();
            if (range == 0) {
                return false;
            }
            if (direction < 0) {
                return offset > 0;
            } else {
                return offset < range - 1;
            }
        }

    }

    static class EclairMr1ViewCompatImpl extends BaseViewCompatImpl {

    }

    static class GBViewCompatImpl extends EclairMr1ViewCompatImpl {

    }

    static class HCViewCompatImpl extends GBViewCompatImpl {

    }

    static class ICSViewCompatImpl extends HCViewCompatImpl {
        static Field mAccessibilityDelegateField;
        static boolean accessibilityDelegateCheckFailed = false;

        @Override
        public boolean canScrollHorizontally(View v, int direction) {
            return ViewCompatICS.canScrollHorizontally(v, direction);
        }

        @Override
        public boolean canScrollVertically(View v, int direction) {
            return ViewCompatICS.canScrollVertically(v, direction);
        }
    }

    static class JBViewCompatImpl extends ICSViewCompatImpl {

    }

    static class JbMr1ViewCompatImpl extends JBViewCompatImpl {

    }

    static class KitKatViewCompatImpl extends JbMr1ViewCompatImpl {
    }

    static class LollipopViewCompatImpl extends KitKatViewCompatImpl {
    }

    static final ViewCompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LollipopViewCompatImpl();
        } else if (version >= 19) {
            IMPL = new KitKatViewCompatImpl();
        } else if (version >= 17) {
            IMPL = new JbMr1ViewCompatImpl();
        } else if (version >= 16) {
            IMPL = new JBViewCompatImpl();
        } else if (version >= 14) {
            IMPL = new ICSViewCompatImpl();
        } else if (version >= 11) {
            IMPL = new HCViewCompatImpl();
        } else if (version >= 9) {
            IMPL = new GBViewCompatImpl();
        } else if (version >= 7) {
            IMPL = new EclairMr1ViewCompatImpl();
        } else {
            IMPL = new BaseViewCompatImpl();
        }
    }

    /**
     * Check if this view can be scrolled horizontally in a certain direction.
     *
     * @param v         The View against which to invoke the method.
     * @param direction Negative to check scrolling left, positive to check scrolling right.
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    public static boolean canScrollHorizontally(View v, int direction) {
        return IMPL.canScrollHorizontally(v, direction);
    }

    /**
     * Check if this view can be scrolled vertically in a certain direction.
     *
     * @param v         The View against which to invoke the method.
     * @param direction Negative to check scrolling up, positive to check scrolling down.
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    public static boolean canScrollVertically(View v, int direction) {
        return IMPL.canScrollVertically(v, direction);
    }

}


