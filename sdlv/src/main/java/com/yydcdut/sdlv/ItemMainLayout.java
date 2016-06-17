package com.yydcdut.sdlv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by yuyidong on 15/9/24.
 */
class ItemMainLayout extends FrameLayout {
    private static final int INTENTION_LEFT_OPEN = 1;
    private static final int INTENTION_LEFT_CLOSE = 2;
    private static final int INTENTION_LEFT_ALREADY_OPEN = 3;
    private static final int INTENTION_RIGHT_OPEN = -1;
    private static final int INTENTION_RIGHT_CLOSE = -2;
    private static final int INTENTION_RIGHT_ALREADY_OPEN = -3;
    private static final int INTENTION_SCROLL_BACK = -4;
    private static final int INTENTION_ZERO = 0;
    private int mIntention = INTENTION_ZERO;

    /* 判断当前是否滑出，若为滑出，则是SCROLL_STATE_OPEN，则过度的滑动都不会去触发slideOpen接口，同理SCROLL_STATE_CLOSE */
    protected static final int SCROLL_STATE_OPEN = 1;
    protected static final int SCROLL_STATE_CLOSE = 0;
    private int mScrollState = SCROLL_STATE_CLOSE;
    /* 需要scroll back的时候返回的状态 */
    protected static final int SCROLL_BACK_CLICK_NOTHING = 0;
    protected static final int SCROLL_BACK_CLICK_OWN = 1;//点击到item了
    protected static final int SCROLL_BACK_ALREADY_CLOSED = 2;
    protected static final int SCROLL_BACK_CLICK_MENU_BUTTON = 3;//点击到menu的button了

    /* 时间 */
    private static final int SCROLL_TIME = 500;//500ms
    private static final int SCROLL_BACK_TIME = 250;//250ms
    private static final int SCROLL_DELETE_TIME = 300;//300ms
    /* 子控件中button的总宽度 */
    private int mBtnLeftTotalWidth;
    private int mBtnRightTotalWidth;
    /* 子view */
    private ItemBackGroundLayout mItemLeftBackGroundLayout;
    private ItemBackGroundLayout mItemRightBackGroundLayout;
    private View mItemCustomView;
    /* Scroller */
    private Scroller mScroller;
    /* 控件是否滑动 */
    private boolean mIsMoving = false;
    /* 是不是要滑过(over) */
    private boolean mWannaOver = true;
    /* 最小滑动距离，超过了，才认为开始滑动 */
    private int mTouchSlop = 0;
    /* 滑动的监听器 */
    private OnItemSlideListenerProxy mOnItemSlideListenerProxy;
    /* Drawable */
    private Drawable mNormalCustomBackgroundDrawable;
    private Drawable mTotalCustomBackgroundDrawable;
    private Drawable mNormalListSelectorDrawable;
    private Drawable mTotalListSelectorDrawable;

