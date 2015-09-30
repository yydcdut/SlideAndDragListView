package com.yydcdut.sdlv;

import android.view.View;

/**
 * Created by yuyidong on 15/9/29.
 */
interface OnAdapterSlideListenerProxy {
    void onSlideOpen(View view, int position);

    void onSlideClose(View view, int position);
}
