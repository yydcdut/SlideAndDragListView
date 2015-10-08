package com.yydcdut.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by yuyidong on 15/9/24.
 */
class ItemMainLayout extends FrameLayout {
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
    private ItemBackGroundLayout mItemBackGroundLayout;
    private ItemCustomLayout mItemCustomLayout;
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

    public ItemMainLayout(Context context) {
        this(context, null);
    }

    public ItemMainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemMainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mItemBackGroundLayout = new ItemBackGroundLayout(context);
        addView(mItemBackGroundLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mItemCustomLayout = new ItemCustomLayout(context);
        addView(mItemCustomLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /**
     * 得到CustomView
     *
     * @return
     */
    public ItemCustomLayout getItemCustomLayout() {
        return mItemCustomLayout;
    }

    /**
     * 得到背景View
     *
     * @return
     */
    public ItemBackGroundLayout getItemBackGroundLayout() {
        return mItemBackGroundLayout;
    }

    /**
     * 设置item的高度,button总宽度
     *
     * @param height
     * @param btnTotalWidth
     */
    public void setLayoutHeight(int height, int btnTotalWidth) {
        mHeight = height;
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
                //控件初始距离
                mXScrollDistance = mItemCustomLayout.getScrollX();
                //是否有要scroll的动向，目前没有
                mIsMoving = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (fingerNotMove(ev) && !mIsMoving) {//手指的范围在50以内
                    //执行ListView的手势操作
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (fingerLeftAndRightMove(ev) || mIsMoving) {//上下范围在50，主要检测左右滑动
                    //是否有要scroll的动向，是
                    mIsMoving = true;
                    //执行控件的手势操作
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float moveDistance = ev.getX() - mXDown;//这个往右是正，往左是负
                    //计算出距离
                    float distance = mXScrollDistance - moveDistance < 0 ? mXScrollDistance - moveDistance : 0;
                    //滑动
                    mItemCustomLayout.scrollTo((int) distance, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //如果滑出的话，那么就滑到固定位置(只要滑出了 mBGWidth / 2 ，就算滑出去了)
                if (Math.abs(mItemCustomLayout.getScrollX()) > mBGWidth / 2) {
                    //滑出
                    int delta = mBGWidth - Math.abs(mItemCustomLayout.getScrollX());
                    if (Math.abs(mItemCustomLayout.getScrollX()) < mBGWidth) {
                        mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, -delta, 0, SCROLL_QUICK_TIME);
                    } else {
                        mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, -delta, 0, SCROLL_TIME);
                    }
                    if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_OPEN) {
                        mOnItemSlideListenerProxy.onSlideOpen(this);
                    }
                    mScrollState = SCROLL_STATE_OPEN;
                } else {
                    mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, -mItemCustomLayout.getScrollX(), 0, SCROLL_TIME);
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
            mItemCustomLayout.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 归位
     */
    public void scrollBack() {
        mScroller.startScroll(mItemCustomLayout.getScrollX(), 0, -mItemCustomLayout.getScrollX(), 0, SCROLL_TIME);
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

    public interface OnItemSlideListenerProxy {
        void onSlideOpen(View view);

        void onSlideClose(View view);
    }
}
