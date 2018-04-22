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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yuyidong on 2017/5/10.
 */
public class SlideAndDragListView extends SlideListView {

    public SlideAndDragListView(Context context) {
        this(context, null);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //-------------------    item delete    -------------------

    /**
     * item删除的监听
     */
    public interface OnItemDeleteListener {
        /**
         * item被删除动画执行完成之后的回调
         *
         * @param view
         * @param position
         */
        void onItemDeleteAnimationFinished(View view, int position);
    }
    //-------------------    item delete    -------------------

    //-------------------    item scroll back    -------------------

    /**
     * item的滑归位的监听
     */
    public interface OnItemScrollBackListener {
        /**
         * 当滑回去动画结束的时候回调
         *
         * @param view
         * @param position
         */
        void onScrollBackAnimationFinished(View view, int position);
    }
    //-------------------    item scroll back    -------------------

    //-------------------    item slide    -------------------

    /**
     * item的滑动的监听器
     */
    public interface OnSlideListener {
        /**
         * 当滑动开的时候触发
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideOpen(View view, View parentView, int position, int direction);

        /**
         * 当滑动归位的时候触发
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideClose(View view, View parentView, int position, int direction);
    }
    //-------------------    item slide    -------------------

    //-------------------    menu click    -------------------

    /**
     * item中的button监听器
     */
    public interface OnMenuItemClickListener {
        /**
         * 点击事件
         *
         * @param v
         * @param itemPosition   第几个item
         * @param buttonPosition 第几个button
         * @param direction      方向
         * @return 参考Menu的几个常量
         */
        int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction);
    }
    //-------------------    menu click    -------------------

    //-------------------    drag & drop    -------------------

    /**
     * 当发生drag的时候触发的监听器
     */
    public interface OnDragDropListener {
        /**
         * 开始drag
         *
         * @param beginPosition
         */
        void onDragViewStart(int beginPosition);

        /**
         * drag的正在移动
         *
         * @param fromPosition
         * @param toPosition
         */
        void onDragDropViewMoved(int fromPosition, int toPosition);

        /**
         * drag的放下了
         *
         * @param finalPosition
         */
        void onDragViewDown(int finalPosition);
    }
    //-------------------    drag & drop    -------------------

    //-------------------    API    -------------------

    /**
     * 让前面几个item不进行排序操作
     *
     * @param headerCount
     */
    public void setNotDragHeaderCount(int headerCount) {
        WrapperAdapter adapter = getWrapperAdapter();
        if (adapter != null) {
            adapter.setStartLimit(headerCount - 1);
        }
    }

    /**
     * 让租后几个item不进行排序操作
     *
     * @param footerCount
     */
    public void setNotDragFooterCount(int footerCount) {
        WrapperAdapter adapter = getWrapperAdapter();
        if (adapter != null) {
            adapter.setEndLimit(adapter.getCount() - footerCount);
        }
    }
    //-------------------    API    -------------------
}
