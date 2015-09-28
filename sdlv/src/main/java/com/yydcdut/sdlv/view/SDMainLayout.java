package com.yydcdut.sdlv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by yuyidong on 15/9/24.
 */
public class SDMainLayout extends FrameLayout {
    private int mHeight;

    private SDBGLayout mSDBGLayout;
    private SDCustomLayout mSDCustomLayout;

    private Scroller mScroller;

    public SDMainLayout(Context context) {
        this(context, null);
    }

    public SDMainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SDMainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mSDBGLayout = new SDBGLayout(context);
        addView(mSDBGLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mSDCustomLayout = new SDCustomLayout(context);
        addView(mSDCustomLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setLayoutHeight(int height, int btnWidth) {
        mHeight = height;
        mSDBGLayout.setBtnWidth(btnWidth);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeight > 0) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mHeight);
            for (int i = 0; i < getChildCount(); i++) {
                measureChild(getChildAt(i), widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public SDCustomLayout getSDCustomLayout() {
        return mSDCustomLayout;
    }

    public SDBGLayout getSDBGLayout() {
        return mSDBGLayout;
    }


    private float mXDown;
    private float mXScrollDistance;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getX();
                mXScrollDistance = mSDCustomLayout.getScrollX();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveDistance = ev.getX() - mXDown;//这个往右是正，往左是负
                float distance = mXScrollDistance - moveDistance < 0 ? mXScrollDistance - moveDistance : 0;
                mSDCustomLayout.scrollTo((int) distance, 0);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mScroller.startScroll(mSDCustomLayout.getScrollX(), 0, -mSDCustomLayout.getScrollX(), 0, 1000);
                postInvalidate();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mSDCustomLayout.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }
}
