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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;
import android.widget.WrapperListAdapter;

import java.util.List;

/**
 * Created by yuyidong on 2017/5/10.
 */
public class SlideAndDragListView<T> extends FrameLayout implements Callback.OnDragDropListener {
    /* drag的时候透明度 */
    private static final float DRAG_VIEW_ALPHA = 0.7f;
    /* drag的View */
    private ImageView mDragView;
    /* Inner View */
    private SlideListView<T> mSlideListView;
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

    public SlideAndDragListView(Context context) {
        this(context, null);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        createView(context, attrs);
    }

    private void createView(Context context, AttributeSet attrs) {
        mSlideListView = new SlideListView(context, attrs);
        addView(mSlideListView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mDragView = new ImageView(context);
        addView(mDragView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDragView.setVisibility(GONE);
        mSlideListView.add0OnDragDropListener(this);
    }

    protected void setInterceptTouchEvent(boolean interceptTouchEvent) {
        this.interceptTouchEvent = interceptTouchEvent;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchDownForDragStartY = (int) ev.getY();
        }
        if (interceptTouchEvent) {
            final int boundGap = (int) (getHeight() * BOUND_GAP_RATIO);
            mTopScrollBound = (getTop() + boundGap);
            mBottomScrollBound = (getBottom() - boundGap);
            mSlideListView.handleDragStarted((int) ev.getX(), (int) ev.getY());
        }
        return interceptTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
        return interceptTouchEvent || super.onTouchEvent(event);
    }

    @Override
    public void onDragStarted(int x, int y, View view) {
        mDragViewBitmap = createDraggedChildBitmap(view);
        if (mDragViewBitmap == null) {
            return;
        }
        mDragView.setImageBitmap(mDragViewBitmap);
        mDragView.setVisibility(VISIBLE);
        mDragView.setAlpha(DRAG_VIEW_ALPHA);
        mDragView.setX(mSlideListView.getPaddingLeft() + getPaddingLeft());
        mDragDelta = y - view.getTop();
        mDragView.setY(y - mDragDelta);
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
    public void onDragMoving(int x, int y, View view) {
        mDragView.setX(mSlideListView.getPaddingLeft() + getPaddingLeft());
        mDragView.setY(y - mDragDelta);
    }

    @Override
    public void onDragFinished(int x, int y) {
        mDragDelta = 0;
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

    private void ensureScrollHandler() {
        if (mScrollHandler == null) {
            mScrollHandler = getHandler();
        }
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

    /**
     * @return The maximum amount a list view will scroll in response to
     * an arrow event.
     */
    public int getMaxScrollAmount() {
        return mSlideListView.getMaxScrollAmount();
    }

    /**
     * Add a fixed view to appear at the top of the list. If this method is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(ListAdapter)}. Starting with
     * {@link android.os.Build.VERSION_CODES#KITKAT}, this method may be
     * called at any time. If the ListView's adapter does not extend
     * {@link HeaderViewListAdapter}, it will be wrapped with a supporting
     * instance of {@link WrapperListAdapter}.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable whether the item is selectable
     */
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        mSlideListView.addHeaderView(v, data, isSelectable);
    }

    /**
     * Add a fixed view to appear at the top of the list. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(ListAdapter)}. Starting with
     * {@link android.os.Build.VERSION_CODES#KITKAT}, this method may be
     * called at any time. If the ListView's adapter does not extend
     * {@link HeaderViewListAdapter}, it will be wrapped with a supporting
     * instance of {@link WrapperListAdapter}.
     *
     * @param v The view to add.
     */
    public void addHeaderView(View v) {
        mSlideListView.addHeaderView(v);
    }

    public int getHeaderViewsCount() {
        return mSlideListView.getHeaderViewsCount();
    }

    /**
     * Removes a previously-added header view.
     *
     * @param v The view to remove
     * @return true if the view was removed, false if the view was not a header
     * view
     */
    public boolean removeHeaderView(View v) {
        return mSlideListView.removeHeaderView(v);
    }


    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(ListAdapter)}. Starting with
     * {@link android.os.Build.VERSION_CODES#KITKAT}, this method may be
     * called at any time. If the ListView's adapter does not extend
     * {@link HeaderViewListAdapter}, it will be wrapped with a supporting
     * instance of {@link WrapperListAdapter}.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable true if the footer view can be selected
     */
    public void addFooterView(View v, Object data, boolean isSelectable) {
        mSlideListView.addFooterView(v, data, isSelectable);
    }

    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(ListAdapter)}. Starting with
     * {@link android.os.Build.VERSION_CODES#KITKAT}, this method may be
     * called at any time. If the ListView's adapter does not extend
     * {@link HeaderViewListAdapter}, it will be wrapped with a supporting
     * instance of {@link WrapperListAdapter}.
     *
     * @param v The view to add.
     */
    public void addFooterView(View v) {
        mSlideListView.addFooterView(v);
    }

    public int getFooterViewsCount() {
        return mSlideListView.getFooterViewsCount();
    }

    /**
     * Removes a previously-added footer view.
     *
     * @param v The view to remove
     * @return true if the view was removed, false if the view was not a footer view
     */
    public boolean removeFooterView(View v) {
        return mSlideListView.removeFooterView(v);
    }

    /**
     * Returns the adapter currently in use in this ListView. The returned adapter
     * might not be the same adapter passed to {@link #setAdapter(ListAdapter)} but
     * might be a {@link WrapperListAdapter}.
     *
     * @return The adapter currently used to display data in this ListView.
     * @see #setAdapter(ListAdapter)
     */
    public ListAdapter getAdapter() {
        return mSlideListView.getAdapter();
    }

    /**
     * Sets up this AbsListView to use a remote views adapter which connects to a RemoteViewsService
     * through the specified intent.
     *
     * @param intent the intent used to identify the RemoteViewsService for the adapter to connect to.
     */
    public void setRemoteViewsAdapter(Intent intent) {
        mSlideListView.setRemoteViewsAdapter(intent);
    }

    /**
     * Sets the data behind this ListView.
     * <p>
     * The adapter passed to this method may be wrapped by a {@link WrapperListAdapter},
     * depending on the ListView features currently in use. For instance, adding
     * headers and/or footers will cause the adapter to be wrapped.
     *
     * @param adapter The ListAdapter which is responsible for maintaining the
     *                data backing this list and for producing a view to represent an
     *                item in that data set.
     * @see #getAdapter()
     */
    public void setAdapter(ListAdapter adapter) {
        mSlideListView.setAdapter(adapter);
    }

    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        return mSlideListView.requestChildRectangleOnScreen(child, rect, immediate);
    }

    /**
     * Smoothly scroll to the specified adapter position. The view will
     * scroll such that the indicated position is displayed.
     *
     * @param position Scroll to this adapter position.
     */
    public void smoothScrollToPosition(int position) {
        mSlideListView.smoothScrollToPosition(position);
    }

    /**
     * Smoothly scroll to the specified adapter position offset. The view will
     * scroll such that the indicated position is displayed.
     *
     * @param offset The amount to offset from the adapter position to scroll to.
     */
    public void smoothScrollByOffset(int offset) {
        mSlideListView.smoothScrollByOffset(offset);
    }

    /**
     * Sets the currently selected item. If in touch mode, the item will not be selected
     * but it will still be positioned appropriately. If the specified selection position
     * is less than 0, then the item at position 0 will be selected.
     *
     * @param position Index (starting at 0) of the data item to be selected.
     */
    public void setSelection(int position) {
        mSlideListView.setSelection(position);
    }

    /**
     * setSelectionAfterHeaderView set the selection to be the first list item
     * after the header views.
     */
    public void setSelectionAfterHeaderView() {
        mSlideListView.setSelectionAfterHeaderView();
    }

    /**
     * Indicates that the views created by the ListAdapter can contain focusable
     * items.
     *
     * @param itemsCanFocus true if items can get focus, false otherwise
     */
    public void setItemsCanFocus(boolean itemsCanFocus) {
        mSlideListView.setItemsCanFocus(itemsCanFocus);
    }

    public boolean isOpaque() {
        return mSlideListView.isOpaque();
    }

    /**
     * @return Whether the views created by the ListAdapter can contain focusable
     * items.
     */
    public boolean getItemsCanFocus() {
        return mSlideListView.getItemsCanFocus();
    }

    public void setCacheColorHint(int color) {
        mSlideListView.setCacheColorHint(color);
    }

    /**
     * Returns the drawable that will be drawn between each item in the list.
     *
     * @return the current drawable drawn between list elements
     */
    public Drawable getDivider() {
        return mSlideListView.getDivider();
    }

    /**
     * Sets the drawable that will be drawn between each item in the list. If the drawable does
     * not have an intrinsic height, you should also call {@link #setDividerHeight(int)}
     *
     * @param divider The drawable to use.
     */
    public void setDivider(Drawable divider) {
        mSlideListView.setDivider(divider);
    }

    /**
     * @return Returns the height of the divider that will be drawn between each item in the list.
     */
    public int getDividerHeight() {
        return mSlideListView.getDividerHeight();
    }

    /**
     * Sets the height of the divider that will be drawn between each item in the list. Calling
     * this will override the intrinsic height as set by {@link #setDivider(Drawable)}
     *
     * @param height The new height of the divider in pixels.
     */
    public void setDividerHeight(int height) {
        mSlideListView.setDividerHeight(height);
    }

    /**
     * Enables or disables the drawing of the divider for header views.
     *
     * @param headerDividersEnabled True to draw the headers, false otherwise.
     * @see #setFooterDividersEnabled(boolean)
     * @see #areHeaderDividersEnabled()
     * @see #addHeaderView(android.view.View)
     */
    public void setHeaderDividersEnabled(boolean headerDividersEnabled) {
        mSlideListView.setHeaderDividersEnabled(headerDividersEnabled);
    }

    /**
     * @return Whether the drawing of the divider for header views is enabled
     * @see #setHeaderDividersEnabled(boolean)
     */
    public boolean areHeaderDividersEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return mSlideListView.areHeaderDividersEnabled();
        } else {
            return false;
        }
    }

