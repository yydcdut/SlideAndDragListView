package com.yydcdut.sdlv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/9/30.
 */
class DragListView<T> extends ListView implements OnDragDropListener {
    private static final String DRAG_LOCAL_STATE = "local_state";
    private static final float DRAG_VIEW_ALPHA = 0.7f;
    private static final ClipData EMPTY_CLIP_DATA = ClipData.newPlainText("", "");
    /* 移动距离 */
    private final int DRAG_SCROLL_PX_UNIT = 25;
    /* Handler */
    private Handler mScrollHandler;
    /* Handler的延时 */
    private final long SCROLL_HANDLER_DELAY_MILLIS = 5;
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
    /* 数据 */
    protected List<T> mDataList;
    /* 监听器 */
    private SlideAndDragListView.OnDragListener mOnDragListener;
    /* drag的View */
    private ImageView mDragView;
    /* drag的父View */
    private View mDragViewParent;
    /* drag的View的cache bitmap */
    private Bitmap mDragViewBitmap;
    /* drag的View的left */
    private int mDragViewLeft;
    /* drag的View的top */
    private int mDragViewTop;
    /* left的偏移 */
    private int mTouchOffsetToChildLeft;
    /* top的偏移 */
    private int mTouchOffsetToChildTop;
    /* 监听器 */
    private List<OnDragDropListener> mOnDragDropListeners;
    /* 最小距离 */
    private float mTouchSlop;

    public DragListView(Context context) {
        this(context, null);
    }

