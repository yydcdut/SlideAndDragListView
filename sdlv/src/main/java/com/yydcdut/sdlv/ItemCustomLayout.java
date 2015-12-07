package com.yydcdut.sdlv;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by yuyidong on 15/9/25.
 */
class ItemCustomLayout extends FrameLayout {
    private Drawable mDrawable;

    public ItemCustomLayout(Context context) {
        this(context, null);
    }

    public ItemCustomLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemCustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addCustomView(View customView) {
        addView(customView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public View getCustomView() {
        return getChildAt(0);
    }

    public View getRealView() {
        return this;
    }

    public void hideBackground() {
        Compat.setBackgroundDrawable(this, new ColorDrawable(Color.TRANSPARENT));
    }

    public void showBackground() {
        Compat.setBackgroundDrawable(this, mDrawable);
    }

    public void saveBackground(Drawable drawable) {
        mDrawable = drawable;
        showBackground();
    }

    public boolean isBackgroundShowing() {
        return mDrawable == getBackground();
    }

}
