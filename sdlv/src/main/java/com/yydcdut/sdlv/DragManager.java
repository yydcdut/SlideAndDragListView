package com.yydcdut.sdlv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by yuyidong on 2018/4/21.
 */
public class DragManager implements Callback.OnDragDropListener {
    /* drag的时候透明度 */
    private static final float DRAG_VIEW_ALPHA = 0.7f;

    /* Inner View */
    private SlideListView mSlideListView;
    /* current View */
    private View mCurrentView;
    /* Handler的延时 */
    private final long SCROLL_HANDLER_DELAY_MILLIS = 5;
    /* 移动距离 */
    private final int DRAG_SCROLL_PX_UNIT = 25;
    /* Handler */
    private Handler mScrollHandler;
    /* 边界比例，到这个比例的位置就开始移动 */
    private final float BOUND_GAP_RATIO = 0.2f;
    /* 边界 */
    private int mTopScrollBound;
    private int mBottomScrollBound;
    /* 按下的时候的Y轴坐标 */
    private int mTouchDownForDragStartY;
    /* Move的时候的Y轴坐标 */
    private int mLastDragY;
    /* 是否进入了scroll的handler里面了 */
    private boolean mIsDragScrollerRunning = false;
    /* 最小距离 */
    private float mTouchSlop;
    /* drag的View的Bitmap */
    private Bitmap mDragViewBitmap;
    /* 拦截参数 */
    private boolean interceptTouchEvent = false;
    /* drag的误差 */
    private int mDragDelta;

    private int mX, mY;
    private boolean isVisible;

    public DragManager(Context context, SlideListView slideListView, View currentView, ViewGroup decorView) {
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        mCurrentView = currentView;
        mSlideListView = slideListView;
        mSlideListView.setListDragDropListener(this);
    }

    protected void setInterceptTouchEvent(boolean interceptTouchEvent) {
        this.interceptTouchEvent = interceptTouchEvent;
    }

    protected boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchDownForDragStartY = (int) ev.getY();
        }
        if (interceptTouchEvent) {
            final int boundGap = (int) (mCurrentView.getHeight() * BOUND_GAP_RATIO);
            mTopScrollBound = (mCurrentView.getTop() + boundGap);
            mBottomScrollBound = (mCurrentView.getBottom() - boundGap);
            mSlideListView.handleDragStarted((int) ev.getX(), (int) ev.getY());
        }
        return interceptTouchEvent;
    }

    protected boolean onTouchEvent(MotionEvent event) {
        int eX = (int) event.getX();
        int eY = (int) event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                mLastDragY = eY;
                mSlideListView.handleDragMoving(eX, eY);
                if (!mIsDragScrollerRunning && (Math.abs(mLastDragY - mTouchDownForDragStartY) >= 4 * mTouchSlop)) {
                    mIsDragScrollerRunning = true;
                    ensureScrollHandler();
                    mScrollHandler.postDelayed(mDragScroller, SCROLL_HANDLER_DELAY_MILLIS);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ensureScrollHandler();
                mScrollHandler.removeCallbacks(mDragScroller);
                mIsDragScrollerRunning = false;
                mSlideListView.handleDragFinished(eX, eY);
                interceptTouchEvent = false;
                break;
        }
        return interceptTouchEvent;
    }

    private void ensureScrollHandler() {
        if (mScrollHandler == null) {
            mScrollHandler = new Handler();
        }
    }

    private final Runnable mDragScroller = new Runnable() {
        @Override
        public void run() {
            if (mLastDragY <= mTopScrollBound) {
                mSlideListView.smoothScrollBy(-DRAG_SCROLL_PX_UNIT, (int) SCROLL_HANDLER_DELAY_MILLIS);
            } else if (mLastDragY >= mBottomScrollBound) {
                mSlideListView.smoothScrollBy(DRAG_SCROLL_PX_UNIT, (int) SCROLL_HANDLER_DELAY_MILLIS);
            }
            mScrollHandler.postDelayed(this, SCROLL_HANDLER_DELAY_MILLIS);
        }
    };

    @Override
    public boolean onDragStarted(int x, int y, View view) {
        mDragViewBitmap = createDraggedChildBitmap(view);
        if (mDragViewBitmap == null) {
            return false;
        }
//        mDragView.setImageBitmap(mDragViewBitmap);
//        mDragView.setVisibility(View.VISIBLE);
//        mDragView.setAlpha(DRAG_VIEW_ALPHA);
//        mDragView.setX(mSlideListView.getPaddingLeft() + mCurrentView.getPaddingLeft());
//        mDragDelta = y - view.getTop();
//        mDragView.setY(y - mDragDelta);
        mX = mSlideListView.getPaddingLeft() + mCurrentView.getPaddingLeft();
        mDragDelta = y - view.getTop();
        mY = y - mDragDelta;
        isVisible = true;
        mCurrentView.invalidate();
        return true;
    }

    private Bitmap createDraggedChildBitmap(View view) {
        if (view instanceof ItemMainLayout) {
            ((ItemMainLayout) view).disableBackgroundDrawable();
        }
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        Bitmap bitmap = null;
        if (cache != null) {
            try {
                bitmap = cache.copy(Bitmap.Config.ARGB_8888, false);
            } catch (OutOfMemoryError e) {
                bitmap = null;
            }
        }
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        if (view instanceof ItemMainLayout) {
            ((ItemMainLayout) view).enableBackgroundDrawable();
        }
        return bitmap;
    }

    @Override
    public void onDragMoving(int x, int y, View view, SlideAndDragListView.OnDragDropListener listener) {
        mX = mSlideListView.getPaddingLeft() + mCurrentView.getPaddingLeft();
        mY = y - mDragDelta;
        mCurrentView.invalidate();
    }

    @Override
    public void onDragFinished(int x, int y, SlideAndDragListView.OnDragDropListener listener) {
        mDragDelta = 0;
        if (isVisible) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(DRAG_VIEW_ALPHA, 0.0f);
            valueAnimator.setDuration(100);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentView.invalidate();
                }
            });
            valueAnimator.addListener(new DragFinishAnimation());
            valueAnimator.start();
//            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mDragView, "alpha", DRAG_VIEW_ALPHA, 0.0f);
//            objectAnimator.setDuration(100);
//            objectAnimator.addListener(new DragFinishAnimation());
//            objectAnimator.start();
        }
    }

    private class DragFinishAnimation extends AnimatorListenerAdapter {
        @Override
        public void onAnimationEnd(Animator animation) {
            if (mDragViewBitmap != null) {
                mDragViewBitmap.recycle();
                mDragViewBitmap = null;
            }
            isVisible = false;
            mCurrentView.invalidate();
//            mDragView.setVisibility(View.GONE);
//            mDragView.setImageBitmap(null);
        }
    }

    public void draw(Canvas canvas) {
        Log.i("yuyidong", "00000");
        if (mDragViewBitmap == null || !isVisible) {
            return;
        }
        Log.i("yuyidong", "1111");
        canvas.drawBitmap(mDragViewBitmap, new Rect(0, 0, mDragViewBitmap.getWidth(), mDragViewBitmap.getHeight())
                , new Rect(mX, mY, mX + mDragViewBitmap.getWidth(), mY + mDragViewBitmap.getHeight()), null);
    }
}
