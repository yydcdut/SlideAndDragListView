package com.yydcdut.sdlv;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.Map;

/**
 * Created by yuyidong on 15/9/28.
 */
class WrapperAdapter implements WrapperListAdapter, ItemMainLayout.OnItemSlideListenerProxy, View.OnClickListener,
        AbsListView.OnScrollListener, ItemMainLayout.OnItemDeleteListenerProxy {
    private static final int TAG_LEFT = 3 << 24;
    private static final int TAG_RIGHT = 4 << 24;
    /* 上下文 */
    private Context mContext;
    /* 适配器 */
    private ListAdapter mAdapter;
    /* 用户自定义参数 */
    private Map<Integer, Menu> mMenuMap;
    /* SDLV */
    private SlideAndDragListView mListView;
    /* 当前滑动的item的位置 */
    private int mSlideItemPosition = -1;
    /* 监听器 */
    private OnAdapterSlideListenerProxy mOnAdapterSlideListenerProxy;
    private OnAdapterMenuClickListenerProxy mOnAdapterMenuClickListenerProxy;
    private onItemDeleteListenerProxy mOnItemDeleteListenerProxy;
    private OnScrollListenerProxy mOnScrollListenerProxy;

    protected WrapperAdapter(Context context, SlideAndDragListView listView, ListAdapter adapter, Map<Integer, Menu> map) {
        //// TODO: 2017/3/7 NULL context,listview,adapter,map
        mContext = context;
        mListView = listView;
        mListView.setOnSuperScrollListener(this);
        mAdapter = adapter;
        mMenuMap = map;
        mAdapter.registerDataSetObserver(mDataSetObserver);
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

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            returnSlideItemPosition();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

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
        ItemMainLayout itemMainLayout = null;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            itemMainLayout = new ItemMainLayout(mContext, contentView);
            int type = mAdapter.getItemViewType(position);
            Menu menu = mMenuMap.get(type);
            if (menu == null) {
                throw new IllegalArgumentException("No menu matches any view types in ListView");
            }
            itemMainLayout.setParams(menu.getTotalBtnLength(MenuItem.DIRECTION_LEFT),
                    menu.getTotalBtnLength(MenuItem.DIRECTION_RIGHT), menu.isWannaOver());
            createMenu(menu, itemMainLayout);
            itemMainLayout.setOnItemSlideListenerProxy(this);
            itemMainLayout.setSelector(mListView.getSelector());
        } else {
            itemMainLayout = (ItemMainLayout) convertView;
            mAdapter.getView(position, itemMainLayout.getItemCustomView(), parent);
        }
        return itemMainLayout;
    }

    /**
     * 创建Menu
     *
     * @param itemMainLayout
     */
    private void createMenu(Menu menu, ItemMainLayout itemMainLayout) {
        if (menu.getTotalBtnLength(MenuItem.DIRECTION_LEFT) > 0) {
            for (int i = 0; i < menu.getMenuItems(MenuItem.DIRECTION_LEFT).size(); i++) {
                View v = itemMainLayout.getItemLeftBackGroundLayout().addMenuItem(menu.getMenuItems(MenuItem.DIRECTION_LEFT).get(i));
                v.setOnClickListener(this);
                v.setTag(TAG_LEFT, i);
            }
        } else {
            itemMainLayout.getItemLeftBackGroundLayout().setVisibility(View.GONE);
        }
        if (menu.getTotalBtnLength(MenuItem.DIRECTION_RIGHT) > 0) {
            for (int i = 0; i < menu.getMenuItems(MenuItem.DIRECTION_RIGHT).size(); i++) {
                View v = itemMainLayout.getItemRightBackGroundLayout().addMenuItem(menu.getMenuItems(MenuItem.DIRECTION_RIGHT).get(i));
                v.setOnClickListener(this);
                v.setTag(TAG_RIGHT, i);
            }
        } else {
            itemMainLayout.getItemRightBackGroundLayout().setVisibility(View.GONE);
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
     * 归位mSlideItemPosition
     */
    protected void returnSlideItemPosition() {
        if (mSlideItemPosition != -1) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
            if (itemMainLayout != null) {
                itemMainLayout.scrollBack();
            }
            mSlideItemPosition = -1;
        }
    }

    protected void deleteSlideItemPosition() {
        if (mSlideItemPosition != -1) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
            if (itemMainLayout != null) {
                itemMainLayout.deleteItem(this);
            }
        }
    }

    /**
     * @param x
     * @return 是否滑动归位了
     */
    protected int returnSlideItemPosition(float x) {
        if (mSlideItemPosition != -1) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
            if (itemMainLayout != null) {
                int scrollBackSituation = itemMainLayout.scrollBack(x);
                switch (scrollBackSituation) {
                    case ItemMainLayout.SCROLL_BACK_ALREADY_CLOSED:
                    case ItemMainLayout.SCROLL_BACK_CLICK_OWN:
                        mSlideItemPosition = -1;
                        break;
                    case ItemMainLayout.SCROLL_BACK_CLICK_MENU_BUTTON:
                        break;
                }
                return scrollBackSituation;
            }
            mSlideItemPosition = -1;
            return ItemMainLayout.SCROLL_BACK_CLICK_NOTHING;
        }
        return ItemMainLayout.SCROLL_BACK_CLICK_NOTHING;
    }

    protected boolean isWannaTransparentWhileDragging(int position) {
        int type = getItemViewType(position);
        Menu menu = mMenuMap.get(type);
        return menu.isWannaTransparentWhileDragging();
    }

    protected void removeDataSetObserver() {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
    }

    /**
     * 设置监听器
     *
     * @param onAdapterSlideListenerProxy
     */
    protected void setOnAdapterSlideListenerProxy(OnAdapterSlideListenerProxy onAdapterSlideListenerProxy) {
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
    }

    /**
     * 设置监听器
     *
     * @param onAdapterMenuClickListenerProxy
     */
    protected void setOnAdapterMenuClickListenerProxy(OnAdapterMenuClickListenerProxy onAdapterMenuClickListenerProxy) {
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
                    deleteSlideItemPosition();
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
        if (mOnScrollListenerProxy != null) {
            mOnScrollListenerProxy.onScrollStateChangedProxy(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListenerProxy != null) {
            mOnScrollListenerProxy.onScrollProxy(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onDeleteBegin() {
        if (mOnItemDeleteListenerProxy != null) {
            mOnItemDeleteListenerProxy.onDeleteBegin();
        }
    }

    @Override
    public void onDelete(View view) {
        int position = mSlideItemPosition;
        if (mSlideItemPosition != -1) {
            if (mOnItemDeleteListenerProxy != null) {
                mOnItemDeleteListenerProxy.onItemDelete(view, position);
            }
            mSlideItemPosition = -1;
        }

    }

    protected interface OnAdapterMenuClickListenerProxy {
        int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction);
    }

    protected interface OnAdapterSlideListenerProxy {
        void onSlideOpen(View view, int position, int direction);

        void onSlideClose(View view, int position, int direction);
    }

    protected void setOnScrollListenerProxy(OnScrollListenerProxy onScrollListenerProxy) {
        mOnScrollListenerProxy = onScrollListenerProxy;
    }

    protected interface OnScrollListenerProxy {
        void onScrollStateChangedProxy(AbsListView view, int scrollState);

        void onScrollProxy(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    protected void setOnItemDeleteListenerProxy(onItemDeleteListenerProxy onItemDeleteListenerProxy) {
        mOnItemDeleteListenerProxy = onItemDeleteListenerProxy;
    }

    protected interface onItemDeleteListenerProxy {
        void onDeleteBegin();

        void onItemDelete(View view, int position);
    }
}
