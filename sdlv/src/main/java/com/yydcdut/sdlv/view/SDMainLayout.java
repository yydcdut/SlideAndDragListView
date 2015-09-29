package com.yydcdut.sdlv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.yydcdut.sdlv.utils.OnItemSlideListenerProxy;

/**
 * Created by yuyidong on 15/9/24.
 */
public class SDMainLayout extends FrameLayout {
    private static final int SCROLL_STATE_OPEN = 1;
    private static final int SCROLL_STATE_CLOSE = 0;
    private int mScrollState = SCROLL_STATE_CLOSE;
    /* 时间 */
    private static final int SCROLL_TIME = 500;//500ms
    private static final int SCROLL_QUICK_TIME = 200;//200ms
    /* 控件高度 */
    private int mHeight;
    /* 子控件中button的宽度 */
    private int mBGWidth;
    /* 子view */
    private SDBGLayout mSDBGLayout;
    private SDCustomLayout mSDCustomLayout;
    /* Scroller */
    private Scroller mScroller;
    /* 控件是否滑动 */
    private boolean mIsMoving = false;
    /* 坐标 */
    private float mXDown;
    private float mYDown;
    /* X方向滑动距离 */
    private float mXScrollDistance;
    /* 滑动的监听器 */
    private OnItemSlideListenerProxy mOnItemSlideListenerProxy;


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

    /**
     * 得到CustomView
     *
     * @return
     */
    public SDCustomLayout getSDCustomLayout() {
        return mSDCustomLayout;
    }

    /**
     * 得到背景View
     *
     * @return
     */
    public SDBGLayout getSDBGLayout() {
        return mSDBGLayout;
    }

    /**
     * 设置item的高度,button的宽度,button总宽度
     *
     * @param height
     * @param btnWidth
     */
    public void setLayoutHeight(int height, int btnWidth, int btnTotalWidth) {
        mHeight = height;
        mSDBGLayout.setBtnWidth(btnWidth);
        mBGWidth = btnTotalWidth;
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

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(false);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getX();
                mYDown = ev.getY();
                mXScrollDistance = mSDCustomLayout.getScrollX();
                mIsMoving = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (fingerNotMove(ev) && !mIsMoving) {//手指的范围在50以内
                    //执行ListView的手势操作
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (fingerLeftAndRightMove(ev) || mIsMoving) {//上下范围在50，主要检测左右滑动
                    //执行控件的手势操作
                    mIsMoving = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float moveDistance = ev.getX() - mXDown;//这个往右是正，往左是负
                    float distance = mXScrollDistance - moveDistance < 0 ? mXScrollDistance - moveDistance : 0;
                    mSDCustomLayout.scrollTo((int) distance, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //如果滑出的话，那么就滑到固定位置(只要滑出了 mBGWidth / 2 ，就算滑出去了)
                if (Math.abs(mSDCustomLayout.getScrollX()) > mBGWidth / 2) {
                    //滑出
                    int delta = mBGWidth - Math.abs(mSDCustomLayout.getScrollX());
                    if (Math.abs(mSDCustomLayout.getScrollX()) < mBGWidth) {
                        mScroller.startScroll(mSDCustomLayout.getScrollX(), 0, -delta, 0, SCROLL_QUICK_TIME);
                    } else {
                        mScroller.startScroll(mSDCustomLayout.getScrollX(), 0, -delta, 0, SCROLL_TIME);
                    }
                    if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_OPEN) {
                        mOnItemSlideListenerProxy.onSlideOpen(this);
                    }
                    mScrollState = SCROLL_STATE_OPEN;
                } else {
                    mScroller.startScroll(mSDCustomLayout.getScrollX(), 0, -mSDCustomLayout.getScrollX(), 0, SCROLL_TIME);
                    //滑回去,归位
                    if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_CLOSE) {
                        mOnItemSlideListenerProxy.onSlideClose(this);
                    }
                    mScrollState = SCROLL_STATE_CLOSE;
                }
                postInvalidate();
                mIsMoving = false;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 上下左右不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerNotMove(MotionEvent ev) {
        return (mXDown - ev.getX() < 25 && mXDown - ev.getX() > -25 &&
                mYDown - ev.getY() < 25 && mYDown - ev.getY() > -25);
    }

    /**
     * 左右得超出50，上下不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerLeftAndRightMove(MotionEvent ev) {
        return ((ev.getX() - mXDown > 25 || ev.getX() - mXDown < -25) &&
                ev.getY() - mYDown < 25 && ev.getY() - mYDown > -25);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mSDCustomLayout.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 归位
     */
    public void scrollBack() {
        mScroller.startScroll(mSDCustomLayout.getScrollX(), 0, -mSDCustomLayout.getScrollX(), 0, SCROLL_TIME);
        postInvalidate();
    }

    /**
     * 设置item滑动的监听器
     *
     * @param onItemSlideListenerProxy
     */
    public void setOnItemSlideListenerProxy(OnItemSlideListenerProxy onItemSlideListenerProxy) {
        mOnItemSlideListenerProxy = onItemSlideListenerProxy;
    }
}