    protected ItemMainLayout(Context context, View customView) {
        super(context);
        mScroller = new Scroller(context);
        mItemRightBackGroundLayout = new ItemBackGroundLayout(context);
        addView(mItemRightBackGroundLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mItemLeftBackGroundLayout = new ItemBackGroundLayout(context);
        addView(mItemLeftBackGroundLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mItemCustomView = customView;
        ViewGroup.LayoutParams layoutParams = customView.getLayoutParams();
        if (layoutParams == null) {
            addView(mItemCustomView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else {
            addView(mItemCustomView, layoutParams);
        }
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        initBackgroundDrawable();
    }


    /**
     * 得到CustomView
     *
     * @return
     */
    protected View getItemCustomView() {
        return mItemCustomView;
    }

    /**
     * 得到左边的背景View
     *
     * @return
     */
    protected ItemBackGroundLayout getItemLeftBackGroundLayout() {
        return mItemLeftBackGroundLayout;
    }

    /**
     * 得到右边的背景View
     *
     * @return
     */
    protected ItemBackGroundLayout getItemRightBackGroundLayout() {
        return mItemRightBackGroundLayout;
    }

    /**
     * @param btnLeftTotalWidth
     * @param btnRightTotalWidth
     * @param wannaOver
     */
    protected void setParams(int btnLeftTotalWidth, int btnRightTotalWidth, boolean wannaOver) {
        requestLayout();
        mBtnLeftTotalWidth = btnLeftTotalWidth;
        mBtnRightTotalWidth = btnRightTotalWidth;
        mWannaOver = wannaOver;
    }

    protected void setSelector(Drawable drawable) {
        Compat.setBackgroundDrawable(mItemLeftBackGroundLayout, drawable);
        Compat.setBackgroundDrawable(mItemRightBackGroundLayout, drawable);
    }

    protected void handleMotionEvent(MotionEvent ev, final float xDown, final float yDown, final int leftDistance) {
        getParent().requestDisallowInterceptTouchEvent(false);
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                if (fingerNotMove(ev, xDown, yDown) && !mIsMoving) {//手指的范围在50以内
                    //执行ListView的手势操作
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (fingerLeftAndRightMove(ev, xDown, yDown) || mIsMoving) {//上下范围在50，主要检测左右滑动
                    //禁止StateListDrawable
                    disableBackgroundDrawable();
                    //是否有要scroll的动向
                    mIsMoving = true;
                    //执行控件的手势操作
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float moveDistance = ev.getX() - xDown;//这个往右是正，往左是负
                    //判断意图
                    if (moveDistance > 0) {//往右
                        if (leftDistance == 0) {//关闭状态
                            mIntention = INTENTION_LEFT_OPEN;
                            setBackGroundVisible(true, false);
                        } else if (leftDistance < 0) {//右边的btn显示出来的
                            mIntention = INTENTION_RIGHT_CLOSE;
                            setBackGroundVisible(false, true);
                        } else if (leftDistance > 0) {//左边的btn显示出来的
                            mIntention = INTENTION_LEFT_ALREADY_OPEN;
                            setBackGroundVisible(true, false);
                        }
                    } else if (moveDistance < 0) {//往左
                        if (leftDistance == 0) {//关闭状态
                            mIntention = INTENTION_RIGHT_OPEN;
                            setBackGroundVisible(false, true);
                        } else if (leftDistance < 0) {//右边的btn显示出来的
                            mIntention = INTENTION_RIGHT_ALREADY_OPEN;
                            setBackGroundVisible(false, true);
                        } else if (leftDistance > 0) {//左边的btn显示出来的
                            mIntention = INTENTION_LEFT_CLOSE;
                            setBackGroundVisible(true, false);
                        }
                    }
                    //计算出距离
                    switch (mIntention) {
                        case INTENTION_LEFT_OPEN:
                        case INTENTION_LEFT_ALREADY_OPEN:
                            if (mItemLeftBackGroundLayout.getBtnViews().size() == 0) {//如果左边没有menu，就不能往左滑动
                                break;
                            }
                            //此时moveDistance为正数，mLeftDistance为0
                            float distanceLeftOpen = leftDistance + moveDistance;
                            if (!mWannaOver) {
                                distanceLeftOpen = distanceLeftOpen > mBtnLeftTotalWidth ? mBtnLeftTotalWidth : distanceLeftOpen;
                            }
                            //滑动
                            mItemCustomView.layout((int) distanceLeftOpen, mItemCustomView.getTop(),
                                    mItemCustomView.getWidth() + (int) distanceLeftOpen, mItemCustomView.getBottom());
                            break;
                        case INTENTION_LEFT_CLOSE:
                            //此时moveDistance为负数，mLeftDistance为正数
                            float distanceLeftClose = leftDistance + moveDistance < 0 ? 0 : leftDistance + moveDistance;
                            //滑动
                            mItemCustomView.layout((int) distanceLeftClose, mItemCustomView.getTop(),
                                    mItemCustomView.getWidth() + (int) distanceLeftClose, mItemCustomView.getBottom());
                            break;
                        case INTENTION_RIGHT_OPEN:
                        case INTENTION_RIGHT_ALREADY_OPEN:
                            if (mItemRightBackGroundLayout.getBtnViews().size() == 0) {//如果右边没有menu，就不能往左滑动
                                break;
                            }
                            //此时moveDistance为负数，mLeftDistance为0
                            float distanceRightOpen = leftDistance + moveDistance;
                            //distanceRightOpen为正数
                            if (!mWannaOver) {
                                distanceRightOpen = -distanceRightOpen > mBtnRightTotalWidth ? -mBtnRightTotalWidth : distanceRightOpen;
                            }
                            //滑动
                            mItemCustomView.layout((int) distanceRightOpen, mItemCustomView.getTop(),
                                    mItemCustomView.getWidth() + (int) distanceRightOpen, mItemCustomView.getBottom());
                            break;
                        case INTENTION_RIGHT_CLOSE:
                            //此时moveDistance为正数，mLeftDistance为负数
                            float distanceRightClose = leftDistance + moveDistance > 0 ? 0 : leftDistance + moveDistance;
                            //滑动
                            mItemCustomView.layout((int) distanceRightClose, mItemCustomView.getTop(),
                                    mItemCustomView.getWidth() + (int) distanceRightClose, mItemCustomView.getBottom());
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                switch (mIntention) {
                    case INTENTION_LEFT_CLOSE:
                    case INTENTION_LEFT_OPEN:
                    case INTENTION_LEFT_ALREADY_OPEN:
                        //如果滑出的话，那么就滑到固定位置(只要滑出了 mBtnLeftTotalWidth / 2 ，就算滑出去了)
                        if (Math.abs(mItemCustomView.getLeft()) > mBtnLeftTotalWidth / 2) {
                            //滑出
                            mIntention = INTENTION_LEFT_OPEN;
                            int delta = mBtnLeftTotalWidth - Math.abs(mItemCustomView.getLeft());
                            mScroller.startScroll(mItemCustomView.getLeft(), 0, delta, 0, SCROLL_TIME);
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_OPEN) {
                                mOnItemSlideListenerProxy.onSlideOpen(this, MenuItem.DIRECTION_LEFT);
                            }
                            mScrollState = SCROLL_STATE_OPEN;
                        } else {
                            mIntention = INTENTION_LEFT_CLOSE;
                            //滑回去,归位
                            mScroller.startScroll(mItemCustomView.getLeft(), 0, -mItemCustomView.getLeft(), 0, SCROLL_TIME);
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_CLOSE) {
                                mOnItemSlideListenerProxy.onSlideClose(this, MenuItem.DIRECTION_LEFT);
                            }
                            mScrollState = SCROLL_STATE_CLOSE;
                        }
                        break;
                    case INTENTION_RIGHT_CLOSE:
                    case INTENTION_RIGHT_OPEN:
                    case INTENTION_RIGHT_ALREADY_OPEN:
                        if (Math.abs(mItemCustomView.getLeft()) > mBtnRightTotalWidth / 2) {
                            //滑出
                            mIntention = INTENTION_RIGHT_OPEN;
                            int delta = mBtnRightTotalWidth - Math.abs(mItemCustomView.getLeft());
                            mScroller.startScroll(mItemCustomView.getLeft(), 0, -delta, 0, SCROLL_TIME);
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_OPEN) {
                                mOnItemSlideListenerProxy.onSlideOpen(this, MenuItem.DIRECTION_RIGHT);
                            }
                            mScrollState = SCROLL_STATE_OPEN;
                        } else {
                            mIntention = INTENTION_RIGHT_CLOSE;
                            mScroller.startScroll(mItemCustomView.getLeft(), 0, -mItemCustomView.getLeft(), 0, SCROLL_TIME);
                            //滑回去,归位
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_CLOSE) {
                                mOnItemSlideListenerProxy.onSlideClose(this, MenuItem.DIRECTION_RIGHT);
                            }
                            mScrollState = SCROLL_STATE_CLOSE;
                        }
                        break;
                }
                mIntention = INTENTION_ZERO;
                postInvalidate();
                mIsMoving = false;
                break;
            default:
                break;
        }
    }


    /**
     * 设置哪边显示哪边不显示
     *
     * @param leftVisible
     * @param rightVisible
     */
    private void setBackGroundVisible(boolean leftVisible, boolean rightVisible) {
        if (leftVisible) {
            if (mItemLeftBackGroundLayout.getVisibility() != VISIBLE) {
                mItemLeftBackGroundLayout.setVisibility(VISIBLE);
            }
        } else {
            if (mItemLeftBackGroundLayout.getVisibility() == VISIBLE) {
                mItemLeftBackGroundLayout.setVisibility(GONE);
            }
        }
        if (rightVisible) {
            if (mItemRightBackGroundLayout.getVisibility() != VISIBLE) {
                mItemRightBackGroundLayout.setVisibility(VISIBLE);
            }
        } else {
            if (mItemRightBackGroundLayout.getVisibility() == VISIBLE) {
                mItemRightBackGroundLayout.setVisibility(GONE);
            }
        }
    }

    /**
     * 上下左右不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerNotMove(MotionEvent ev, final float xDown, final float yDown) {
        return (xDown - ev.getX() < mTouchSlop && xDown - ev.getX() > -mTouchSlop &&
                yDown - ev.getY() < mTouchSlop && yDown - ev.getY() > -mTouchSlop);
    }

    /**
     * 左右得超出50，上下不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerLeftAndRightMove(MotionEvent ev, final float xDown, final float yDown) {
        return ((ev.getX() - xDown > mTouchSlop || ev.getX() - xDown < -mTouchSlop) &&
                ev.getY() - yDown < mTouchSlop && ev.getY() - yDown > -mTouchSlop);
    }

    /**
     * 删除Item
     */
    protected void deleteItem(final OnItemDeleteListenerProxy onItemDeleteListenerProxy) {
        scrollBack();
        final int height = getMeasuredHeight();
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ViewGroup.LayoutParams layoutParams = ItemMainLayout.this.getLayoutParams();
                layoutParams.height = height;
                ItemMainLayout.this.setLayoutParams(layoutParams);
                if (onItemDeleteListenerProxy != null) {
                    onItemDeleteListenerProxy.onDelete(ItemMainLayout.this);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ViewGroup.LayoutParams layoutParams = ItemMainLayout.this.getLayoutParams();
                layoutParams.height = height - (int) (height * interpolatedTime);
                ItemMainLayout.this.setLayoutParams(layoutParams);
            }
        };
        animation.setAnimationListener(animationListener);
        animation.setDuration(SCROLL_DELETE_TIME);
        startAnimation(animation);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int left = mScroller.getCurrX();
            mItemCustomView.layout(left, mItemCustomView.getTop(),
                    mScroller.getCurrX() + mItemCustomView.getWidth(), mItemCustomView.getBottom());
            postInvalidate();
            if (left == 0) {
                setBackGroundVisible(false, false);
                //当item归位的时候才将drawable设置回去
                enableBackgroundDrawable();
            }
        }
        super.computeScroll();
    }

