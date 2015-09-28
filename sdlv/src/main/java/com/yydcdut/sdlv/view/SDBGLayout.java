package com.yydcdut.sdlv.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yuyidong on 15/9/24.
 */
public class SDBGLayout extends ViewGroup {
    private static final String TAG_ONE = "one";
    private static final String TAG_TWO = "two";
    private static final String TAG_THREE = "three";
    private int mBtnWidth;

    private TextView mLeftView;
    private TextView mMiddleView;
    private TextView mRightView;
    private ImageView mBGImage;

    public SDBGLayout(Context context) {
        this(context, null);
    }

    public SDBGLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SDBGLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBGImage = new ImageView(context);
        mBGImage.setBackgroundColor(Color.TRANSPARENT);
        addView(mBGImage, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mLeftView = new TextView(context);
        mLeftView.setBackgroundColor(Color.RED);
        mLeftView.setTag(TAG_ONE);
        mLeftView.setText(TAG_ONE);
        addView(mLeftView, new LayoutParams(LayoutParams.MATCH_PARENT, 100));

        mMiddleView = new TextView(context);
        mMiddleView.setBackgroundColor(Color.GREEN);
        mMiddleView.setTag(TAG_TWO);
        mMiddleView.setText(TAG_TWO);
        addView(mMiddleView, new LayoutParams(LayoutParams.MATCH_PARENT, 100));

        mRightView = new TextView(context);
        mRightView.setBackgroundColor(Color.BLUE);
        mRightView.setTag(TAG_THREE);
        mRightView.setText(TAG_THREE);
        addView(mRightView, new LayoutParams(LayoutParams.MATCH_PARENT, 100));
    }

    public void setBtnWidth(int btnWidth) {
        mBtnWidth = btnWidth;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mBtnWidth > 0) {
            int total = getChildCount();
            for (int i = 0; i < total; i++) {
                View view = getChildAt(i);
                if (view instanceof ImageView) {
                    measureChild(view, widthMeasureSpec, heightMeasureSpec);
                } else {
                    measureChild(view, MeasureSpec.makeMeasureSpec(mBtnWidth, MeasureSpec.EXACTLY),
                            heightMeasureSpec);
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
                        view.layout(l + mBtnWidth * 2, t, l + mBtnWidth * 3, b);
                        break;
                }
            }
        }
    }

    public TextView getLeftView() {
        return mLeftView;
    }

    public TextView getMiddleView() {
        return mMiddleView;
    }

    public TextView getRightView() {
        return mRightView;
    }

    public ImageView getBackGroundImage() {
        return mBGImage;
    }
}
