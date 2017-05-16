package com.yydcdut.sdlv;

import android.view.View;

/**
 * Created by yuyidong on 2017/5/16.
 */
interface Callback {

    interface OnDragDropListener {

        void onDragStarted(int x, int y, View view);

        void onDragMoving(int x, int y, View view);

        void onDragFinished(int x, int y);
    }
}