    public DragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        mOnDragDropListeners = new ArrayList<>();
        addOnDragDropListener(this);
    }

    private final Runnable mDragScroller = new Runnable() {
        @Override
        public void run() {
            if (mLastDragY <= mTopScrollBound) {
                smoothScrollBy(-DRAG_SCROLL_PX_UNIT, (int) SCROLL_HANDLER_DELAY_MILLIS);
            } else if (mLastDragY >= mBottomScrollBound) {
                smoothScrollBy(DRAG_SCROLL_PX_UNIT, (int) SCROLL_HANDLER_DELAY_MILLIS);
            }
            mScrollHandler.postDelayed(this, SCROLL_HANDLER_DELAY_MILLIS);
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchDownForDragStartY = (int) ev.getY();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        final int action = event.getAction();
        final int eX = (int) event.getX();
        final int eY = (int) event.getY();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (!DRAG_LOCAL_STATE.equals(event.getLocalState())) {
                    return false;
                }
                if (!handleDragStarted(eX, eY)) {
                    return false;
                }
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                mLastDragY = eY;
                handleDragMoving(this, eX, eY);
                if (!mIsDragScrollerRunning && (Math.abs(mLastDragY - mTouchDownForDragStartY) >= 4 * mTouchSlop)) {
                    mIsDragScrollerRunning = true;
                    ensureScrollHandler();
                    mScrollHandler.postDelayed(mDragScroller, SCROLL_HANDLER_DELAY_MILLIS);
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                final int boundGap = (int) (getHeight() * BOUND_GAP_RATIO);
                mTopScrollBound = (getTop() + boundGap);
                mBottomScrollBound = (getBottom() - boundGap);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
            case DragEvent.ACTION_DROP:
                ensureScrollHandler();
                mScrollHandler.removeCallbacks(mDragScroller);
                mIsDragScrollerRunning = false;
                if (action == DragEvent.ACTION_DROP || action == DragEvent.ACTION_DRAG_ENDED) {
                    handleDragFinished(eX, eY);
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 确保Handler
     */
    private void ensureScrollHandler() {
        if (mScrollHandler == null) {
            mScrollHandler = getHandler();
        }
        if (mScrollHandler == null) {
            mScrollHandler = new Handler();
        }
    }

    protected void setDragView(ImageView overlay) {
        mDragView = overlay;
        mDragViewParent = (View) mDragView.getParent();
    }

    protected void setDragPosition(int position, boolean isWannaTransparentWhileDragging) {
        View view = getChildAt(position - getFirstVisiblePosition());
        if (mOnDragListener != null && view instanceof ItemMainLayout) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) getChildAt(position - getFirstVisiblePosition());
            Drawable backgroundDrawable = itemMainLayout.getItemCustomView().getBackground();
            if (isWannaTransparentWhileDragging) {
                Compat.setBackgroundDrawable(itemMainLayout.getItemCustomView(), new ColorDrawable(Color.TRANSPARENT));
            }
            itemMainLayout.getItemLeftBackGroundLayout().setVisibility(GONE);
            itemMainLayout.getItemRightBackGroundLayout().setVisibility(GONE);
            itemMainLayout.startDrag(EMPTY_CLIP_DATA, new View.DragShadowBuilder(), DRAG_LOCAL_STATE, 0);
//            mOnDragListener.onDragViewStart(position);
            if (isWannaTransparentWhileDragging) {
                Compat.setBackgroundDrawable(itemMainLayout.getItemCustomView(), backgroundDrawable);
            }
        }
    }

    @Deprecated
    @Override
    public void setOnDragListener(View.OnDragListener l) {
    }

    /**
     * 设置drag的监听器，加入数据
     *
     * @param onDragListener
     * @param dataList
     */
    public void setOnDragListener(SlideAndDragListView.OnDragListener onDragListener, List<T> dataList) {
        mOnDragListener = onDragListener;
        mDataList = dataList;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    private View getViewForLocation(int x, int y) {
        int[] locationOnScreen = new int[2];
        getLocationOnScreen(locationOnScreen);
        int viewX = x - locationOnScreen[0];
        int viewY = y - locationOnScreen[1];
        View child = getViewAtPosition(viewX, viewY);
        return child;
    }

    private View getViewAtPosition(int x, int y) {
        int count = getChildCount();
        View child;
        for (int childIdx = 0; childIdx < count; childIdx++) {
            child = getChildAt(childIdx);
            if (y >= child.getTop() && y <= child.getBottom() && x >= child.getLeft() && x <= child.getRight()) {
                return child;
            }
        }
        return null;
    }

    @Override
    public void onDragStarted(int x, int y, View view) {
        if (mDragView == null) {
            return;
        }
        mDragViewBitmap = createDraggedChildBitmap(view);
        if (mDragViewBitmap == null) {
            return;
        }

        int[] locationOnScreen = new int[2];
        view.getLocationOnScreen(locationOnScreen);
        mDragViewLeft = locationOnScreen[0];
        mDragViewTop = locationOnScreen[1];

        mTouchOffsetToChildLeft = x - mDragViewLeft;
        mTouchOffsetToChildTop = y - mDragViewTop;

        mDragViewParent.getLocationOnScreen(locationOnScreen);
        mDragViewLeft -= locationOnScreen[0];
        mDragViewTop -= locationOnScreen[1];

        mDragView.setImageBitmap(mDragViewBitmap);
        mDragView.setVisibility(VISIBLE);
        mDragView.setAlpha(DRAG_VIEW_ALPHA);

//        mDragView.setX(mDragViewLeft);
        mDragView.setX(getPaddingLeft() + mDragViewParent.getPaddingLeft());
        mDragView.setY(mDragViewTop);
    }

    private Bitmap createDraggedChildBitmap(View view) {
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
        return bitmap;
    }

    @Override
    public void onDragMoving(int x, int y, View view) {
        int[] locationOnScreen = new int[2];
        mDragViewParent.getLocationOnScreen(locationOnScreen);
        mDragViewLeft = x - mTouchOffsetToChildLeft - locationOnScreen[0];
        mDragViewTop = y - mTouchOffsetToChildTop - locationOnScreen[1];
        // Draw the drag shadow at its last known location if the drag shadow exists.
        if (mDragView != null) {
//            mDragView.setX(mDragViewLeft);
            mDragView.setX(getPaddingLeft() + mDragViewParent.getPaddingLeft());
            mDragView.setY(mDragViewTop);
        }
    }

    @Override
    public void onDragFinished(int x, int y) {
        if (mDragView != null) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mDragView, "alpha", DRAG_VIEW_ALPHA, 0.0f);
            objectAnimator.setDuration(100);
            objectAnimator.addListener(new DragFinishAnimation());
            objectAnimator.start();
        }
    }

    private class DragFinishAnimation extends AnimatorListenerAdapter {
        @Override
        public void onAnimationEnd(Animator animation) {
            if (mDragViewBitmap != null) {
                mDragViewBitmap.recycle();
                mDragViewBitmap = null;
            }
            mDragView.setVisibility(GONE);
            mDragView.setImageBitmap(null);
        }
    }

    boolean handleDragStarted(int x, int y) {
        View view = getViewForLocation(x, y);
        if (view == null) {
            return false;
        }
        for (int i = 0; i < mOnDragDropListeners.size(); i++) {
            mOnDragDropListeners.get(i).onDragStarted(x, y, view);
        }

        return true;
    }

    public void handleDragMoving(View v, int x, int y) {
        int[] locationOnScreen = new int[2];
        v.getLocationOnScreen(locationOnScreen);
        int screenX = x + locationOnScreen[0];
        int screenY = y + locationOnScreen[1];
        View view = getViewForLocation(screenX, screenY);
        if (view == null) {
            return;
        }
        for (int i = 0; i < mOnDragDropListeners.size(); i++) {
            mOnDragDropListeners.get(i).onDragMoving(screenX, screenY, view);
        }
    }

    public void handleDragFinished(int x, int y) {
        for (int i = 0; i < mOnDragDropListeners.size(); i++) {
            mOnDragDropListeners.get(i).onDragFinished(x, y);
        }
    }

    public void addOnDragDropListener(OnDragDropListener listener) {
        if (!mOnDragDropListeners.contains(listener)) {
            mOnDragDropListeners.add(listener);
        }
    }
}
