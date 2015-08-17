package com.yydcdut.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by yuyidong on 15/8/15.
 */
public class SDItemLayout extends RelativeLayout {
    private int mHeight;

    public SDItemLayout(Context context) {
        super(context);
    }

    public SDItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SDItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setItemHeight(int height) {
        mHeight = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeight > 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, mHeight);
        }
    }
}
