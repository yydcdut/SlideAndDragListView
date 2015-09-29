package com.yydcdut.sdlv;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import com.yydcdut.sdlv.utils.AttrsHolder;
import com.yydcdut.sdlv.utils.OnAdapterSlideListenerProxy;
import com.yydcdut.sdlv.utils.OnItemSlideListenerProxy;
import com.yydcdut.sdlv.view.SDMainLayout;

/**
 * Created by yuyidong on 15/9/28.
 */
public class WrapperAdapter implements WrapperListAdapter, OnItemSlideListenerProxy {
    private Context mContext;
    private ListAdapter mAdapter;
    private AttrsHolder mAttrsHolder;
    /* 当前滑动的item的位置 */
    private int mSlideItemPosition;
    /* 监听器 */
    private OnAdapterSlideListenerProxy mOnAdapterSlideListenerProxy;

    public WrapperAdapter(Context context, ListAdapter adapter, AttrsHolder attrsHolder) {
        mContext = context;
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
            sdMainLayout.setLayoutHeight((int) mAttrsHolder.itemHeight, (int) mAttrsHolder.btnWidth);
            sdMainLayout.getSDBGLayout().getBackGroundImage().setBackgroundDrawable(mAttrsHolder.itemBackGroundDrawable);
            sdMainLayout.getSDCustomLayout().getBackGroundImage().setBackgroundDrawable(mAttrsHolder.itemBackGroundDrawable);
            sdMainLayout.setBtnTotalWidth((int) (mAttrsHolder.btnWidth * mAttrsHolder.btnNumber));
            sdMainLayout.getSDBGLayout().getLeftView().setBackgroundDrawable(mAttrsHolder.btn1Drawable);
            sdMainLayout.getSDBGLayout().getLeftView().setText(mAttrsHolder.btn1Text);
            sdMainLayout.getSDBGLayout().getLeftView().setTextSize(mAttrsHolder.btnTextSize);
            sdMainLayout.getSDBGLayout().getLeftView().setTextColor(mAttrsHolder.btnTextColor);
            sdMainLayout.getSDBGLayout().getMiddleView().setBackgroundDrawable(mAttrsHolder.btn2Drawable);
            sdMainLayout.getSDBGLayout().getMiddleView().setText(mAttrsHolder.btn2Text);
            sdMainLayout.getSDBGLayout().getMiddleView().setTextSize(mAttrsHolder.btnTextSize);
            sdMainLayout.getSDBGLayout().getMiddleView().setTextColor(mAttrsHolder.btnTextColor);
            sdMainLayout.setOnItemSlideListenerProxy(this);
            //判断哪些隐藏哪些显示
            checkVisible(sdMainLayout.getSDBGLayout().getLeftView(), sdMainLayout.getSDBGLayout().getMiddleView());
            sdMainLayout.getSDCustomLayout().addCustomView(contentView);
        } else {
            sdMainLayout = (SDMainLayout) convertView;
            View contentView = mAdapter.getView(position, sdMainLayout.getSDCustomLayout().getCustomView(), parent);
        }
        //归位
        sdMainLayout.getSDCustomLayout().getRealView().scrollTo(0, 0);
        return sdMainLayout;
    }

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
     * 设置监听器
     *
     * @param onAdapterSlideListenerProxy
     */
    public void setOnAdapterSlideListenerProxy(OnAdapterSlideListenerProxy onAdapterSlideListenerProxy) {
        mOnAdapterSlideListenerProxy = onAdapterSlideListenerProxy;
    }

    /**
     * 设置slide滑开的item的位置
     *
     * @param position
     */
    protected void setSlideItemPosition(int position) {
        mSlideItemPosition = position;
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
    }
}
