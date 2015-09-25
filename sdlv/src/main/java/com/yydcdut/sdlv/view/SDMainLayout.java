package com.yydcdut.sdlv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by yuyidong on 15/9/24.
 */
public class SDMainLayout extends ViewGroup {
    private int mHeight;

    private SDBGLayout mSDBGLayout;
    private SDCustomLayout mSDCustomLayout;

    public SDMainLayout(Context context) {
        this(context, null);
    }

    public SDMainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SDMainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSDBGLayout = new SDBGLayout(context);
        addView(mSDBGLayout);
        mSDCustomLayout = new SDCustomLayout(context);
        addView(mSDCustomLayout);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(l, t, r, b);
        }
    }

    public void setLayoutHeight(int height, int btnWidth) {
        mHeight = height;
        mSDBGLayout.setBtnWidth(btnWidth);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeight > 0) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mHeight);
            for (int i = 0; i < getChildCount(); i++) {
                measureChild(getChildAt(i), widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
