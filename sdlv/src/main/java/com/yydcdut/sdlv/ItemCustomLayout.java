package com.yydcdut.sdlv;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by yuyidong on 15/9/25.
 */
class ItemCustomLayout extends FrameLayout {
    private ImageView mBGImage;
    private Drawable mDrawable;
    private Drawable mTransparentDrawable;

    public ItemCustomLayout(Context context) {
        super(context);
//        if (!Compat.afterLollipop()) {
        mBGImage = new ImageView(context);
        addView(mBGImage, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        }
        mTransparentDrawable = new ColorDrawable(Color.TRANSPARENT);
    }

    public void addCustomView(View customView) {
//        if (!Compat.afterLollipop()) {
        addView(customView, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        } else {
//            addView(customView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        }
    }

    public View getCustomView() {
//        if (!Compat.afterLollipop()) {
        return getChildAt(1);
//        } else {
//            return getChildAt(0);
//        }
    }

    public View getRealView() {
        return this;
    }

    public void hideBackground() {
//        if (!Compat.afterLollipop()) {
        mBGImage.setImageDrawable(mTransparentDrawable);
//        } else {
//            Compat.setBackgroundDrawable(this, mTransparentDrawable);
//        }
    }

    public void showBackground() {
//        if (!Compat.afterLollipop()) {
        mBGImage.setImageDrawable(mDrawable);
//        } else {
//            Compat.setBackgroundDrawable(this, mDrawable);
//        }
    }

    public void saveBackground(Drawable drawable) {
        mDrawable = drawable;
        showBackground();
    }

    public boolean isBackgroundShowing() {
//        if (!Compat.afterLollipop()) {
        return mDrawable == mBGImage.getDrawable();
//        } else {
//            return mDrawable == getBackground();
//        }
    }

}
