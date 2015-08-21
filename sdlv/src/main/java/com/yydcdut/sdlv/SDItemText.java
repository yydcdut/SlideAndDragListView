package com.yydcdut.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yuyidong on 15/8/17.
 */
final class SDItemText extends TextView {

    private int mWidth;
    private int mHeight;

    public SDItemText(Context context) {
        super(context);
    }

    public SDItemText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SDItemText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBtnHeight(int height) {
        mHeight = height;
        requestLayout();
    }

    public void setBtnWidth(int width) {
        mWidth = width;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeight > 0 && mWidth > 0) {
            setMeasuredDimension(mWidth, mHeight);
        }
    }
}
