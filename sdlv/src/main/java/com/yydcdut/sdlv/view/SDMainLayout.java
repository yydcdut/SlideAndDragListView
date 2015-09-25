package com.yydcdut.sdlv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by yuyidong on 15/9/24.
 */
public class SDMainLayout extends FrameLayout {
    private int mHeight;
    private int mBtnWidth;

    public SDMainLayout(Context context) {
        this(context, null);
    }

    public SDMainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SDMainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayoutHeight(int height) {
        mHeight = height;
        requestLayout();
    }

    public void setBtnWidth(int width) {
        mBtnWidth = width;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
