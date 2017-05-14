package com.yydcdut.sdlv;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.List;

/**
 * Created by yuyidong on 2017/5/10.
 */
public class SlideAndDragListView<T> extends FrameLayout {
    private ImageView mImageView;
    private SlideListView<T> mSlideListView;

    public SlideAndDragListView(Context context) {
        this(context, null);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createView(context, attrs);
    }

    private void createView(Context context, AttributeSet attrs) {
        mSlideListView = new SlideListView(context, attrs);
        addView(mSlideListView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mImageView = new ImageView(context);
        addView(mImageView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mImageView.setVisibility(GONE);
        mSlideListView.setDragView(mImageView);
    }

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

    /**
     * 关闭打开了的Item
     */
    public void closeSlidedItem() {
        mSlideListView.closeSlidedItem();
    }

    public void deleteSlideItem() {
        mSlideListView.deleteSlideItem();
    }

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

    /**
     * 自己的单击事件
     */
    public interface OnListItemClickListener {
        void onListItemClick(View v, int position);
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnListItemClickListener(SlideAndDragListView.OnListItemClickListener listener) {
        mSlideListView.setOnListItemClickListener(listener);
    }

    /**
     * 自己写的长点击事件
     */
    public interface OnListItemLongClickListener {
        void onListItemLongClick(View view, int position);
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnListItemLongClickListener(SlideAndDragListView.OnListItemLongClickListener listener) {
        mSlideListView.setOnListItemLongClickListener(listener);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(View view, int position);
    }

    public void setOnItemDeleteListener(SlideAndDragListView.OnItemDeleteListener onItemDeleteListener) {
        mSlideListView.setOnItemDeleteListener(onItemDeleteListener);
    }

    public interface OnListScrollListener {
        int SCROLL_STATE_IDLE = 0;
        int SCROLL_STATE_TOUCH_SCROLL = 1;
        int SCROLL_STATE_FLING = 2;

        void onScrollStateChanged(AbsListView view, int scrollState);

        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    public void setOnListScrollListener(SlideAndDragListView.OnListScrollListener onListScrollListener) {
        mSlideListView.setOnListScrollListener(onListScrollListener);
    }

}
