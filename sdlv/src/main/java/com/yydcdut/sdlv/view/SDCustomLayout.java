package com.yydcdut.sdlv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by yuyidong on 15/9/25.
 */
public class SDCustomLayout extends ViewGroup {
    private FrameLayout mCustomView;

    public SDCustomLayout(Context context) {
        this(context, null);
    }

    public SDCustomLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SDCustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ImageView bgImage = new ImageView(context);
        bgImage.setBackgroundColor(0x00ffffff);
        bgImage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(bgImage, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        mCustomView = new FrameLayout(context);
        mCustomView.setBackgroundColor(0x00ffffff);
        mCustomView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mCustomView);
    }

    public FrameLayout getCustomView() {
        return mCustomView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(l, t, r, b);
        }
    }
}
