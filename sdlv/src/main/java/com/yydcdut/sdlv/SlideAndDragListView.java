package com.yydcdut.sdlv;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import java.util.List;

/**
 * Created by yuyidong on 15/8/1.
 */
public class SlideAndDragListView<T> extends ListView implements Handler.Callback, View.OnDragListener,
        SDAdapter.OnButtonClickListener, AdapterView.OnItemClickListener {
    private static final int ITEM_BTN_NUMBER_MAX = 2;
    /* Handler 的 Message 信息 */
    private static final int MSG_WHAT_LONG_CLICK = 1;
    /* 时间 */
    private static final long DELAY_TIME = 1000;//1s
    private static final int SCROLL_TIME = 500;//500ms
    private static final int SCROLL_QUICK_TIME = 200;//200ms
    /* onTouch里面的状态 */
    private static final int STATE_NOTHING = -1;//抬起状态
    private static final int STATE_DOWN = 0;//按下状态
    private static final int STATE_LONG_CLICK = 1;//长点击状态
    private static final int STATE_SCROLL = 2;//SCROLL状态
    private static final int STATE_LONG_CLICK_FINISH = 3;//长点击已经触发完成
    private int mState = STATE_NOTHING;

    /* Scroller 滑动的 */
    private Scroller mScroller;
    /* 振动 */
    private Vibrator mVibrator;
    /* handler */
    private Handler mHandler;
    /* 滑动的目标对象 */
    private View mSlideTargetView;
    /* 要滑动的目标对象位置 */
    private int mSlideTargetPosition;
    private int mLastPosition;
    /* 手指放下的坐标 */
    private int mXDown;
    private int mYDown;
    /* X方向滑动了多少 */
    private int mXScrollDistance;
    /* 监听器 */
    private OnListItemLongClickListener mOnListItemLongClickListener;
    private OnListItemClickListener mOnListItemClickListener;
    private OnListItemClickListener mTempListItemClickListener;

    /* 那两个button的长度 */
    private int mBGWidth;
    /* 判断drag往上还是往下 */
    private boolean mUp = false;
    /* 当前drag所在ListView中的位置 */
    private int mCurrentPosition;
    /* 之前drag所在ListView中的位置 */
    private int mBeforeCurrentPosition;
    /* 之前之前drag所在ListView中的位置 */
    private int mBeforeBeforePosition;
    /* 适配器 */
    private SDAdapter mSDAdapter;
    /* 监听器 */
    private OnDragListener mOnDragListener;
    /* 数据 */
    private List<T> mDataList;
    /* 滑动的监听器 */
    private OnSlideListener mOnSlideListener;
    /* 代理Adapter里面的button的click事件 */
    private OnButtonClickListenerProxy mOnButtonClickListenerProxy;
    /* Attrs */
    private float mItemHeight = 0;
    private float mItemHeightDefault = getContext().getResources().getDimension(R.dimen.slv_item_height);
    private float mItemBtnWidth = 0;
    private float mItemBtnWidthDefault = getContext().getResources().getDimension(R.dimen.slv_item_bg_btn_width);
    private int mItemBtnNumber = 0;
    private int mItemBtnNumberDefault = 2;
    private String mItemBtn1Text;
    private String mItemBtn2Text;
    private Drawable mItemBGDrawable = null;

    public SlideAndDragListView(Context context) {
        this(context, null);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //-------------------------- attrs --------------------------
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.sdlv, defStyleAttr, 0);
        mItemHeight = a.getDimension(R.styleable.sdlv_item_height, mItemHeightDefault);
        mItemBtnWidth = a.getDimension(R.styleable.sdlv_item_btn_width, mItemBtnWidthDefault);
        mItemBtnNumber = a.getInt(R.styleable.sdlv_item_btn_number, mItemBtnNumberDefault);
        if (mItemBtnNumber > ITEM_BTN_NUMBER_MAX || mItemBtnNumber < 0) {
            throw new IllegalArgumentException("The number of Item buttons should be in between 0 and 2 !");
        }
        mItemBtn1Text = a.getString(R.styleable.sdlv_item_btn1_text);
        mItemBtn2Text = a.getString(R.styleable.sdlv_item_btn2_text);
        if (!TextUtils.isEmpty(mItemBtn2Text) && TextUtils.isEmpty(mItemBtn1Text)) {
            throw new IllegalArgumentException("先1后2");
        }
        mItemBGDrawable = a.getDrawable(R.styleable.sdlv_item_background);

        a.recycle();
        //-------------------------- attrs --------------------------
        mBGWidth = (int) (mItemBtnWidth * mItemBtnNumber);
        mScroller = new Scroller(getContext());
        mVibrator = (Vibrator)
                getContext().getSystemService(Context.VIBRATOR_SERVICE);

        mHandler = new Handler(this);
        setOnDragListener(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_LONG_CLICK:
                if (mState == STATE_LONG_CLICK) {//如果得到msg的时候state状态是Long Click的话
                    //改为long click触发完成
                    mState = STATE_LONG_CLICK_FINISH;
                    //得到长点击的位置
                    int position = msg.arg1;
                    //找到那个位置的view
                    View view = getChildAt(mSlideTargetPosition - getFirstVisiblePosition());
                    //通知adapter
                    mSDAdapter.setDragPosition(position);
                    //如果设置了监听器的话，就触发
                    if (mOnListItemLongClickListener != null) {
                        scrollBack();
                        mVibrator.vibrate(100);
                        mOnListItemLongClickListener.onListItemLongClick(view, position);
                    }
                    mCurrentPosition = position;
                    mBeforeCurrentPosition = position;
                    mBeforeBeforePosition = position;
                    //把背景给弄透明，这样drag的时候要好看些
                    view.findViewById(R.id.layout_item_bg).setVisibility(INVISIBLE);
                    view.findViewById(R.id.img_item_scroll_bg).setVisibility(INVISIBLE);
                    //drag
                    ClipData.Item item = new ClipData.Item("1");
                    ClipData data = new ClipData("1", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                    view.startDrag(data, new View.DragShadowBuilder(view), null, 0);
                    //通知adapter变颜色
                    mSDAdapter.notifyDataSetChanged();
                }
                break;
        }
        return true;
    }

    private boolean mIsScrolling = false;

    @Override
    public void computeScroll() {
        //滑动到指定位置
        if (mScroller.computeScrollOffset()) {
            mIsScrolling = true;
            if (mSlideTargetView != null) {
                mSlideTargetView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                postInvalidate();
                if (mScroller.isFinished()) {
                    mSlideTargetView = null;
                }
            }
        } else {
            mIsScrolling = false;
        }
        super.computeScroll();
    }

    /**
     * 设置滑动监听器
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
         * @param position
         */
        void onSlideOpen(View view, int position);

        /**
         * 当滑动归位的时候触发
         *
         * @param view
         * @param position
         */
        void onSlideClose(View view, int position);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsScrolling) {//scroll正在滑动的话就不要做其他处理了
                    return super.onTouchEvent(ev);
                }
                //获取出坐标来
                mXDown = (int) ev.getX();
                mYDown = (int) ev.getY();

                //通过坐标找到在ListView中的位置
                mSlideTargetPosition = pointToPosition(mXDown, mYDown);
                if (mSlideTargetPosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(ev);
                }

                //通过位置找到要slide的view
                View view = getChildAt(mSlideTargetPosition - getFirstVisiblePosition());
                if (view == null) {
                    return super.dispatchTouchEvent(ev);
                }
                mSlideTargetView = view.findViewById(R.id.layout_item_scroll);
                if (mSlideTargetView != null) {
                    //如果已经是滑开了的或者没有滑开的
                    mXScrollDistance = mSlideTargetView.getScrollX();
                } else {
                    mXScrollDistance = 0;
                }
                //当前state状态味按下
                mState = STATE_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsScrolling) {//scroll正在滑动的话就不要做其他处理了
                    return super.dispatchTouchEvent(ev);
                }
                if (fingerNotMove(ev)) {//手指的范围在50以内
                    if (mState != STATE_SCROLL && mState != STATE_LONG_CLICK_FINISH) {//状态不为滑动状态且不为已经触发完成
                        sendLongClickMessage();
                        mState = STATE_LONG_CLICK;
                    } else if (mState == STATE_SCROLL) {//当为滑动状态的时候
                        //有滑动，那么不再触发长点击
                        removeLongClickMessage();
                    }
                } else if (fingerLeftAndRightMove(ev) && mSlideTargetView != null) {//上下范围在50，主要检测左右滑动
                    boolean bool = false;
                    //这次位置与上一次的不一样，那么要滑这个之前把之前的归位
                    if (mLastPosition != mSlideTargetPosition) {
                        mLastPosition = mSlideTargetPosition;
                        bool = scrollBack();
                    }
                    //如果有scroll归位的话的话先跳过这次move
                    if (bool) {
                        return super.dispatchTouchEvent(ev);
                    }
                    //scroll当前的View
                    int moveDistance = (int) ev.getX() - mXDown;//这个往右是正，往左是负
                    int distance = mXScrollDistance - moveDistance < 0 ? mXScrollDistance - moveDistance : 0;
                    mSlideTargetView.scrollTo(distance, 0);
                    mState = STATE_SCROLL;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsScrolling) {//scroll正在滑动的话就不要做其他处理了
                    return true;
                }
                if (mSlideTargetView != null && mState == STATE_SCROLL) {
                    //如果滑出的话，那么就滑到固定位置(只要滑出了 mBGWidth / 2 ，就算滑出去了)
                    if (Math.abs(mSlideTargetView.getScrollX()) > mBGWidth / 2) {
                        //通知adapter
                        mSDAdapter.setBtnPosition(mSlideTargetPosition);
                        //不触发onListItemClick事件
                        mOnListItemClickListener = null;
                        mSDAdapter.setSlideOpenItemPosition(mSlideTargetPosition);
                        if (mOnSlideListener != null) {
                            mOnSlideListener.onSlideOpen(mSlideTargetView, mSlideTargetPosition);
                        }
                        //滑出
                        int delta = mBGWidth - Math.abs(mSlideTargetView.getScrollX());
                        if (Math.abs(mSlideTargetView.getScrollX()) < mBGWidth) {
                            mScroller.startScroll(mSlideTargetView.getScrollX(), 0, -delta, 0, SCROLL_QUICK_TIME);
                        } else {
                            mScroller.startScroll(mSlideTargetView.getScrollX(), 0, -delta, 0, SCROLL_TIME);
                        }
                        postInvalidate();
                    } else {
                        //通知adapter
                        mSDAdapter.setBtnPosition(-1);
                        mSDAdapter.setSlideOpenItemPosition(-1);
                        //如果有onListItemClick事件的话，就赋值过去，代表可以触发了
                        if (mTempListItemClickListener != null && mOnListItemClickListener == null) {
                            mOnListItemClickListener = mTempListItemClickListener;
                        }
                        //滑回去,归位
                        if (mOnSlideListener != null) {
                            mOnSlideListener.onSlideClose(mSlideTargetView, mSlideTargetPosition);
                        }
                        mScroller.startScroll(mSlideTargetView.getScrollX(), 0, -mSlideTargetView.getScrollX(), 0, SCROLL_QUICK_TIME);
                        postInvalidate();
                    }
                    mState = STATE_NOTHING;
                    removeLongClickMessage();
                    //更新last的值
                    mLastPosition = mSlideTargetPosition;
                    //设置为无效的
                    mSlideTargetPosition = AdapterView.INVALID_POSITION;
                    return false;
                }
                mState = STATE_NOTHING;
                removeLongClickMessage();
                //更新last的值
                mLastPosition = mSlideTargetPosition;
                //设置为无效的
                mSlideTargetPosition = AdapterView.INVALID_POSITION;
                break;
            default:
                removeLongClickMessage();
                mState = STATE_NOTHING;
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

    /**
     * remove掉message
     */
    private void removeLongClickMessage() {
        if (mHandler.hasMessages(MSG_WHAT_LONG_CLICK)) {
            mHandler.removeMessages(MSG_WHAT_LONG_CLICK);
        }
    }

    /**
     * sendMessage
     */
    private void sendLongClickMessage() {
        if (!mHandler.hasMessages(MSG_WHAT_LONG_CLICK)) {
            Message message = new Message();
            message.what = MSG_WHAT_LONG_CLICK;
            message.arg1 = mSlideTargetPosition;
            mHandler.sendMessageDelayed(message, DELAY_TIME);
        }
    }

    /**
     * 展开的都scroll归位
     *
     * @return
     */
    private boolean scrollBack() {
        boolean bool = false;
        //计算当前ListView上有多少个item
        int total = getLastVisiblePosition() - getFirstVisiblePosition();
        for (int i = 0; i < total; i++) {
            View backLayoutView = getChildAt(i);
            View backView = backLayoutView.findViewById(R.id.layout_item_scroll);
            //判断当前这个view有没有scroll过
            if (backView.getScrollX() == 0) {
                continue;
            } else {//这里scroll回去不要动画也挺连贯了
                //如果scroll过的话就scroll到0,0
                backView.scrollTo(0, 0);
                //通知adapter
                mSDAdapter.setBtnPosition(-1);
                mSDAdapter.setSlideOpenItemPosition(-1);
                //如果有onListItemClick事件的话，就赋值过去，代表可以触发了
                if (mTempListItemClickListener != null && mOnListItemClickListener == null) {
                    mOnListItemClickListener = mTempListItemClickListener;
                }
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlideClose(backView, i);
                }
                bool = true;
            }
        }
        return bool;
    }

    @Deprecated
    @Override
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        //do nothing
        //这个系统的方法禁用了
    }

    /**
     * 自己写的长点击事件
     */
    public interface OnListItemLongClickListener {
        void onListItemLongClick(View view, int position);
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnListItemLongClickListener(OnListItemLongClickListener listener) {
        mOnListItemLongClickListener = listener;
    }

    @Deprecated
    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        //do nothing
        //这个系统的方法禁用了
    }

    /**
     * 自己的单击事件
     */
    public interface OnListItemClickListener {
        void onListItemClick(View v, int position);
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnListItemClickListener(OnListItemClickListener listener) {
        if (listener != null) {
            mTempListItemClickListener = listener;
            mOnListItemClickListener = listener;
            super.setOnItemClickListener(this);
        } else {
            mOnListItemClickListener = null;
            mTempListItemClickListener = null;
            super.setOnItemClickListener(null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        scrollBack();
        if (mOnListItemClickListener != null) {
            mOnListItemClickListener.onListItemClick(view, position);
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                //当前移动的item在ListView中的position
                int position = pointToPosition((int) event.getX(), (int) event.getY());
                //如果位置发生了改变
                if (mBeforeCurrentPosition != position) {
                    //有时候得到的position是-1(AdapterView.INVALID_POSITION)，忽略掉
                    if (position >= 0) {
                        //判断是往上了还是往下了
                        mUp = position - mBeforeCurrentPosition > 0 ? false : true;
                        //记录移动之后上一次的位置
                        mBeforeBeforePosition = mBeforeCurrentPosition;
                        //记录当前位置
                        mBeforeCurrentPosition = position;
                    }
                }
                moveListViewUpOrDown(position);
                //有时候为-1(AdapterView.INVALID_POSITION)的情况，忽略掉
                if (position >= 0) {
                    //判断是不是已经换过位置了，如果没有换过，则进去换
                    if (position != mCurrentPosition) {
                        if (mUp) {//往上
                            //只是移动了一格
                            if (position - mBeforeBeforePosition == -1) {
                                T t = mDataList.get(position);
                                mDataList.set(position, mDataList.get(position + 1));
                                mDataList.set(position + 1, t);
                            } else {//一下子移动了好几个位置，其实可以和上面那个方法合并起来的
                                T t = mDataList.get(mBeforeBeforePosition);
                                for (int i = mBeforeBeforePosition; i > position; i--) {
                                    mDataList.set(i, mDataList.get(i - 1));
                                }
                                mDataList.set(position, t);
                            }
                        } else {
                            if (position - mBeforeBeforePosition == 1) {
                                T t = mDataList.get(position);
                                mDataList.set(position, mDataList.get(position - 1));
                                mDataList.set(position - 1, t);
                            } else {
                                T t = mDataList.get(mBeforeBeforePosition);
                                for (int i = mBeforeBeforePosition; i < position; i++) {
                                    mDataList.set(i, mDataList.get(i + 1));
                                }
                                mDataList.set(position, t);
                            }
                        }
                        mSDAdapter.notifyDataSetChanged();
                        //更新位置
                        mCurrentPosition = position;
                    }
                }
                //通知adapter
                mSDAdapter.setDragPosition(position);
                if (mOnDragListener != null) {
                    mOnDragListener.onDragViewMoving(mCurrentPosition);
                }
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                return true;
            case DragEvent.ACTION_DROP:
                mSDAdapter.notifyDataSetChanged();
                //通知adapter
                mSDAdapter.setDragPosition(-1);
                if (mOnDragListener != null) {
                    mOnDragListener.onDragViewDown(mCurrentPosition);
                }
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                return true;
            default:
                break;
        }
        return false;
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mSDAdapter = (SDAdapter) adapter;
        mSDAdapter.setOnButtonClickListener(this);
        mSDAdapter.setItemHeight(mItemHeight);
        mSDAdapter.setItemBtnNumber(mItemBtnNumber, mItemBtn1Text, mItemBtn2Text);
        mSDAdapter.setItemBtnWidth(mItemBtnWidth);
        mSDAdapter.setItemBGDrawable(mItemBGDrawable);
        mDataList = mSDAdapter.getDataList();
    }

    /**
     * 如果到了两端，判断ListView是往上滑动还是ListView往下滑动
     *
     * @param position
     */
    private void moveListViewUpOrDown(int position) {
        //ListView中最上面的显示的位置
        int firstPosition = getFirstVisiblePosition();
        //ListView中最下面的显示的位置
        int lastPosition = getLastVisiblePosition();
        //能够往上的话往上
        if ((position == firstPosition || position == firstPosition + 1) && firstPosition != 0) {
            smoothScrollToPosition(firstPosition - 1);
        }
        //能够往下的话往下
        if ((position == lastPosition || position == lastPosition - 1) && lastPosition != getCount() - 1) {
            smoothScrollToPosition(lastPosition + 1);
        }
    }

    /**
     * 当发生drag的时候触发的监听器
     */
    public interface OnDragListener {
        /**
         * drag的正在移动
         *
         * @param position
         */
        void onDragViewMoving(int position);

        /**
         * drag的放下了
         *
         * @param position
         */
        void onDragViewDown(int position);
    }

    /**
     * 设置drag的监听器
     *
     * @param listener
     */
    public void setOnDragListener(OnDragListener listener) {
        mOnDragListener = listener;
    }

    @Override
    public void onClick(View v, int position, int number) {
        if (mOnButtonClickListenerProxy != null) {
            mOnButtonClickListenerProxy.onClick(v, position, number);
        }
    }

    /**
     * 将adapter里面的button的时间代理出去
     * 为啥这里是代理出去呢，我想的是实现adapter的onClick方法不太符合逻辑，所以在这里实现，然后代理出去
     */
    public interface OnButtonClickListenerProxy {
        void onClick(View v, int position, int number);

    }

    /**
     * 设置代理的监听器
     *
     * @param proxy
     */
    public void setOnButtonClickListenerProxy(OnButtonClickListenerProxy proxy) {
        mOnButtonClickListenerProxy = proxy;
    }

}
