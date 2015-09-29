package com.yydcdut.sdlv.utils;

import android.widget.AbsListView;

/**
 * Created by yuyidong on 15/9/29.
 */
public interface OnScrollListenerProxy {

    void onScrollStateChanged(AbsListView view, int scrollState);

    void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
}
