package com.yydcdut.sdlv;

import android.view.View;
import android.widget.AbsListView;

/**
 * Created by yuyidong on 2017/5/16.
 */
interface Callback {

    interface OnDragDropListener {

        void onDragStarted(int x, int y, View view);

        void onDragMoving(int x, int y, View view);

        void onDragFinished(int x, int y);
    }

    interface OnScrollListenerWrapper {

        void onScrollStateChanged(AbsListView view, int scrollState);

        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    interface OnItemLongClickListenerWrapper {
        void onListItemLongClick(View view, int position);
    }

    interface OnItemClickListenerWrapper {
        void onListItemClick(View v, int position);
    }
}
