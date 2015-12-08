package com.yydcdut.sdlv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by yuyidong on 15/9/25.
 */
class ItemCustomLayout extends FrameLayout {
    private boolean mIsFirstTimeRefresh = true;
    private ImageView mBGImage;
    private Drawable mDrawable;

    public ItemCustomLayout(Context context) {
        super(context);
        mBGImage = new ImageView(context);
        addView(mBGImage, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void addCustomView(View customView) {
        addView(customView, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public View getCustomView() {
        return getChildAt(1);
    }

    public View getRealView() {
        return this;
    }

    public void hideBackground() {
        mBGImage.setVisibility(GONE);
    }

    public void showBackground() {
        mBGImage.setVisibility(VISIBLE);
    }

    public void saveBackground(Drawable drawable) {
        mDrawable = drawable;
        mBGImage.setImageDrawable(mDrawable);
        showBackground();
    }

    public boolean isBackgroundShowing() {
        return mDrawable == mBGImage.getDrawable();
    }

    public void refreshBackground() {
        //// FIXME: 15/12/8 第一次进行deleteItem的时候总是会有bug
        if (mIsFirstTimeRefresh) {
            hideBackground();
            showBackground();
            mIsFirstTimeRefresh = false;
        }
    }
}