    /**
     * Enables or disables the drawing of the divider for footer views.
     *
     * @param footerDividersEnabled True to draw the footers, false otherwise.
     * @see #setHeaderDividersEnabled(boolean)
     * @see #areFooterDividersEnabled()
     * @see #addFooterView(android.view.View)
     */
    public void setFooterDividersEnabled(boolean footerDividersEnabled) {
        mSlideListView.setFooterDividersEnabled(footerDividersEnabled);
    }

    /**
     * @return Whether the drawing of the divider for footer views is enabled
     * @see #setFooterDividersEnabled(boolean)
     */
    public boolean areFooterDividersEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return mSlideListView.areFooterDividersEnabled();
        } else {
            return false;
        }
    }

    /**
     * Sets the drawable that will be drawn above all other list content.
     * This area can become visible when the user overscrolls the list.
     *
     * @param header The drawable to use
     */
    public void setOverscrollHeader(Drawable header) {
        mSlideListView.setOverscrollHeader(header);
    }

    /**
     * @return The drawable that will be drawn above all other list content
     */
    public Drawable getOverscrollHeader() {
        return mSlideListView.getOverscrollHeader();
    }

    /**
     * Sets the drawable that will be drawn below all other list content.
     * This area can become visible when the user overscrolls the list,
     * or when the list's content does not fully fill the container area.
     *
     * @param footer The drawable to use
     */
    public void setOverscrollFooter(Drawable footer) {
        mSlideListView.setOverscrollFooter(footer);
    }

    /**
     * @return The drawable that will be drawn below all other list content
     */
    public Drawable getOverscrollFooter() {
        return mSlideListView.getOverscrollFooter();
    }

    /**
     * Returns the set of checked items ids. The result is only valid if the
     * choice mode has not been set to {@link android.widget.AbsListView#CHOICE_MODE_NONE}.
     *
     * @return A new array which contains the id of each checked item in the
     * list.
     * @deprecated Use {@link #getCheckedItemIds()} instead.
     */
    @Deprecated
    public long[] getCheckItemIds() {
        return mSlideListView.getCheckItemIds();
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mSlideListView.onInitializeAccessibilityEvent(event);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSlideListView.onInitializeAccessibilityNodeInfo(info);
        }
    }

    public void onInitializeAccessibilityNodeInfoForItem(
            View view, int position, AccessibilityNodeInfo info) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSlideListView.onInitializeAccessibilityNodeInfoForItem(view, position, info);
        }
    }

    //-------------------    AbsListView    -------------------

    @Override
    public void setOverScrollMode(int mode) {
        mSlideListView.setOverScrollMode(mode);
    }

    /**
     * Returns the number of items currently selected. This will only be valid
     * if the choice mode is not {@link AbsListView#CHOICE_MODE_NONE} (default).
     * <p>
     * <p>To determine the specific items that are currently selected, use one of
     * the <code>getChecked*</code> methods.
     *
     * @return The number of items currently selected
     * @see #getCheckedItemPosition()
     * @see #getCheckedItemPositions()
     * @see #getCheckedItemIds()
     */
    public int getCheckedItemCount() {
        return mSlideListView.getCheckedItemCount();
    }

    /**
     * Returns the checked state of the specified position. The result is only
     * valid if the choice mode has been set to {@link AbsListView#CHOICE_MODE_SINGLE}
     * or {@link AbsListView#CHOICE_MODE_MULTIPLE}.
     *
     * @param position The item whose checked state to return
     * @return The item's checked state or <code>false</code> if choice mode
     * is invalid
     * @see #setChoiceMode(int)
     */
    public boolean isItemChecked(int position) {
        return mSlideListView.isItemChecked(position);
    }

    /**
     * Returns the currently checked item. The result is only valid if the choice
     * mode has been set to {@link AbsListView#CHOICE_MODE_SINGLE}.
     *
     * @return The position of the currently checked item or
     * {@link AbsListView#INVALID_POSITION} if nothing is selected
     * @see #setChoiceMode(int)
     */
    public int getCheckedItemPosition() {
        return mSlideListView.getCheckedItemPosition();
    }

    /**
     * Returns the set of checked items in the list. The result is only valid if
     * the choice mode has not been set to {@link AbsListView#CHOICE_MODE_NONE}.
     *
     * @return A SparseBooleanArray which will return true for each call to
     * get(int position) where position is a checked position in the
     * list and false otherwise, or <code>null</code> if the choice
     * mode is set to {@link AbsListView#CHOICE_MODE_NONE}.
     */
    public SparseBooleanArray getCheckedItemPositions() {
        return mSlideListView.getCheckedItemPositions();
    }

    /**
     * Returns the set of checked items ids. The result is only valid if the
     * choice mode has not been set to {@link AbsListView#CHOICE_MODE_NONE} and the adapter
     * has stable IDs. ({@link ListAdapter#hasStableIds()} == {@code true})
     *
     * @return A new array which contains the id of each checked item in the
     * list.
     */
    public long[] getCheckedItemIds() {
        return mSlideListView.getCheckedItemIds();
    }

    /**
     * Clear any choices previously set
     */
    public void clearChoices() {
        mSlideListView.clearChoices();
    }

    /**
     * Sets the checked state of the specified position. The is only valid if
     * the choice mode has been set to {@link AbsListView#CHOICE_MODE_SINGLE} or
     * {@link AbsListView#CHOICE_MODE_MULTIPLE}.
     *
     * @param position The item whose checked state is to be checked
     * @param value    The new checked state for the item
     */
    public void setItemChecked(int position, boolean value) {
        mSlideListView.setItemChecked(position, value);
    }

    public boolean performItemClick(View view, int position, long id) {
        return mSlideListView.performItemClick(view, position, id);
    }

    /**
     * @return The current choice mode
     * @see #setChoiceMode(int)
     */
    public int getChoiceMode() {
        return mSlideListView.getChoiceMode();
    }

    /**
     * Defines the choice behavior for the List. By default, Lists do not have any choice behavior
     * ({@link AbsListView#CHOICE_MODE_NONE}). By setting the choiceMode to {@link AbsListView#CHOICE_MODE_SINGLE}, the
     * List allows up to one item to  be in a chosen state. By setting the choiceMode to
     * {@link AbsListView#CHOICE_MODE_MULTIPLE}, the list allows any number of items to be chosen.
     *
     * @param choiceMode One of {@link AbsListView#CHOICE_MODE_NONE}, {@link AbsListView#CHOICE_MODE_SINGLE}, or
     *                   {@link AbsListView#CHOICE_MODE_MULTIPLE}
     */
    public void setChoiceMode(int choiceMode) {
        mSlideListView.setChoiceMode(choiceMode);
    }

    /**
     * Set a {@link AbsListView.MultiChoiceModeListener} that will manage the lifecycle of the
     * selection {@link ActionMode}. Only used when the choice mode is set to
     * {@link AbsListView#CHOICE_MODE_MULTIPLE_MODAL}.
     *
     * @param listener Listener that will manage the selection mode
     * @see #setChoiceMode(int)
     */
    public void setMultiChoiceModeListener(AbsListView.MultiChoiceModeListener listener) {
        mSlideListView.setMultiChoiceModeListener(listener);
    }

    /**
     * Specifies whether fast scrolling is enabled or disabled.
     * <p>
     * When fast scrolling is enabled, the user can quickly scroll through lists
     * by dragging the fast scroll thumb.
     * <p>
     * If the adapter backing this list implements {@link SectionIndexer}, the
     * fast scroller will display section header previews as the user scrolls.
     * Additionally, the user will be able to quickly jump between sections by
     * tapping along the length of the scroll bar.
     *
     * @param enabled true to enable fast scrolling, false otherwise
     * @see SectionIndexer
     * @see #isFastScrollEnabled()
     */
    public void setFastScrollEnabled(final boolean enabled) {
        mSlideListView.setFastScrollEnabled(enabled);
    }

    /**
     * Specifies the style of the fast scroller decorations.
     *
     * @param styleResId style resource containing fast scroller properties
     * @see android[dot]R[dot]styleable[dot]FastScroll
     */
    public void setFastScrollStyle(int styleResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSlideListView.setFastScrollStyle(styleResId);
        }
    }

    /**
     * Set whether or not the fast scroller should always be shown in place of
     * the standard scroll bars. This will enable fast scrolling if it is not
     * already enabled.
     * <p>
     * Fast scrollers shown in this way will not fade out and will be a
     * permanent fixture within the list. This is best combined with an inset
     * scroll bar style to ensure the scroll bar does not overlap content.
     *
     * @param alwaysShow true if the fast scroller should always be displayed,
     *                   false otherwise
     * @see #setScrollBarStyle(int)
     * @see #setFastScrollEnabled(boolean)
     */
    public void setFastScrollAlwaysVisible(final boolean alwaysShow) {
        mSlideListView.setFastScrollAlwaysVisible(alwaysShow);
    }

    /**
     * Returns true if the fast scroller is set to always show on this view.
     *
     * @return true if the fast scroller will always show
     * @see #setFastScrollAlwaysVisible(boolean)
     */
    public boolean isFastScrollAlwaysVisible() {
        return mSlideListView.isFastScrollAlwaysVisible();
    }

    @Override
    public int getVerticalScrollbarWidth() {
        return mSlideListView.getVerticalScrollbarWidth();
    }

    /**
     * Returns true if the fast scroller is enabled.
     *
     * @return true if fast scroll is enabled, false otherwise
     * @see #setFastScrollEnabled(boolean)
     */
    @ViewDebug.ExportedProperty
    public boolean isFastScrollEnabled() {
        return mSlideListView.isFastScrollEnabled();
    }

    @Override
    public void setVerticalScrollbarPosition(int position) {
        mSlideListView.setVerticalScrollbarPosition(position);
    }

    @Override
    public void setScrollBarStyle(int style) {
        mSlideListView.setScrollBarStyle(style);
    }

    /**
     * When smooth scrollbar is enabled, the position and size of the scrollbar thumb
     * is computed based on the number of visible pixels in the visible items. This
     * however assumes that all list items have the same height. If you use a list in
     * which items have different heights, the scrollbar will change appearance as the
     * user scrolls through the list. To avoid this issue, you need to disable this
     * property.
     * <p>
     * When smooth scrollbar is disabled, the position and size of the scrollbar thumb
     * is based solely on the number of items in the adapter and the position of the
     * visible items inside the adapter. This provides a stable scrollbar as the user
     * navigates through a list of items with varying heights.
     *
     * @param enabled Whether or not to enable smooth scrollbar.
     * @attr ref android.R.styleable#AbsListView_smoothScrollbar
     * @see #setSmoothScrollbarEnabled(boolean)
     */
    public void setSmoothScrollbarEnabled(boolean enabled) {
        mSlideListView.setSmoothScrollbarEnabled(enabled);
    }

    /**
     * Returns the current state of the fast scroll feature.
     *
     * @return True if smooth scrollbar is enabled is enabled, false otherwise.
     * @see #setSmoothScrollbarEnabled(boolean)
     */
    public boolean isSmoothScrollbarEnabled() {
        return mSlideListView.isSmoothScrollbarEnabled();
    }

    @Override
    public void sendAccessibilityEvent(int eventType) {
        mSlideListView.sendAccessibilityEvent(eventType);
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return mSlideListView.performAccessibilityAction(action, arguments);
        } else {
            return false;
        }
    }

    /**
     * Indicates whether the children's drawing cache is used during a scroll.
     * By default, the drawing cache is enabled but this will consume more memory.
     *
     * @return true if the scrolling cache is enabled, false otherwise
     * @see #setScrollingCacheEnabled(boolean)
     * @see View#setDrawingCacheEnabled(boolean)
     */
    public boolean isScrollingCacheEnabled() {
        return mSlideListView.isScrollingCacheEnabled();
    }

    /**
     * Enables or disables the children's drawing cache during a scroll.
     * By default, the drawing cache is enabled but this will use more memory.
     * <p>
     * When the scrolling cache is enabled, the caches are kept after the
     * first scrolling. You can manually clear the cache by calling
     * {@link android.view.ViewGroup#setChildrenDrawingCacheEnabled(boolean)}.
     *
     * @param enabled true to enable the scroll cache, false otherwise
     * @see #isScrollingCacheEnabled()
     * @see View#setDrawingCacheEnabled(boolean)
     */
    public void setScrollingCacheEnabled(boolean enabled) {
        mSlideListView.setScrollingCacheEnabled(enabled);
    }

    /**
     * Enables or disables the type filter window. If enabled, typing when
     * this view has focus will filter the children to match the users input.
     * Note that the {@link Adapter} used by this view must implement the
     * {@link Filterable} interface.
     *
     * @param textFilterEnabled true to enable type filtering, false otherwise
     * @see Filterable
     */
    public void setTextFilterEnabled(boolean textFilterEnabled) {
        mSlideListView.setTextFilterEnabled(textFilterEnabled);
    }

    /**
     * Indicates whether type filtering is enabled for this view
     *
     * @return true if type filtering is enabled, false otherwise
     * @see #setTextFilterEnabled(boolean)
     * @see Filterable
     */
    @ViewDebug.ExportedProperty
    public boolean isTextFilterEnabled() {
        return mSlideListView.isTextFilterEnabled();
    }

    @Override
    public void getFocusedRect(Rect r) {
        mSlideListView.getFocusedRect(r);
    }

    /**
     * Indicates whether the content of this view is pinned to, or stacked from,
     * the bottom edge.
     *
     * @return true if the content is stacked from the bottom edge, false otherwise
     */
    @ViewDebug.ExportedProperty
    public boolean isStackFromBottom() {
        return mSlideListView.isStackFromBottom();
    }

    /**
     * When stack from bottom is set to true, the list fills its content starting from
     * the bottom of the view.
     *
     * @param stackFromBottom true to pin the view's content to the bottom edge,
     *                        false to pin the view's content to the top edge
     */
    public void setStackFromBottom(boolean stackFromBottom) {
        mSlideListView.setStackFromBottom(stackFromBottom);
    }

    /**
     * Sets the initial value for the text filter.
     *
     * @param filterText The text to use for the filter.
     * @see #setTextFilterEnabled
     */
    public void setFilterText(String filterText) {
        mSlideListView.setFilterText(filterText);
    }


    /**
     * Returns the list's text filter, if available.
     *
     * @return the list's text filter or null if filtering isn't enabled
     */
    public CharSequence getTextFilter() {
        return mSlideListView.getTextFilter();
    }

    @ViewDebug.ExportedProperty
    public View getSelectedView() {
        return mSlideListView.getSelectedView();
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @return The top list padding.
     * @see android.view.View#getPaddingTop()
     * @see #getSelector()
     */
    public int getListPaddingTop() {
        return mSlideListView.getListPaddingTop();
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @return The bottom list padding.
     * @see android.view.View#getPaddingBottom()
     * @see #getSelector()
     */
    public int getListPaddingBottom() {
        return mSlideListView.getListPaddingBottom();
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @return The left list padding.
     * @see android.view.View#getPaddingLeft()
     * @see #getSelector()
     */
    public int getListPaddingLeft() {
        return mSlideListView.getListPaddingLeft();
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @return The right list padding.
     * @see android.view.View#getPaddingRight()
     * @see #getSelector()
     */
    public int getListPaddingRight() {
        return mSlideListView.getListPaddingRight();
    }

    /**
     * Controls whether the selection highlight drawable should be drawn on top of the item or
     * behind it.
     *
     * @param onTop If true, the selector will be drawn on the item it is highlighting. The default
     *              is false.
     * @attr ref android.R.styleable#AbsListView_drawSelectorOnTop
     */
    public void setDrawSelectorOnTop(boolean onTop) {
        mSlideListView.setDrawSelectorOnTop(onTop);
    }

    /**
     * Set a Drawable that should be used to highlight the currently selected item.
     *
     * @param resID A Drawable resource to use as the selection highlight.
     * @attr ref android.R.styleable#AbsListView_listSelector
     */
    public void setSelector(int resID) {
        mSlideListView.setSelector(resID);
    }

    public void setSelector(Drawable sel) {
        mSlideListView.setSelector(sel);
    }

    /**
     * Returns the selector {@link android.graphics.drawable.Drawable} that is used to draw the
     * selection in the list.
     *
     * @return the drawable used to display the selector
     */
    public Drawable getSelector() {
        return mSlideListView.getSelector();
    }

    public void setScrollIndicators(View up, View down) {
        mSlideListView.setScrollIndicators(up, down);
    }

    /**
     * The amount of friction applied to flings. The default value
     * is {@link ViewConfiguration#getScrollFriction}.
     */
    public void setFriction(float friction) {
        mSlideListView.setFriction(friction);
    }

    /**
     * Sets a scale factor for the fling velocity. The initial scale
     * factor is 1.0.
     *
     * @param scale The scale factor to multiply the velocity by.
     */
    public void setVelocityScale(float scale) {
        mSlideListView.setVelocityScale(scale);
    }

    /**
     * Smoothly scroll to the specified adapter position. The view will scroll
     * such that the indicated position is displayed <code>offset</code> pixels below
     * the top edge of the view. If this is impossible, (e.g. the offset would scroll
     * the first or last item beyond the boundaries of the list) it will get as close
     * as possible. The scroll will take <code>duration</code> milliseconds to complete.
     *
     * @param position Position to scroll to
     * @param offset   Desired distance in pixels of <code>position</code> from the top
     *                 of the view when scrolling is finished
     * @param duration Number of milliseconds to use for the scroll
     */
    public void smoothScrollToPositionFromTop(int position, int offset, int duration) {
        mSlideListView.smoothScrollToPositionFromTop(position, offset, duration);
    }

    /**
     * Smoothly scroll to the specified adapter position. The view will scroll
     * such that the indicated position is displayed <code>offset</code> pixels below
     * the top edge of the view. If this is impossible, (e.g. the offset would scroll
     * the first or last item beyond the boundaries of the list) it will get as close
     * as possible.
     *
     * @param position Position to scroll to
     * @param offset   Desired distance in pixels of <code>position</code> from the top
     *                 of the view when scrolling is finished
     */
    public void smoothScrollToPositionFromTop(int position, int offset) {
        mSlideListView.smoothScrollToPositionFromTop(position, offset);
    }

    /**
     * Smoothly scroll to the specified adapter position. The view will
     * scroll such that the indicated position is displayed, but it will
     * stop early if scrolling further would scroll boundPosition out of
     * view.
     *
     * @param position      Scroll to this adapter position.
     * @param boundPosition Do not scroll if it would move this adapter
     *                      position out of view.
     */
    public void smoothScrollToPosition(int position, int boundPosition) {
        mSlideListView.smoothScrollToPosition(position, boundPosition);
    }

    /**
     * Smoothly scroll by distance pixels over duration milliseconds.
     *
     * @param distance Distance to scroll in pixels.
     * @param duration Duration of the scroll animation in milliseconds.
     */
    public void smoothScrollBy(int distance, int duration) {
        mSlideListView.smoothScrollBy(distance, duration);
    }

    /**
     * Scrolls the list items within the view by a specified number of pixels.
     *
     * @param y the amount of pixels to scroll by vertically
     * @see #canScrollList(int)
     */
    public void scrollListBy(int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSlideListView.scrollListBy(y);
        }
    }

    /**
     * Check if the items in the list can be scrolled in a certain direction.
     *
     * @param direction Negative to check scrolling up, positive to check
     *                  scrolling down.
     * @return true if the list can be scrolled in the specified direction,
     * false otherwise.
     * @see #scrollListBy(int)
     */
    public boolean canScrollList(int direction) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return mSlideListView.canScrollList(direction);
        } else {
            return false;
        }
    }

    /**
     * Causes all the views to be rebuilt and redrawn.
     */
    public void invalidateViews() {
        mSlideListView.invalidateViews();
    }

    /**
     * Puts the list or grid into transcript mode. In this mode the list or grid will always scroll
     * to the bottom to show new items.
     *
     * @param mode the transcript mode to set
     * @see AbsListView#TRANSCRIPT_MODE_DISABLED
     * @see AbsListView#TRANSCRIPT_MODE_NORMAL
     * @see AbsListView#TRANSCRIPT_MODE_ALWAYS_SCROLL
     */
    public void setTranscriptMode(int mode) {
        mSlideListView.setTranscriptMode(mode);
    }

    /**
     * Returns the current transcript mode.
     *
     * @return {@link AbsListView#TRANSCRIPT_MODE_DISABLED}, {@link AbsListView#TRANSCRIPT_MODE_NORMAL} or
     * {@link AbsListView#TRANSCRIPT_MODE_ALWAYS_SCROLL}
     */
    public int getTranscriptMode() {
        return mSlideListView.getTranscriptMode();
    }

    /**
     * When set to a non-zero value, the cache color hint indicates that this list is always drawn
     * on top of a solid, single-color, opaque background
     *
     * @return The cache color hint
     */
    @ViewDebug.ExportedProperty(category = "drawing")
    public int getCacheColorHint() {
        return mSlideListView.getCacheColorHint();
    }

    /**
     * Move all views (excluding headers and footers) held by this AbsListView into the supplied
     * List. This includes views displayed on the screen as well as views stored in AbsListView's
     * internal view recycler.
     *
     * @param views A list into which to put the reclaimed views
     */
    public void reclaimViews(List<View> views) {
        mSlideListView.reclaimViews(views);
    }

    /**
     * Sets the recycler listener to be notified whenever a View is set aside in
     * the recycler for later reuse. This listener can be used to free resources
     * associated to the View.
     *
     * @param listener The recycler listener to be notified of views set aside
     *                 in the recycler.
     * @see android.widget.AbsListView[dot]RecycleBin
     * @see android.widget.AbsListView.RecyclerListener
     */
    public void setRecyclerListener(AbsListView.RecyclerListener listener) {
        mSlideListView.setRecyclerListener(listener);
    }

    /**
     * Sets the selected item and positions the selection y pixels from the top edge
     * of the ListView. (If in touch mode, the item will not be selected but it will
     * still be positioned appropriately.)
     *
     * @param position Index (starting at 0) of the data item to be selected.
     * @param y        The distance from the top edge of the ListView (plus padding) that the
     *                 item will be positioned.
     */
    public void setSelectionFromTop(int position, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSlideListView.setSelectionFromTop(position, y);
        }
    }
    //-------------------    AbsListView    -------------------
    //-------------------    click    -------------------

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mSlideListView.setOnItemClickListener(listener);
    }
    //-------------------    click    -------------------
    //-------------------    long click    -------------------

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked and held
     *
     * @param listener The callback that will run
     */
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mSlideListView.setOnItemLongClickListener(listener);
    }
    //-------------------    long click    -------------------
    //-------------------    scroll    -------------------

    /**
     * Set the listener that will receive notifications every time the list scrolls.
     *
     * @param l the scroll listener
     */
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mSlideListView.setOnScrollListener(l);
    }
    //-------------------    scroll    -------------------

    //-------------------    item delete    -------------------
    public interface OnItemDeleteListener {

        void onItemDeleteAnimationFinished(View view, int position);
    }

    public void setOnItemDeleteListener(SlideAndDragListView.OnItemDeleteListener onItemDeleteListener) {
        mSlideListView.setOnItemDeleteListener(onItemDeleteListener);
    }
    //-------------------    item delete    -------------------

    //-------------------    item scroll back    -------------------
    public interface OnItemScrollBackListener {
        void onScrollBackAnimationFinished(View view, int position);
    }

    public void setOnItemScrollBackListener(OnItemScrollBackListener onItemScrollBackListener) {
        mSlideListView.setOnItemScrollBackListener(onItemScrollBackListener);
    }
    //-------------------    item scroll back    -------------------

    //-------------------    item slide    -------------------

    /**
     * item的滑动的监听器
     */
    public interface OnSlideListener {
        /**
         * 当滑动开的时候触发
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideOpen(View view, View parentView, int position, int direction);

        /**
         * 当滑动归位的时候触发
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideClose(View view, View parentView, int position, int direction);
    }

    /**
     * 设置item滑动监听器
     *
     * @param listener
     */
    public void setOnSlideListener(SlideAndDragListView.OnSlideListener listener) {
        mSlideListView.setOnSlideListener(listener);
    }
    //-------------------    item slide    -------------------

    //-------------------    menu click    -------------------

    /**
     * item中的button监听器
     */
    public interface OnMenuItemClickListener {
        /**
         * 点击事件
         *
         * @param v
         * @param itemPosition   第几个item
         * @param buttonPosition 第几个button
         * @param direction      方向
         * @return 参考Menu的几个常量
         */
        int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction);
    }

    /**
     * 设置item中的button点击事件的监听器
     *
     * @param onMenuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mSlideListView.setOnMenuItemClickListener(onMenuItemClickListener);
    }
    //-------------------    menu click    -------------------

    //-------------------    drag & drop    -------------------

    /**
     * 当发生drag的时候触发的监听器
     */
    public interface OnDragListener {
        /**
         * 开始drag
         *
         * @param position
         */
        void onDragViewStart(int position);

        /**
         * drag的正在移动
         *
         * @param position
         */
        void onDragViewMoving(int position);

        /**
         * drag的放下了
         *
         * @param position
         */
        void onDragViewDown(int position);
    }

    /**
     * 设置drag的监听器，加入数据
     *
     * @param onDragListener
     * @param dataList
     */
    public void setOnDragListener(OnDragListener onDragListener, List<T> dataList) {
        mSlideListView.setOnDragListener(onDragListener, dataList);
    }
    //-------------------    drag & drop    -------------------

    //-------------------    API    -------------------
    public void closeSlidedItem() {
        mSlideListView.closeSlidedItem();
    }

    public void deleteSlideItem() {
        mSlideListView.deleteSlideItem();
    }

    public boolean startDrag(int position) {
        return mSlideListView.startDrag(position);
    }
    //-------------------    API    -------------------

    //-------------------    menu    -------------------

    /**
     * 设置Menu
     *
     * @param menu
     */
    public void setMenu(Menu menu) {
        mSlideListView.setMenu(menu);
    }

    /**
     * 设置menu
     *
     * @param list
     */
    public void setMenu(List<Menu> list) {
        mSlideListView.setMenu(list);
    }

    /**
     * 设置Menu
     *
     * @param menus
     */
    public void setMenu(Menu... menus) {
        mSlideListView.setMenu(menus);
    }
    //-------------------    menu    -------------------

}
