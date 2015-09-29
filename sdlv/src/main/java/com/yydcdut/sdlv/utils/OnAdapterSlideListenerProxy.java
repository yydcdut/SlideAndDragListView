package com.yydcdut.sdlv.utils;

import android.view.View;

/**
 * Created by yuyidong on 15/9/29.
 */
public interface OnAdapterSlideListenerProxy {
    void onSlideOpen(View view, int position);

    void onSlideClose(View view, int position);
}
