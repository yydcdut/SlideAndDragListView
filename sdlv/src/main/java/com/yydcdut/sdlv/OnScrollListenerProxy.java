package com.yydcdut.sdlv;

import android.widget.AbsListView;

/**
 * Created by yuyidong on 15/9/29.
 */
interface OnScrollListenerProxy {

    void onScrollStateChanged(AbsListView view, int scrollState);

    void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
}
