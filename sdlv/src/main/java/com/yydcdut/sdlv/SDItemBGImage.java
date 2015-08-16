package com.yydcdut.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yuyidong on 15/8/16.
 */
public class SDItemBGImage extends ImageView {
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

    public void setHeight(int height) {
        mHeight = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, mHeight);
    }
}
