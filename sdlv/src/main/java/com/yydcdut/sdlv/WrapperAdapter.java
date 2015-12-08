package com.yydcdut.sdlv;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

/**
 * Created by yuyidong on 15/9/28.
 */
abstract class WrapperAdapter implements WrapperListAdapter, ItemMainLayout2.OnItemSlideListenerProxy, View.OnClickListener,
        AbsListView.OnScrollListener, ItemMainLayout2.OnItemDeleteListenerProxy {
    private static final int TAG_LEFT = 3 << 24;
    private static final int TAG_RIGHT = 4 << 24;
    /* 上下文 */
    private Context mContext;
    /* 适配器 */
    private ListAdapter mAdapter;
    /* 用户自定义参数 */
    private Menu mMenu;
    /* SDLV */
    private SlideAndDragListView mListView;
    /* 当前滑动的item的位置 */
    private int mSlideItemPosition = -1;
    /* 监听器 */
    private OnAdapterSlideListenerProxy mOnAdapterSlideListenerProxy;
    private OnAdapterMenuClickListenerProxy mOnAdapterMenuClickListenerProxy;

    public WrapperAdapter(Context context, SlideAndDragListView listView, ListAdapter adapter, Menu menu) {
        mContext = context;
        mListView = listView;
        mListView.setOnScrollListener(this);
        mAdapter = adapter;
        mMenu = menu;
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemMainLayout2 itemMainLayout2 = null;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            itemMainLayout2 = new ItemMainLayout2(mContext);
            itemMainLayout2.setParams(mMenu.getItemHeight(), mMenu.getTotalBtnLength(MenuItem.DIRECTION_LEFT),
                    mMenu.getTotalBtnLength(MenuItem.DIRECTION_RIGHT), mMenu.isWannaOver());
            createMenu(itemMainLayout2);
            itemMainLayout2.getItemCustomLayout().saveBackground(mMenu.getItemBackGroundDrawable());
            itemMainLayout2.setOnItemSlideListenerProxy(this);
            itemMainLayout2.getItemCustomLayout().addCustomView(contentView);
        } else {
            itemMainLayout2 = (ItemMainLayout2) convertView;
            mAdapter.getView(position, itemMainLayout2.getItemCustomLayout().getCustomView(), parent);
        }
        return itemMainLayout2;
    }

    /**
     * 创建Menu
     *
     * @param itemMainLayout2
     */
    private void createMenu(ItemMainLayout2 itemMainLayout2) {
        if (mMenu.getTotalBtnLength(MenuItem.DIRECTION_LEFT) > 0) {
            Compat.setBackgroundDrawable(itemMainLayout2.getItemLeftBackGroundLayout().getBackGroundImage(), mMenu.getItemBackGroundDrawable());
            for (int i = 0; i < mMenu.getMenuItems(MenuItem.DIRECTION_LEFT).size(); i++) {
                View v = itemMainLayout2.getItemLeftBackGroundLayout().addMenuItem(mMenu.getMenuItems(MenuItem.DIRECTION_LEFT).get(i));
                v.setOnClickListener(this);
                v.setClickable(false);
                v.setTag(TAG_LEFT, i);
            }
        } else {
            itemMainLayout2.getItemLeftBackGroundLayout().setVisibility(View.GONE);
        }
        if (mMenu.getTotalBtnLength(MenuItem.DIRECTION_RIGHT) > 0) {
            Compat.setBackgroundDrawable(itemMainLayout2.getItemRightBackGroundLayout().getBackGroundImage(), mMenu.getItemBackGroundDrawable());
            for (int i = 0; i < mMenu.getMenuItems(MenuItem.DIRECTION_RIGHT).size(); i++) {
                View v = itemMainLayout2.getItemRightBackGroundLayout().addMenuItem(mMenu.getMenuItems(MenuItem.DIRECTION_RIGHT).get(i));
                v.setOnClickListener(this);
                v.setClickable(false);
                v.setTag(TAG_RIGHT, i);
            }
        } else {
            itemMainLayout2.getItemRightBackGroundLayout().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    /**
     * 设置slide滑开的item的位置
     *
     * @param position
     */
    protected void setSlideItemPosition(int position) {
        if (mSlideItemPosition != -1 && mSlideItemPosition != position) {
            returnSlideItemPosition();
        }
        if (mSlideItemPosition == position) {//已经执行过下面的操作了，就不要再去操作了。
            return;
        }
        mSlideItemPosition = position;
        ItemMainLayout2 itemMainLayout2 = (ItemMainLayout2) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
        for (View v : itemMainLayout2.getItemLeftBackGroundLayout().getBtnViews()) {
            v.setClickable(true);
        }
        for (View v : itemMainLayout2.getItemRightBackGroundLayout().getBtnViews()) {
            v.setClickable(true);
        }
    }

    /**
     * 得到当前滑开的item的位置
     *
     * @return
     */
    protected int getSlideItemPosition() {
        return mSlideItemPosition;
    }

    /**
     * 归位mSlideItemPosition，button不可点击
     */
    public void returnSlideItemPosition() {
        if (mSlideItemPosition != -1) {
            ItemMainLayout2 itemMainLayout2 = (ItemMainLayout2) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
            if (itemMainLayout2 != null) {
                itemMainLayout2.scrollBack();
                for (View v : itemMainLayout2.getItemLeftBackGroundLayout().getBtnViews()) {
                    v.setClickable(false);
                }
                for (View v : itemMainLayout2.getItemRightBackGroundLayout().getBtnViews()) {
                    v.setClickable(false);
                }
            }
            mSlideItemPosition = -1;
        }
    }

    /**
     * 设置监听器
     *
     * @param onAdapterSlideListenerProxy
     */
    public void setOnAdapterSlideListenerProxy(OnAdapterSlideListenerProxy onAdapterSlideListenerProxy) {
        mOnAdapterSlideListenerProxy = onAdapterSlideListenerProxy;
    }

    @Override
    public void onSlideOpen(View view, int direction) {
        if (mOnAdapterSlideListenerProxy != null) {
            mOnAdapterSlideListenerProxy.onSlideOpen(view, mSlideItemPosition, direction);
        }
    }

    @Override
    public void onSlideClose(View view, int direction) {
        if (mOnAdapterSlideListenerProxy != null) {
            mOnAdapterSlideListenerProxy.onSlideClose(view, mSlideItemPosition, direction);
        }
        //归位
        returnSlideItemPosition();
    }

    /**
     * 设置监听器
     *
     * @param onAdapterMenuClickListenerProxy
     */
    public void setOnAdapterMenuClickListenerProxy(OnAdapterMenuClickListenerProxy onAdapterMenuClickListenerProxy) {
        mOnAdapterMenuClickListenerProxy = onAdapterMenuClickListenerProxy;
    }

    @Override
    public void onClick(View v) {
        if (mOnAdapterMenuClickListenerProxy != null) {
            int scroll = mOnAdapterMenuClickListenerProxy.onMenuItemClick(v, mSlideItemPosition,
                    (Integer) (v.getTag(TAG_LEFT) != null ? v.getTag(TAG_LEFT) : v.getTag(TAG_RIGHT)),
                    v.getTag(TAG_LEFT) != null ? MenuItem.DIRECTION_LEFT : MenuItem.DIRECTION_RIGHT);
            switch (scroll) {
                case Menu.ITEM_NOTHING:
                    break;
                case Menu.ITEM_SCROLL_BACK:
                    //归位
                    returnSlideItemPosition();
                    break;
                case Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP:
                    if (mSlideItemPosition != -1) {
                        ItemMainLayout2 itemMainLayout2 = (ItemMainLayout2) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
                        if (itemMainLayout2 != null) {
                            itemMainLayout2.deleteItem(this);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //当发生滑动的时候归位
        if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            returnSlideItemPosition();
        }
        onScrollStateChangedProxy(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        onScrollProxy(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    public void onDelete(View view) {
        int position = mSlideItemPosition;
        onItemDelete(view, position);
        mSlideItemPosition = -1;
    }

    public interface OnAdapterMenuClickListenerProxy {
        int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction);
    }

    public interface OnAdapterSlideListenerProxy {
        void onSlideOpen(View view, int position, int direction);

        void onSlideClose(View view, int position, int direction);
    }

    public abstract void onScrollStateChangedProxy(AbsListView view, int scrollState);

    public abstract void onScrollProxy(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);

    public abstract void onItemDelete(View view, int position);

}
