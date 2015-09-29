package com.yydcdut.sdlv;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.yydcdut.sdlv.utils.AttrsHolder;
import com.yydcdut.sdlv.utils.OnAdapterButtonClickListenerProxy;
import com.yydcdut.sdlv.utils.OnAdapterSlideListenerProxy;

/**
 * Created by yuyidong on 15/9/28.
 */
public class SlideAndDragListView1 extends ListView implements OnAdapterSlideListenerProxy, OnAdapterButtonClickListenerProxy {
    /* item的btn的最大个数 */
    private static final int ITEM_BTN_NUMBER_MAX = 2;
    /* onTouch里面的状态 */
    private static final int STATE_NOTHING = -1;//抬起状态
    private static final int STATE_DOWN = 0;//按下状态
    private static final int STATE_LONG_CLICK = 1;//长点击状态
    private static final int STATE_SCROLL = 2;//SCROLL状态
    private static final int STATE_LONG_CLICK_FINISH = 3;//长点击已经触发完成
    private int mState = STATE_NOTHING;
    /* 手指放下的坐标 */
    private int mXDown;
    private int mYDown;
    /* Attrs */
    private AttrsHolder mAttrsHolder;
    /* WrapperAdapter */
    private WrapperAdapter mWrapperAdapter;
    /* 监听器 */
    private OnSlideListener mOnSlideListener;
    private OnButtonClickListener mOnButtonClickListener;


    public SlideAndDragListView1(Context context) {
        this(context, null);
    }

    public SlideAndDragListView1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAndDragListView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //-------------------------- attrs --------------------------
        mAttrsHolder = new AttrsHolder();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.sdlv, defStyleAttr, 0);
        mAttrsHolder.itemHeight = a.getDimension(R.styleable.sdlv_item_height, getContext().getResources().getDimension(R.dimen.slv_item_height));
        mAttrsHolder.itemBackGroundDrawable = a.getDrawable(R.styleable.sdlv_item_background);
        mAttrsHolder.btnWidth = a.getDimension(R.styleable.sdlv_item_btn_width, getContext().getResources().getDimension(R.dimen.slv_item_bg_btn_width));
        mAttrsHolder.btnNumber = a.getInt(R.styleable.sdlv_item_btn_number, 2);
        if (mAttrsHolder.btnNumber > ITEM_BTN_NUMBER_MAX || mAttrsHolder.btnNumber < 0) {
            throw new IllegalArgumentException("The number of Item buttons should be in between 0 and 2 !");
        }
        mAttrsHolder.btn1Text = a.getString(R.styleable.sdlv_item_btn1_text);
        mAttrsHolder.btn2Text = a.getString(R.styleable.sdlv_item_btn2_text);
        if (!TextUtils.isEmpty(mAttrsHolder.btn2Text) && TextUtils.isEmpty(mAttrsHolder.btn1Text)) {
            throw new IllegalArgumentException("The \'item_btn2_text\' has value, but \'item_btn1_text\' dose not have value!");
        }
        mAttrsHolder.btn1Drawable = a.getDrawable(R.styleable.sdlv_item_btn1_background);
        mAttrsHolder.btn2Drawable = a.getDrawable(R.styleable.sdlv_item_btn2_background);
        mAttrsHolder.btnTextSize = a.getDimension(R.styleable.sdlv_item_btn_text_size, getContext().getResources().getDimension(R.dimen.txt_size));
        mAttrsHolder.btnTextColor = a.getColor(R.styleable.sdlv_item_btn_text_color, getContext().getResources().getColor(android.R.color.white));
        a.recycle();
        //-------------------------- attrs --------------------------
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取出坐标来
                mXDown = (int) ev.getX();
                mYDown = (int) ev.getY();
                //当前state状态为按下
                mState = STATE_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (fingerNotMove(ev)) {//手指的范围在50以内
                } else if (fingerLeftAndRightMove(ev)) {//上下范围在50，主要检测左右滑动
                    int position = pointToPosition(mXDown, mYDown);
                    if (position != AdapterView.INVALID_POSITION) {
                        mWrapperAdapter.setSlideItemPosition(position);
                    }
                    return super.dispatchTouchEvent(ev);
                } else {
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 上下左右不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerNotMove(MotionEvent ev) {
        return (mXDown - ev.getX() < 25 && mXDown - ev.getX() > -25 &&
                mYDown - ev.getY() < 25 && mYDown - ev.getY() > -25);
    }

    /**
     * 左右得超出50，上下不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerLeftAndRightMove(MotionEvent ev) {
        return ((ev.getX() - mXDown > 25 || ev.getX() - mXDown < -25) &&
                ev.getY() - mYDown < 25 && ev.getY() - mYDown > -25);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mWrapperAdapter = new WrapperAdapter(getContext(), this, adapter, mAttrsHolder);
        mWrapperAdapter.setOnAdapterSlideListenerProxy(this);
        mWrapperAdapter.setOnAdapterButtonClickListenerProxy(this);
        super.setAdapter(mWrapperAdapter);
    }

    /**
     * 设置item滑动监听器
     *
     * @param listener
     */
    public void setOnSlideListener(OnSlideListener listener) {
        mOnSlideListener = listener;
    }

    /**
     * item的滑动的监听器
     */
    public interface OnSlideListener {
        /**
         * 当滑动开的时候触发
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideOpen(View view, View parentView, int position);

        /**
         * 当滑动归位的时候触发
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideClose(View view, View parentView, int position);
    }

    @Override
    public void onSlideOpen(View view, int position) {
        if (mOnSlideListener != null) {
            mOnSlideListener.onSlideOpen(view, this, position);
        }
    }

    @Override
    public void onSlideClose(View view, int position) {
        if (mOnSlideListener != null) {
            mOnSlideListener.onSlideClose(view, this, position);
        }
    }

    /**
     * 设置item中的button点击事件的监听器
     *
     * @param onButtonClickListener
     */
    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        mOnButtonClickListener = onButtonClickListener;
    }

    /**
     * item中的button监听器
     */
    public interface OnButtonClickListener {
        /**
         * 点击事件
         *
         * @param v
         * @param itemPosition   第几个item
         * @param buttonPosition 第几个button
         */
        void onClick(View v, int itemPosition, int buttonPosition);
    }

    @Override
    public void onClick(View v, int itemPosition, int buttonPosition) {
        if (mOnButtonClickListener != null) {
            mOnButtonClickListener.onClick(v, itemPosition, buttonPosition);
        }
    }
}
