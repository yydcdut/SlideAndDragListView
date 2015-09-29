package com.yydcdut.sdlv;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import com.yydcdut.sdlv.utils.AttrsHolder;
import com.yydcdut.sdlv.utils.OnAdapterButtonClickListenerProxy;
import com.yydcdut.sdlv.utils.OnAdapterSlideListenerProxy;
import com.yydcdut.sdlv.utils.OnItemSlideListenerProxy;
import com.yydcdut.sdlv.utils.OnScrollListenerProxy;
import com.yydcdut.sdlv.view.SDBGLayout;
import com.yydcdut.sdlv.view.SDMainLayout;

/**
 * Created by yuyidong on 15/9/28.
 */
public class WrapperAdapter implements WrapperListAdapter, OnItemSlideListenerProxy, View.OnClickListener,
        OnScrollListenerProxy {
    /* 上下文 */
    private Context mContext;
    /* 适配器 */
    private ListAdapter mAdapter;
    /* 用户自定义参数 */
    private AttrsHolder mAttrsHolder;
    /* SDLV */
    private SlideAndDragListView1 mListView;
    /* 当前滑动的item的位置 */
    private int mSlideItemPosition = -1;
    /* 监听器 */
    private OnAdapterSlideListenerProxy mOnAdapterSlideListenerProxy;
    private OnAdapterButtonClickListenerProxy mOnAdapterButtonClickListenerProxy;

    public WrapperAdapter(Context context, SlideAndDragListView1 listView, ListAdapter adapter, AttrsHolder attrsHolder) {
        mContext = context;
        mListView = listView;
        mListView.setOnScrollListenerProxy(this);
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

    //todo 容错处理
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SDMainLayout sdMainLayout = null;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            sdMainLayout = new SDMainLayout(mContext);
            sdMainLayout.setLayoutHeight((int) mAttrsHolder.itemHeight, (int) mAttrsHolder.btnWidth, (int) (mAttrsHolder.btnWidth * mAttrsHolder.btnNumber));
            sdMainLayout.getSDBGLayout().getBackGroundImage().setBackgroundDrawable(mAttrsHolder.itemBackGroundDrawable);
            sdMainLayout.getSDCustomLayout().getBackGroundImage().setBackgroundDrawable(mAttrsHolder.itemBackGroundDrawable);
            sdMainLayout.getSDBGLayout().getLeftView().setBackgroundDrawable(mAttrsHolder.btn1Drawable);
            sdMainLayout.getSDBGLayout().getLeftView().setText(mAttrsHolder.btn1Text);
            sdMainLayout.getSDBGLayout().getLeftView().setTextSize(mAttrsHolder.btnTextSize);
            sdMainLayout.getSDBGLayout().getLeftView().setTextColor(mAttrsHolder.btnTextColor);
            sdMainLayout.getSDBGLayout().getLeftView().setOnClickListener(this);
            sdMainLayout.getSDBGLayout().getMiddleView().setBackgroundDrawable(mAttrsHolder.btn2Drawable);
            sdMainLayout.getSDBGLayout().getMiddleView().setText(mAttrsHolder.btn2Text);
            sdMainLayout.getSDBGLayout().getMiddleView().setTextSize(mAttrsHolder.btnTextSize);
            sdMainLayout.getSDBGLayout().getMiddleView().setTextColor(mAttrsHolder.btnTextColor);
            sdMainLayout.getSDBGLayout().getMiddleView().setOnClickListener(this);
            sdMainLayout.setOnItemSlideListenerProxy(this);
            //判断哪些隐藏哪些显示
            checkVisible(sdMainLayout.getSDBGLayout().getLeftView(), sdMainLayout.getSDBGLayout().getMiddleView());
            sdMainLayout.getSDCustomLayout().addCustomView(contentView);
        } else {
            sdMainLayout = (SDMainLayout) convertView;
            View contentView = mAdapter.getView(position, sdMainLayout.getSDCustomLayout().getCustomView(), parent);
        }
        return sdMainLayout;
    }

    /**
     * 设置哪些button显示哪些button不显示
     *
     * @param left
     * @param middle
     */
    private void checkVisible(View left, View middle) {
        switch (mAttrsHolder.btnNumber) {
            case 0:
                left.setVisibility(View.GONE);
                middle.setVisibility(View.GONE);
                break;
            case 1:
                left.setVisibility(View.VISIBLE);
                middle.setVisibility(View.GONE);
                break;
            case 2:
                left.setVisibility(View.VISIBLE);
                middle.setVisibility(View.VISIBLE);
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
        SDMainLayout sdMainLayout = (SDMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
        sdMainLayout.getSDBGLayout().getLeftView().setClickable(true);
        sdMainLayout.getSDBGLayout().getMiddleView().setClickable(true);
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
            SDMainLayout sdMainLayout = (SDMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
            if (sdMainLayout != null) {
                sdMainLayout.scrollBack();
                sdMainLayout.getSDBGLayout().getLeftView().setClickable(false);
                sdMainLayout.getSDBGLayout().getMiddleView().setClickable(false);
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
            SDMainLayout sdMainLayout = (SDMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
            if (sdMainLayout != null) {
                int scrollX = -sdMainLayout.getSDCustomLayout().getScrollX();
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
            case SDBGLayout.TAG_ONE:
                if (mOnAdapterButtonClickListenerProxy != null) {
                    mOnAdapterButtonClickListenerProxy.onClick(v, mSlideItemPosition, 0);
                }
                break;
            case SDBGLayout.TAG_TWO:
                if (mOnAdapterButtonClickListenerProxy != null) {
                    mOnAdapterButtonClickListenerProxy.onClick(v, mSlideItemPosition, 1);
                }
                break;
            case SDBGLayout.TAG_THREE:
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
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
