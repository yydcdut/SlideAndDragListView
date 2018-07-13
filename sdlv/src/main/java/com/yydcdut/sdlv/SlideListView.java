/*
 * Copyright (C) 2015 yydcdut (yuyidong2015@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yydcdut.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.List;

/**
 * Created by yuyidong on 15/9/28.
 */
class SlideListView extends DragListView implements WrapperAdapter.OnAdapterSlideListenerProxy,
        WrapperAdapter.OnAdapterMenuClickListenerProxy, WrapperAdapter.onItemDeleteListenerProxy,
        WrapperAdapter.OnScrollListenerProxy, AbsListView.OnItemLongClickListener, WrapperAdapter.OnItemScrollBackListenerProxy {
    /* onTouch里面的状态 */
    private static final int STATE_NOTHING = -1;//抬起状态
    private static final int STATE_DOWN = 0;//按下状态
    private static final int STATE_SCROLL = 2;//SCROLL状态
    private static final int STATE_LONG_CLICK_FINISH = 3;//长点击已经触发完成
    private static final int STATE_MORE_FINGERS = 4;//多个手指
    private int mState = STATE_NOTHING;

    /* Scroll 归位的时候点击位置 */
    private static final int RETURN_SCROLL_BACK_OWN = 1;//自己有归位操作
    private static final int RETURN_SCROLL_BACK_OTHER = 2;//其他位置有归位操作
    private static final int RETURN_SCROLL_BACK_CLICK_MENU_BUTTON = 3;//点击到了滑开的item的menuButton上
    private static final int RETURN_SCROLL_BACK_NOTHING = 0;//所以位置都没有回归操作

    /* 是否要触发itemClick */
    private boolean isWannaTriggerClick = true;
    /* 是否在滑动 */
    private boolean isScrolling = false;
    /* 是否正在进行delete的动画 */
    private boolean isDeleteAnimationRunning = false;
    /* 手指放下的坐标 */
    private int mXDown;
    private int mYDown;
    /* Menu */
    private SparseArray<Menu> mMenuSparseArray;
    /* WrapperAdapter */
    private WrapperAdapter mWrapperAdapter;
    /* 手指滑动的最短距离 */
    private int mShortestDistance = 25;
    /* CustomItemView距离左边的距离 */
    private int mItemLeftDistance = 0;
    /* ItemMainView是否正在处理手势操作 */
    private boolean isItemViewHandlingMotionEvent = false;
    /* 是否到了底部 */
    private boolean isAtBottom = false;

    /* 监听器 */
    private SlideAndDragListView.OnSlideListener mOnSlideListener;
    private SlideAndDragListView.OnMenuItemClickListener mOnMenuItemClickListener;
    private Callback.OnItemLongClickListenerWrapper mOnListItemLongClickListener;
    private Callback.OnItemClickListenerWrapper mOnListItemClickListener;
    private SlideAndDragListView.OnItemDeleteListener mOnItemDeleteListener;
    private SlideAndDragListView.OnItemScrollBackListener mOnItemScrollBackListener;
    private Callback.OnScrollListenerWrapper mOnListScrollListener;

    public SlideListView(Context context) {
        this(context, null);
    }

    public SlideListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mShortestDistance = ViewConfiguration.get(context).getScaledTouchSlop();
        super.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        //找到那个位置的view
        View view = getChildAt(position - getFirstVisiblePosition());
        if (mOnListItemLongClickListener != null && view instanceof ItemMainLayout) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) view;
            if (itemMainLayout.getItemCustomView().getLeft() == 0) {
                mState = STATE_LONG_CLICK_FINISH;
                //回滚
                mWrapperAdapter.returnSlideItemPosition();
                //触发回调
                mOnListItemLongClickListener.onListItemLongClick(itemMainLayout.getItemCustomView(), position);
            }
        }
        if (mState == STATE_LONG_CLICK_FINISH || mState == STATE_DOWN) {
            startDrag(position);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isDragging()) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //当Menu滑开，然后在Menu的位置滑动，是不会经过onTouchEvent的ACTION_DOWN的
                //获取出坐标来
                mXDown = (int) ev.getX();
                mYDown = (int) ev.getY();
                //当前state状态为按下
                mState = STATE_DOWN;
                ItemMainLayout itemMainLayoutDown = getItemMainLayoutByPosition((int) ev.getX(), (int) ev.getY());
                if (itemMainLayoutDown != null) {
                    mItemLeftDistance = itemMainLayoutDown.getItemCustomView().getLeft();
                } else {
                    mItemLeftDistance = 0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (fingerLeftAndRightMove(ev)) {//上下范围在50，主要检测左右滑动
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isDeleteAnimationRunning) {
            return false;
        }
        if (isScrolling) {
            if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_CANCEL) {
                reset();
                isWannaTriggerClick = isAtBottom ? true : isWannaTriggerClick;
                isScrolling = isAtBottom ? false : isScrolling;
            }
            return super.onTouchEvent(ev);
        }
        if (isDragging()) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //获取出坐标来
                mXDown = (int) ev.getX();
                mYDown = (int) ev.getY();
                //当前state状态为按下
                mState = STATE_DOWN;
                //得到当前Item滑动了多少
                ItemMainLayout itemMainLayoutDown = getItemMainLayoutByPosition(mXDown, mYDown);
                if (itemMainLayoutDown != null) {
                    mItemLeftDistance = itemMainLayoutDown.getItemCustomView().getLeft();
                } else {
                    mItemLeftDistance = 0;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mState = STATE_MORE_FINGERS;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (fingerLeftAndRightMove(ev) && !isItemViewHandlingMotionEvent) {//上下范围在50，主要检测左右滑动
                    int position = pointToPosition(mXDown, mYDown);
                    ItemMainLayout itemMainLayout = getItemMainLayoutByPosition(mXDown, mYDown);
                    if (itemMainLayout != null) {
                        //判断是不是点在menu上面了
                        if (mItemLeftDistance > 0) { //已经向右滑动了，而且滑开了
                            if (ev.getX() < mItemLeftDistance) {//手指的位置再Menu
                                return true;
                            }
                        } else if (mItemLeftDistance < 0) {//已经向左滑动了，而且滑开了
                            if (ev.getX() > mItemLeftDistance + itemMainLayout.getItemCustomView().getWidth()) {
                                return true;
                            }
                        }

                        //没有点在menu上面
                        if (isFingerMoving2Right(ev)) {//如果想向右滑动
                            if (!itemMainLayout.getItemLeftBackGroundLayout().hasMenuItemViews() &&
                                    itemMainLayout.getScrollState() == ItemMainLayout.SCROLL_STATE_CLOSE) {//但是又没有Left的Menu
                                mState = STATE_NOTHING;
                                return true;
                            }
                        } else if (isFingerMoving2Left(ev)) {//如果想向左滑动
                            if (!itemMainLayout.getItemRightBackGroundLayout().hasMenuItemViews() &&
                                    itemMainLayout.getScrollState() == ItemMainLayout.SCROLL_STATE_CLOSE) {//但是又没有Right的Menu
                                mState = STATE_NOTHING;
                                return true;
                            }
                        }
                        //将当前想要滑动哪一个传递给wrapperAdapter
                        mWrapperAdapter.setSlideItemPosition(position);
                        isItemViewHandlingMotionEvent = true;
                        mState = STATE_SCROLL;
                        itemMainLayout.handleMotionEvent(ev, mXDown, mYDown, mItemLeftDistance);
                        return true;
                    } else {
                        mState = STATE_NOTHING;
                        return true;
                    }
                } else {
                    if (isItemViewHandlingMotionEvent) {
                        ItemMainLayout itemMainLayout = getItemMainLayoutByPosition(mXDown, mYDown);
                        if (itemMainLayout != null) {
                            itemMainLayout.handleMotionEvent(ev, mXDown, mYDown, mItemLeftDistance);
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                int position = pointToPosition(mXDown, mYDown);
                if (position != AdapterView.INVALID_POSITION) {
                    if (mState == STATE_DOWN || mState == STATE_LONG_CLICK_FINISH) {
                        //是否ScrollBack了，是的话就不去执行onListItemClick操作了
                        int scrollBackState = scrollBack(position, ev.getX());
                        if (scrollBackState == RETURN_SCROLL_BACK_NOTHING) {
                            if (mOnListItemClickListener != null && isWannaTriggerClick && !isScrolling) {
                                View v = getChildAt(position - getFirstVisiblePosition());
                                if (v instanceof ItemMainLayout) {
                                    ItemMainLayout itemMainLayout = (ItemMainLayout) v;
                                    mOnListItemClickListener.onListItemClick(itemMainLayout.getItemCustomView(), position);
                                }
                            }
                        }
                    } else {
                        ItemMainLayout itemMainLayout = getItemMainLayoutByPosition(mXDown, mYDown);
                        if (itemMainLayout != null) {
                            itemMainLayout.handleMotionEvent(ev, mXDown, mYDown, -1);
                        }
                    }
                }
                mState = STATE_NOTHING;
                mItemLeftDistance = 0;
                isItemViewHandlingMotionEvent = false;
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                reset();
                break;
            default:
                break;

        }
        return super.onTouchEvent(ev);
    }

    /**
     * 归位状态
     */
    private void reset() {
        mState = STATE_NOTHING;
        mItemLeftDistance = 0;
        isItemViewHandlingMotionEvent = false;
    }

    /**
     * 将滑开的item归位
     *
     * @param position
     * @param x        坐标
     * @return
     */
    private int scrollBack(int position, float x) {
        //是不是当前滑开的这个
        if (mWrapperAdapter.getSlideItemPosition() == position) {
            int scrollBackSituation = mWrapperAdapter.returnSlideItemPosition(x);
            switch (scrollBackSituation) {
                case ItemMainLayout.SCROLL_BACK_CLICK_OWN:
                    return RETURN_SCROLL_BACK_OWN;
                case ItemMainLayout.SCROLL_BACK_ALREADY_CLOSED:
                    return RETURN_SCROLL_BACK_NOTHING;
                case ItemMainLayout.SCROLL_BACK_CLICK_MENU_BUTTON:
                    return RETURN_SCROLL_BACK_CLICK_MENU_BUTTON;
            }
        } else if (mWrapperAdapter.getSlideItemPosition() != -1) {
            mWrapperAdapter.returnSlideItemPosition();
            return RETURN_SCROLL_BACK_OTHER;
        }
        return RETURN_SCROLL_BACK_NOTHING;
    }

    /**
     * 用于drag的ScrollBack逻辑操作
     *
     * @param position
     * @return true--->可以drag false--->不能drag
     */
    private boolean scrollBackByDrag(int position) {
        //是不是当前滑开的这个
        if (mWrapperAdapter.getSlideItemPosition() == position) {
            return false;
        } else if (mWrapperAdapter.getSlideItemPosition() != -1) {
            mWrapperAdapter.returnSlideItemPosition();
            return true;
        }
        return true;
    }

    /**
     * 左右得超出50，上下不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerLeftAndRightMove(MotionEvent ev) {
        return ((ev.getX() - mXDown > mShortestDistance || ev.getX() - mXDown < -mShortestDistance) &&
                ev.getY() - mYDown < mShortestDistance && ev.getY() - mYDown > -mShortestDistance);
    }

    /**
     * 是不是向右滑动
     *
     * @return
     */
    private boolean isFingerMoving2Right(MotionEvent ev) {
        return (ev.getX() - mXDown > mShortestDistance);
    }

    /**
     * 是不是向左滑动
     *
     * @return
     */
    private boolean isFingerMoving2Left(MotionEvent ev) {
        return (ev.getX() - mXDown < -mShortestDistance);
    }

    /**
     * 通过手指的XY坐标得到ItemMainLayout
     *
     * @param x
     * @param y
     * @return
     */
    private ItemMainLayout getItemMainLayoutByPosition(int x, int y) {
        int position = pointToPosition(x, y);
        if (position != AdapterView.INVALID_POSITION) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof ItemMainLayout) {
                ItemMainLayout itemMainLayout = (ItemMainLayout) view;
                return itemMainLayout;
            }
        }
        return null;
    }

    /**
     * 设置Menu
     *
     * @param menu
     */
    public void setMenu(Menu menu) {
        if (mMenuSparseArray != null) {
            mMenuSparseArray.clear();
        } else {
            mMenuSparseArray = new SparseArray<>();
        }
        mMenuSparseArray.put(menu.getMenuViewType(), menu);
    }

    /**
     * 设置menu
     *
     * @param list
     */
    public void setMenu(List<Menu> list) {
        if (mMenuSparseArray != null) {
            mMenuSparseArray.clear();
        } else {
            mMenuSparseArray = new SparseArray<>();
        }
        for (Menu menu : list) {
            mMenuSparseArray.put(menu.getMenuViewType(), menu);
        }
    }

    /**
     * 设置Menu
     *
     * @param menus
     */
    public void setMenu(Menu... menus) {
        if (mMenuSparseArray != null) {
            mMenuSparseArray.clear();
        } else {
            mMenuSparseArray = new SparseArray<>();
        }
        for (Menu menu : menus) {
            mMenuSparseArray.put(menu.getMenuViewType(), menu);
        }
    }

    /**
     * 关闭打开了的Item
     */
    public void closeSlidedItem() {
        if (mWrapperAdapter == null) {
            return;
        }
        mWrapperAdapter.returnSlideItemPosition();
    }

    /**
     * 删除滑开的item
     */
    public void deleteSlideItem() {
        if (mWrapperAdapter == null) {
            return;
        }
        mWrapperAdapter.deleteSlideItemPosition();
    }

    /**
     * 滑开 position 位置上的item
     *
     * @param position  index
     * @param direction 方向
     */
    public void slideItem(int position, int direction) {
        if (mWrapperAdapter == null || position < getFirstVisiblePosition() || position > getLastVisiblePosition()
                || mMenuSparseArray == null || mMenuSparseArray.get(mWrapperAdapter.getItemViewType(position)) == null
                || (direction != MenuItem.DIRECTION_LEFT && direction != MenuItem.DIRECTION_RIGHT)) {
            return;
        }
        List<MenuItem> list = mMenuSparseArray.get(mWrapperAdapter.getItemViewType(position)).getMenuItems(direction);
        if (list.size() == 0) {
            return;
        }
        if (mWrapperAdapter.getSlideItemPosition() != 0 && mWrapperAdapter.getSlideItemPosition() != position) {
            closeSlidedItem();
        }
        mWrapperAdapter.slideItem(position, direction);
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        if (mMenuSparseArray == null || mMenuSparseArray.size() == 0) {
            throw new IllegalArgumentException("Set Menu first!");
        }
        mWrapperAdapter = new WrapperAdapter(getContext(), this, adapter, mMenuSparseArray);
        mWrapperAdapter.setOnAdapterSlideListenerProxy(this);
        mWrapperAdapter.setOnAdapterMenuClickListenerProxy(this);
        mWrapperAdapter.setOnItemDeleteListenerProxy(this);
        mWrapperAdapter.setOnScrollListenerProxy(this);
        mWrapperAdapter.setOnItemScrollBackListenerProxy(this);
        super.setAdapter(mWrapperAdapter);
    }

    protected WrapperAdapter getWrapperAdapter() {
        return mWrapperAdapter;
    }

    @Override
    public void onScrollStateChangedProxy(AbsListView view, int scrollState) {
        if (scrollState == WrapperAdapter.SCROLL_STATE_IDLE) {
            isWannaTriggerClick = true;
            isScrolling = false;
        } else {
            isWannaTriggerClick = false;
            isScrolling = true;
        }
        if (mOnListScrollListener != null) {
            mOnListScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScrollProxy(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isAtBottom = firstVisibleItem + visibleItemCount >= totalItemCount;
        if (mOnListScrollListener != null) {
            mOnListScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onDeleteBegin() {
        isDeleteAnimationRunning = true;
    }

    @Override
    public void onItemDelete(View view, int position) {
        isDeleteAnimationRunning = false;
        if (mOnItemDeleteListener != null && view instanceof ItemMainLayout) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) view;
            mOnItemDeleteListener.onItemDeleteAnimationFinished(itemMainLayout.getItemCustomView(), position);
        }
    }

    /**
     * 添加Drag
     *
     * @param position
     * @return
     */
    public boolean startDrag(int position) {
        boolean canDrag = scrollBackByDrag(position);
        //找到那个位置的view
        View view = getChildAt(position - getFirstVisiblePosition());
        if (canDrag && view instanceof ItemMainLayout) {
            setDragPosition(position);
        }
        return canDrag && view instanceof ItemMainLayout;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mWrapperAdapter != null) {
            mWrapperAdapter.addDataSetObserver();
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mWrapperAdapter != null) {
            mWrapperAdapter.removeDataSetObserver();
        }
    }

    /**
     * 设置item滑动监听器
     *
     * @param listener
     */
    public void setOnSlideListener(SlideAndDragListView.OnSlideListener listener) {
        mOnSlideListener = listener;
    }


    @Override
    public void onSlideOpen(View view, int position, int direction) {
        if (mOnSlideListener != null && view instanceof ItemMainLayout) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) view;
            mOnSlideListener.onSlideOpen(itemMainLayout.getItemCustomView(), this, position, direction);
        }
    }

    @Override
    public void onSlideClose(View view, int position, int direction) {
        if (mOnSlideListener != null && view instanceof ItemMainLayout) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) view;
            mOnSlideListener.onSlideClose(itemMainLayout.getItemCustomView(), this, position, direction);
        }
    }

    /**
     * 设置item中的button点击事件的监听器
     *
     * @param onMenuItemClickListener
     */
    public void setOnMenuItemClickListener(SlideAndDragListView.OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        if (mOnMenuItemClickListener != null) {
            return mOnMenuItemClickListener.onMenuItemClick(v, itemPosition, buttonPosition, direction);
        }
        return Menu.ITEM_NOTHING;
    }

    @Override
    public void setOnItemClickListener(final OnItemClickListener listener) {
        if (listener == null) {
            mOnListItemClickListener = null;
            return;
        }
        mOnListItemClickListener = new Callback.OnItemClickListenerWrapper() {
            @Override
            public void onListItemClick(View v, int position) {
                listener.onItemClick(SlideListView.this, v, position, SlideListView.this.getItemIdAtPosition(position));
            }
        };
    }

    @Override
    public void setOnItemLongClickListener(final OnItemLongClickListener listener) {
        if (listener == null) {
            mOnListItemLongClickListener = null;
            return;
        }
        mOnListItemLongClickListener = new Callback.OnItemLongClickListenerWrapper() {
            @Override
            public void onListItemLongClick(View view, int position) {
                listener.onItemLongClick(SlideListView.this, view, position, SlideListView.this.getItemIdAtPosition(position));
            }
        };
    }

    public void setOnItemDeleteListener(SlideAndDragListView.OnItemDeleteListener onItemDeleteListener) {
        mOnItemDeleteListener = onItemDeleteListener;
    }

    public void setOnItemScrollBackListener(SlideAndDragListView.OnItemScrollBackListener onItemScrollBackListener) {
        mOnItemScrollBackListener = onItemScrollBackListener;
    }


    @Override
    public void setOnScrollListener(final OnScrollListener l) {
        if (l == null) {
            mOnListScrollListener = null;
            return;
        }
        mOnListScrollListener = new Callback.OnScrollListenerWrapper() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                l.onScrollStateChanged(view, scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                l.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        };
    }

    /**
     * 自己用的类
     *
     * @param l
     */
    protected void setOnSuperScrollListener(OnScrollListener l) {
        super.setOnScrollListener(l);
    }

    @Override
    public void onScrollBack(View view, int position) {
        if (mOnItemScrollBackListener != null) {
            mOnItemScrollBackListener.onScrollBackAnimationFinished(view, position);
        }
    }
}
