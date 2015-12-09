package com.yydcdut.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.yydcdut.sdlv.drag.MotionEventCompat;
import com.yydcdut.sdlv.drag.ViewDragHelper;

/**
 * Created by yuyidong on 15/12/8.
 */
public class DragListView2 extends ListView {
    private ViewDragHelper mDragHelper;
    private boolean mIsDrag = false;

    public DragListView2(Context context) {
        this(context, null);
    }

    public DragListView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragListView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.i("yuyidong", "tryCaptureView  child--->" + child + "   pointerId--->" + pointerId);
//            if (mDragCapture) {
//                return child == mDragView1;
//            }
            return true;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            invalidate();
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            Log.i("yuyidong", "onViewCaptured  capturedChild--->" + capturedChild + "   activePointerId--->" + activePointerId);
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            Log.i("yuyidong", "onViewReleased  releasedChild--->" + releasedChild + "   xvel--->" + xvel + "   yvel--->" + yvel);
            super.onViewReleased(releasedChild, xvel, yvel);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
//            if (mDragVertical) {
//                final int topBound = getPaddingTop();
//                final int bottomBound = getHeight() - mDragView1.getHeight();
//
//                final int newTop = Math.min(Math.max(top, topBound), bottomBound);
//
//                return newTop;
//            }
            return super.clampViewPositionVertical(child, top, dy);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            if (mDragHorizontal || mDragCapture || mDragEdge) {
//                final int leftBound = getPaddingLeft();
//                final int rightBound = getWidth() - mDragView1.getWidth();
//
//                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
//                return newLeft;
//            }
            return super.clampViewPositionHorizontal(child, left, dx);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsDrag) {
            mDragHelper.processTouchEvent(ev);
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

}
