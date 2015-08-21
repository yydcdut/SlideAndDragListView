package com.yydcdut.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yuyidong on 15/8/16.
 */
final class SDItemBGImage extends ImageView {
    private int mHeight;

    public SDItemBGImage(Context context) {
        super(context);
    }

    public SDItemBGImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SDItemBGImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setItemHeight(int height) {
        mHeight = height;
        requestLayout();
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
