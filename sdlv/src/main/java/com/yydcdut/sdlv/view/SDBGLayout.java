package com.yydcdut.sdlv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yuyidong on 15/9/24.
 */
public class SDBGLayout extends ViewGroup {
    private static final String TAG_ONE = "one";
    private static final String TAG_TWO = "two";
    private static final String TAG_THREE = "three";
    private int mHeight;
    private int mBtnWidth;

    public SDBGLayout(Context context) {
        this(context, null);
    }

    public SDBGLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SDBGLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ImageView bgImage = new ImageView(context);
        bgImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        bgImage.setBackgroundColor(0xffff0000);
        addView(bgImage, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        TextView leftView = new TextView(context);
        leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        leftView.setBackgroundColor(0xff00ff00);
        leftView.setTag(TAG_ONE);
        leftView.setText("11111");
        addView(leftView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 100));

        TextView rightView = new TextView(context);
        rightView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        rightView.setBackgroundColor(0xff0000ff);
        rightView.setTag(TAG_TWO);
        rightView.setText("22222");
        addView(rightView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 100));


    }

    public void setLayoutHeight(int height, int btnWidth) {
        mHeight = height;
        mBtnWidth = btnWidth;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeight > 0 && mBtnWidth > 0) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mHeight);
            int total = getChildCount();
            for (int i = 0; i < total; i++) {
                View view = getChildAt(i);
                if (view instanceof ImageView) {
                    measureChild(view, widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
                } else {
                    measureChild(view, MeasureSpec.makeMeasureSpec(mBtnWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int total = getChildCount();
        for (int i = 0; i < total; i++) {
            View view = getChildAt(i);
            if (view instanceof ImageView) {
                view.layout(l, t, r, b);
            } else {
                String tag = (String) view.getTag();
                switch (tag) {
                    case TAG_ONE:
                        view.layout(l, t, l + mBtnWidth, b);
                        break;
                    case TAG_TWO:
                        view.layout(l + mBtnWidth, t, l + mBtnWidth * 2, b);
                        break;
                    case TAG_THREE:
                        break;
                }
            }
        }
    }
}