    /**
     * 归位
     */
    protected void scrollBack() {
        mIntention = INTENTION_SCROLL_BACK;
        mScroller.startScroll(mItemCustomView.getLeft(), 0, -mItemCustomView.getLeft(), 0, SCROLL_BACK_TIME);
        if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_CLOSE) {
            mOnItemSlideListenerProxy.onSlideClose(this, getItemCustomView().getLeft() < 0 ? MenuItem.DIRECTION_LEFT : MenuItem.DIRECTION_RIGHT);
        }
        postInvalidate();
        mScrollState = SCROLL_STATE_CLOSE;
        enableBackgroundDrawable();
    }

    /**
     * @param x 手指点下的位置
     * @return
     */
    protected int scrollBack(float x) {
        if (mScrollState == SCROLL_STATE_CLOSE) {//没有滑开，其实是滑了但是又滑归位了
            return SCROLL_BACK_ALREADY_CLOSED;
        }
        if (mItemCustomView.getLeft() > 0) { //已经向右滑动了，而且滑开了
            if (x > mItemCustomView.getLeft()) {
                //没有点击到menu的button
                scrollBack();
                mScrollState = SCROLL_STATE_CLOSE;
                return SCROLL_BACK_CLICK_OWN;
            }

        } else if (mItemCustomView.getLeft() < 0) {//已经向左滑动了，而且滑开了
            if (x < mItemCustomView.getRight()) {
                //没有点击到menu的button
                scrollBack();
                mScrollState = SCROLL_STATE_CLOSE;
                return SCROLL_BACK_CLICK_OWN;
            }
        }
        return SCROLL_BACK_CLICK_MENU_BUTTON;
    }

    /**
     * 初始化Drawable
     */
    private void initBackgroundDrawable() {
        Drawable drawable = getItemCustomView().getBackground();
        if (drawable != null) {
            if (drawable instanceof StateListDrawable) {
                StateListDrawable stateListDrawable = (StateListDrawable) drawable;
                mNormalCustomBackgroundDrawable = stateListDrawable.getCurrent();
            } else {
                mNormalCustomBackgroundDrawable = drawable;
            }
            mTotalCustomBackgroundDrawable = drawable;
        }

        Drawable listDrawable = getItemLeftBackGroundLayout().getBackground();
        if (listDrawable != null) {
            if (listDrawable instanceof StateListDrawable) {
                StateListDrawable stateListDrawable = (StateListDrawable) listDrawable;
                mNormalListSelectorDrawable = stateListDrawable.getCurrent();
            } else {
                mNormalListSelectorDrawable = listDrawable;
            }
            mTotalListSelectorDrawable = listDrawable;
        }
    }

    /**
     * 在滑动的时候禁止掉StateListDrawable
     */
    private void disableBackgroundDrawable() {
        if (mNormalCustomBackgroundDrawable != null) {
            Compat.setBackgroundDrawable(getItemCustomView(), mNormalCustomBackgroundDrawable);
        }
        if (mNormalListSelectorDrawable != null) {
            Compat.setBackgroundDrawable(getItemLeftBackGroundLayout(), mNormalListSelectorDrawable);
            Compat.setBackgroundDrawable(getItemRightBackGroundLayout(), mNormalListSelectorDrawable);
        }
    }

    /**
     * 在没有滑动的时候恢复StateListDrawable
     */
    private void enableBackgroundDrawable() {
        if (mTotalCustomBackgroundDrawable != null) {
            Compat.setBackgroundDrawable(getItemCustomView(), mTotalCustomBackgroundDrawable);
        }

        if (mTotalListSelectorDrawable != null) {
            Compat.setBackgroundDrawable(getItemLeftBackGroundLayout(), mTotalListSelectorDrawable);
            Compat.setBackgroundDrawable(getItemRightBackGroundLayout(), mTotalListSelectorDrawable);
        }
    }


    /**
     * 设置item滑动的监听器
     *
     * @param onItemSlideListenerProxy
     */
    protected void setOnItemSlideListenerProxy(OnItemSlideListenerProxy onItemSlideListenerProxy) {
        mOnItemSlideListenerProxy = onItemSlideListenerProxy;
    }

    protected interface OnItemSlideListenerProxy {
        void onSlideOpen(View view, int direction);

        void onSlideClose(View view, int direction);
    }

    protected interface OnItemDeleteListenerProxy {
        void onDelete(View view);
    }

    /**
     * 得到当前itemMainLayout是否是滑开的状态
     *
     * @return
     */
    protected int getScrollState() {
        return mScrollState;
    }
}
