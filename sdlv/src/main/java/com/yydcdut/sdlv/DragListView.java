package com.yydcdut.sdlv;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import java.util.List;

/**
 * Created by yuyidong on 15/9/30.
 */
class DragListView<T> extends ListView {
    /* 数据 */
    protected List<T> mDataList;
    /* 监听器 */
    private SlideAndDragListView.OnDragListener mOnDragListener;
    /* 监听器 */
    private Callback.OnDragDropListener[] mOnDragDropListeners;

    public DragListView(Context context) {
        this(context, null);
    }

    public DragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOnDragDropListeners = new Callback.OnDragDropListener[2];
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
            SlideAndDragListView slideAndDragListView = (SlideAndDragListView) getParent();
            slideAndDragListView.setInterceptTouchEvent(true);
            if (isWannaTransparentWhileDragging) {
                Compat.setBackgroundDrawable(itemMainLayout.getItemCustomView(), backgroundDrawable);
            }
        }
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

    private View getViewByPoint(int x, int y) {
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

    protected void handleDragStarted(int x, int y) {
        View view = getViewByPoint(x, y);
        if (view == null) {
            return;
        }
        if (mOnDragDropListeners[0] != null) {
            mOnDragDropListeners[0].onDragStarted(x, y, view);
        }
        if (mOnDragDropListeners[1] != null) {
            mOnDragDropListeners[1].onDragStarted(x, y, view);
        }
    }

    protected void handleDragMoving(int x, int y) {
        View view = getViewByPoint(x, y);
        if (view == null) {
            return;
        }
        if (mOnDragDropListeners[0] != null) {
            mOnDragDropListeners[0].onDragMoving(x, y, view);
        }
        if (mOnDragDropListeners[1] != null) {
            mOnDragDropListeners[1].onDragMoving(x, y, view);
        }
    }

    protected void handleDragFinished(int x, int y) {
        if (mOnDragDropListeners[0] != null) {
            mOnDragDropListeners[0].onDragFinished(x, y);
        }
        if (mOnDragDropListeners[1] != null) {
            mOnDragDropListeners[1].onDragFinished(x, y);
        }
    }

    protected void add0OnDragDropListener(Callback.OnDragDropListener listener) {
        mOnDragDropListeners[0] = listener;
    }

    protected void add1OnDragDropListener(Callback.OnDragDropListener listener) {
        mOnDragDropListeners[1] = listener;
    }

}
