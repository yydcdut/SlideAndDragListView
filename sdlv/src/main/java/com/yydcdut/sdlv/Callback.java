/*
 * Copyright (C) 2015 yydcdut (yuyidong2015@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yydcdut.sdlv;

import android.view.View;
import android.widget.AbsListView;

/**
 * Created by yuyidong on 2017/5/16.
 */
interface Callback {

    interface OnDragDropListener {

        boolean onDragStarted(int x, int y, View view);

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
