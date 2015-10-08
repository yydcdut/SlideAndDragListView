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
abstract class WrapperAdapter implements WrapperListAdapter, ItemMainLayout.OnItemSlideListenerProxy, View.OnClickListener,
        AbsListView.OnScrollListener {
    /* 上下文 */
    private Context mContext;
    /* 适配器 */
    private ListAdapter mAdapter;
    /* 用户自定义参数 */
    private AttrsHolder mAttrsHolder;
    /* SDLV */
    private SlideAndDragListView mListView;
    /* 当前滑动的item的位置 */
    private int mSlideItemPosition = -1;
    /* 监听器 */
    private OnAdapterSlideListenerProxy mOnAdapterSlideListenerProxy;
    private OnAdapterButtonClickListenerProxy mOnAdapterButtonClickListenerProxy;

    public WrapperAdapter(Context context, SlideAndDragListView listView, ListAdapter adapter, AttrsHolder attrsHolder) {
        mContext = context;
        mListView = listView;
        mListView.setOnScrollListener(this);
        mAdapter = adapter;
        mAttrsHolder = attrsHolder;
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
        ItemMainLayout itemMainLayout = null;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            itemMainLayout = new ItemMainLayout(mContext);
            itemMainLayout.setLayoutHeight((int) mAttrsHolder.itemHeight, (int) mAttrsHolder.btnWidth, (int) (mAttrsHolder.btnWidth * mAttrsHolder.btnNumber));
            itemMainLayout.getItemBackGroundLayout().getBackGroundImage().setBackgroundDrawable(mAttrsHolder.itemBackGroundDrawable);
            itemMainLayout.getItemCustomLayout().getBackGroundImage().setBackgroundDrawable(mAttrsHolder.itemBackGroundDrawable);
            //setBackgroundDrawable setText 有容错处理
            itemMainLayout.getItemBackGroundLayout().getLeftView().setBackgroundDrawable(mAttrsHolder.btn1Drawable);
            itemMainLayout.getItemBackGroundLayout().getLeftView().setText(mAttrsHolder.btn1Text);
            itemMainLayout.getItemBackGroundLayout().getLeftView().setTextSize(mAttrsHolder.btnTextSize);
            itemMainLayout.getItemBackGroundLayout().getLeftView().setTextColor(mAttrsHolder.btnTextColor);
            itemMainLayout.getItemBackGroundLayout().getLeftView().setOnClickListener(this);
            itemMainLayout.getItemBackGroundLayout().getMiddleView().setBackgroundDrawable(mAttrsHolder.btn2Drawable);
            itemMainLayout.getItemBackGroundLayout().getMiddleView().setText(mAttrsHolder.btn2Text);
            itemMainLayout.getItemBackGroundLayout().getMiddleView().setTextSize(mAttrsHolder.btnTextSize);
            itemMainLayout.getItemBackGroundLayout().getMiddleView().setTextColor(mAttrsHolder.btnTextColor);
            itemMainLayout.getItemBackGroundLayout().getMiddleView().setOnClickListener(this);
            itemMainLayout.getItemBackGroundLayout().getRightView().setBackgroundDrawable(mAttrsHolder.btn3Drawable);
            itemMainLayout.getItemBackGroundLayout().getRightView().setText(mAttrsHolder.btn3Text);
            itemMainLayout.getItemBackGroundLayout().getRightView().setTextSize(mAttrsHolder.btnTextSize);
            itemMainLayout.getItemBackGroundLayout().getRightView().setTextColor(mAttrsHolder.btnTextColor);
            itemMainLayout.getItemBackGroundLayout().getRightView().setOnClickListener(this);
            itemMainLayout.setOnItemSlideListenerProxy(this);
            //判断哪些隐藏哪些显示
            checkVisible(itemMainLayout.getItemBackGroundLayout().getLeftView(), itemMainLayout.getItemBackGroundLayout().getMiddleView(),
                    itemMainLayout.getItemBackGroundLayout().getRightView());
            itemMainLayout.getItemCustomLayout().addCustomView(contentView);
        } else {
            itemMainLayout = (ItemMainLayout) convertView;
            View contentView = mAdapter.getView(position, itemMainLayout.getItemCustomLayout().getCustomView(), parent);
        }
        return itemMainLayout;
    }

    /**
     * 设置哪些button显示哪些button不显示
     *
     * @param left
     * @param middle
     * @param right
     */
    private void checkVisible(View left, View middle, View right) {
        switch (mAttrsHolder.btnNumber) {
            case 0:
                left.setVisibility(View.GONE);
                middle.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                break;
            case 1:
                left.setVisibility(View.VISIBLE);
                middle.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                break;
            case 2:
                left.setVisibility(View.VISIBLE);
                middle.setVisibility(View.VISIBLE);
                right.setVisibility(View.GONE);
                break;
            case 3:
                left.setVisibility(View.VISIBLE);
                middle.setVisibility(View.VISIBLE);
                right.setVisibility(View.VISIBLE);
                break;
            default:
                throw new IllegalArgumentException("");
        }
        left.setClickable(false);
        middle.setClickable(false);
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
        ItemMainLayout itemMainLayout = (ItemMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
        itemMainLayout.getItemBackGroundLayout().getLeftView().setClickable(true);
        itemMainLayout.getItemBackGroundLayout().getMiddleView().setClickable(true);
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
            ItemMainLayout itemMainLayout = (ItemMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
            if (itemMainLayout != null) {
                itemMainLayout.scrollBack();
                itemMainLayout.getItemBackGroundLayout().getLeftView().setClickable(false);
                itemMainLayout.getItemBackGroundLayout().getMiddleView().setClickable(false);
            }
            mSlideItemPosition = -1;
        }
    }

    /**
     * 通过点击位置来判断是点击到的哪个位置
     *
     * @param x
     * @return
     */
    public boolean isTriggerButtonClick(float x) {
        if (mSlideItemPosition != -1) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
            if (itemMainLayout != null) {
                int scrollX = -itemMainLayout.getItemCustomLayout().getScrollX();
                return x < scrollX ? true : false;
            }
        }
        return false;
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
    public void onSlideOpen(View view) {
        if (mOnAdapterSlideListenerProxy != null) {
            mOnAdapterSlideListenerProxy.onSlideOpen(view, mSlideItemPosition);
        }
    }

    @Override
    public void onSlideClose(View view) {
        if (mOnAdapterSlideListenerProxy != null) {
            mOnAdapterSlideListenerProxy.onSlideClose(view, mSlideItemPosition);
        }
        //归位
        returnSlideItemPosition();
    }

    /**
     * 设置监听器
     *
     * @param onAdapterButtonClickListenerProxy
     */
    public void setOnAdapterButtonClickListenerProxy(OnAdapterButtonClickListenerProxy onAdapterButtonClickListenerProxy) {
        mOnAdapterButtonClickListenerProxy = onAdapterButtonClickListenerProxy;
    }

    @Override
    public void onClick(View v) {
        switch ((String) v.getTag()) {
            case ItemBackGroundLayout.TAG_ONE:
                if (mOnAdapterButtonClickListenerProxy != null) {
                    mOnAdapterButtonClickListenerProxy.onClick(v, mSlideItemPosition, 0);
                }
                break;
            case ItemBackGroundLayout.TAG_TWO:
                if (mOnAdapterButtonClickListenerProxy != null) {
                    mOnAdapterButtonClickListenerProxy.onClick(v, mSlideItemPosition, 1);
                }
                break;
            case ItemBackGroundLayout.TAG_THREE:
                if (mOnAdapterButtonClickListenerProxy != null) {
                    mOnAdapterButtonClickListenerProxy.onClick(v, mSlideItemPosition, 3);
                }
                break;
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

    public interface OnAdapterButtonClickListenerProxy {
        void onClick(View v, int itemPosition, int buttonPosition);
    }

    public interface OnAdapterSlideListenerProxy {
        void onSlideOpen(View view, int position);

        void onSlideClose(View view, int position);
    }

    public abstract void onScrollStateChangedProxy(AbsListView view, int scrollState);

    public abstract void onScrollProxy(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);

}
