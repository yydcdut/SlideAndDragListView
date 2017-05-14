package com.yydcdut.sdlv;

import android.view.View;

/**
 * Created by yuyidong on 2017/5/14.
 */
interface OnDragDropListener {

    void onDragStarted(int x, int y, View view);

    void onDragMoving(int x, int y, View view);

    void onDragFinished(int x, int y);
}
