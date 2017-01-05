package com.yydcdut.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/9/24.
 */
class ItemBackGroundLayout extends ViewGroup {
    /* 下一个View的距离 */
    private int mMarginLeft = 0;
    private int mMarginRight = 0;
    /* 添加的子View */
    private List<View> mViewsList;

    public ItemBackGroundLayout(Context context) {
        this(context, null);
    }

    public ItemBackGroundLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemBackGroundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewsList = new ArrayList<>();
        setVisibility(GONE);
    }

    protected View addMenuItem(MenuItem menuItem) {
        int count = getChildCount();
        BaseLayout parent = new SDMenuItemView(getContext(), menuItem);
        parent.build();
        addView(parent, count);
        mViewsList.add(parent);
        return parent;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int total = getChildCount();
        for (int i = 0; i < total; i++) {
            BaseLayout view = (BaseLayout) getChildAt(i);
            MenuItem menuItem = view.mMenuItem;
            measureChild(view, MeasureSpec.makeMeasureSpec(menuItem.width, MeasureSpec.EXACTLY), heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int total = getChildCount();
        mMarginLeft = 0;
        mMarginRight = getMeasuredWidth();
        for (int i = 0; i < total; i++) {
            BaseLayout view = (BaseLayout) getChildAt(i);
            MenuItem menuItem = view.mMenuItem;
            if (menuItem.direction == MenuItem.DIRECTION_LEFT) {
                view.layout(mMarginLeft, t, menuItem.width + mMarginLeft, b);
                mMarginLeft += menuItem.width;
            } else {
                view.layout(mMarginRight - menuItem.width, t, mMarginRight, b);
                mMarginRight -= menuItem.width;
            }
        }
    }

    protected List<View> getViewsList() {
        return mViewsList;
    }
}
