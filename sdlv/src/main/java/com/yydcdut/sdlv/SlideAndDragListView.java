package com.yydcdut.sdlv;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuyidong on 15/9/28.
 */
public class SlideAndDragListView<T> extends DragListView<T> implements WrapperAdapter.OnAdapterSlideListenerProxy,
        WrapperAdapter.OnAdapterMenuClickListenerProxy, Handler.Callback {
    /* Handler 的 Message 信息 */
    private static final int MSG_WHAT_LONG_CLICK = 1;
    /* Handler 发送message需要延迟的时间 */
    private static final long CLICK_LONG_TRIGGER_TIME = 1000;//1s
    /* onTouch里面的状态 */
    private static final int STATE_NOTHING = -1;//抬起状态
    private static final int STATE_DOWN = 0;//按下状态
    private static final int STATE_LONG_CLICK = 1;//长点击状态
    private static final int STATE_SCROLL = 2;//SCROLL状态
    private static final int STATE_LONG_CLICK_FINISH = 3;//长点击已经触发完成
    private static final int STATE_MORE_FINGERS = 4;//多个手指
    private int mState = STATE_NOTHING;

    private static final int RETURN_SCROLL_BACK_OWN = 1;//自己有归位操作
    private static final int RETURN_SCROLL_BACK_OTHER = 2;//其他位置有归位操作
    private static final int RETURN_SCROLL_BACK_CLICK_MENU_BUTTON = 3;//点击到了滑开的item的menuButton上
    private static final int RETURN_SCROLL_BACK_NOTHING = 0;//所以位置都没有回归操作

    /* 振动 */
    private Vibrator mVibrator;
    /* handler */
    private Handler mHandler;
    /* 是否要触发itemClick */
    private boolean mIsWannaTriggerClick = true;
    /* 手指放下的坐标 */
    private int mXDown;
    private int mYDown;
    /* Menu */
    private Map<Integer, Menu> mMenuMap;
    /* WrapperAdapter */
    private WrapperAdapter mWrapperAdapter;
    /* 手指滑动的最短距离 */
    private int mShortestDistance = 25;

    /* 监听器 */
    private OnSlideListener mOnSlideListener;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnListItemLongClickListener mOnListItemLongClickListener;
    private OnListItemClickListener mOnListItemClickListener;
    private OnItemDeleteListener mOnItemDeleteListener;
    private OnListScrollListener mOnListScrollListener;

    public SlideAndDragListView(Context context) {
        this(context, null);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        mHandler = new Handler(this);
        mShortestDistance = ViewConfiguration.get(context).getScaledTouchSlop();
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
                    View view = getChildAt(position - getFirstVisiblePosition());
                    //如果设置了监听器的话，就触发
                    if (mOnListItemLongClickListener != null) {
                        mVibrator.vibrate(100);
                        mOnListItemLongClickListener.onListItemLongClick(view, position);
                    }
                    boolean canDrag = scrollBackByDrag(position);
                    if (canDrag && view instanceof ItemMainLayout) {
                        setDragPosition(position);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //当Menu滑开，然后在Menu的位置滑动，是不会经过onTouchEvent的ACTION_DOWN的
                //获取出坐标来
                mXDown = (int) ev.getX();
                mYDown = (int) ev.getY();
                //当前state状态为按下
                mState = STATE_DOWN;
                ItemMainLayout itemMainLayoutDown = getItemMainLayoutByPosition((int) ev.getX(), (int) ev.getY());
                if (itemMainLayoutDown != null) {
                    mItemLeftDistance = itemMainLayoutDown.getItemCustomView().getLeft();
                } else {
                    mItemLeftDistance = 0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (fingerLeftAndRightMove(ev)) {//上下范围在50，主要检测左右滑动
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private int mItemLeftDistance = 0;
    private boolean isItemViewHandlingMotionEvent = false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //获取出坐标来
                mXDown = (int) ev.getX();
                mYDown = (int) ev.getY();
                //当前state状态为按下
                mState = STATE_DOWN;
                //得到当前Item滑动了多少
                ItemMainLayout itemMainLayoutDown = getItemMainLayoutByPosition(mXDown, mYDown);
                if (itemMainLayoutDown != null) {
                    mItemLeftDistance = itemMainLayoutDown.getItemCustomView().getLeft();
                } else {
                    mItemLeftDistance = 0;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                removeLongClickMessage();
                mState = STATE_MORE_FINGERS;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (fingerNotMove(ev) && mState != STATE_SCROLL) {//手指的范围在50以内
                    if (mItemLeftDistance == 0) {
                        sendLongClickMessage(pointToPosition(mXDown, mYDown));
                        mState = STATE_LONG_CLICK;
                    }
                } else if (fingerLeftAndRightMove(ev) && !isItemViewHandlingMotionEvent) {//上下范围在50，主要检测左右滑动
                    removeLongClickMessage();
                    //将当前想要滑动哪一个传递给wrapperAdapter
                    int position = pointToPosition(mXDown, mYDown);
                    ItemMainLayout itemMainLayout = getItemMainLayoutByPosition(mXDown, mYDown);
                    if (itemMainLayout != null) {
                        //判断是不是点在menu上面了
                        Log.i("yuyidong111", "ev.getX()--->" + ev.getX() + "   mItemLeftDistance--->" + mItemLeftDistance);
                        if (mItemLeftDistance > 0) { //已经向右滑动了，而且滑开了
                            if (ev.getX() < mItemLeftDistance) {//手指的位置再Menu
                                Log.i("yuyidong111", "1111111");
                                return true;
                            }
                        } else if (mItemLeftDistance < 0) {//已经向左滑动了，而且滑开了
                            if (ev.getX() > mItemLeftDistance + itemMainLayout.getItemCustomView().getWidth()) {
                                Log.i("yuyidong111", "222222222");
                                return true;
                            }
                        }

                        //没有点在menu上面
                        if (isFingerMoving2Right(ev)) {//如果想向右滑动
                            if (itemMainLayout.getItemLeftBackGroundLayout().getBtnViews().size() == 0 &&
                                    itemMainLayout.getScrollState() == ItemMainLayout.SCROLL_STATE_CLOSE) {//但是又没有Left的Menu
                                mState = STATE_NOTHING;
                                return true;
                            }
                        } else if (isFingerMoving2Left(ev)) {//如果想向左滑动
                            if (itemMainLayout.getItemRightBackGroundLayout().getBtnViews().size() == 0 &&
                                    itemMainLayout.getScrollState() == ItemMainLayout.SCROLL_STATE_CLOSE) {//但是又没有Right的Menu
                                mState = STATE_NOTHING;
                                return true;
                            }
                        }
                        mWrapperAdapter.setSlideItemPosition(position);
                        isItemViewHandlingMotionEvent = true;
                        mState = STATE_SCROLL;
                        itemMainLayout.handleMotionEvent(ev, mXDown, mYDown, mItemLeftDistance);
                        return true;
                    } else {
                        mState = STATE_NOTHING;
                        return true;
                    }
                } else {
                    if (isItemViewHandlingMotionEvent) {
                        ItemMainLayout itemMainLayout = getItemMainLayoutByPosition(mXDown, mYDown);
                        if (itemMainLayout != null) {
                            itemMainLayout.handleMotionEvent(ev, mXDown, mYDown, mItemLeftDistance);
                            return true;
                        }
                    } else {
                        removeLongClickMessage();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                int position = pointToPosition(mXDown, mYDown);
                if (position != AdapterView.INVALID_POSITION) {
                    if (mState == STATE_DOWN || mState == STATE_LONG_CLICK) {
                        //是否ScrollBack了，是的话就不去执行onListItemClick操作了
                        int scrollBackState = scrollBack(position, ev.getX());
                        if (scrollBackState == RETURN_SCROLL_BACK_NOTHING) {
                            if (mOnListItemClickListener != null && mIsWannaTriggerClick) {
                                View v = getChildAt(position - getFirstVisiblePosition());
                                mOnListItemClickListener.onListItemClick(v, position);
                            }
                        }
                    } else {
                        ItemMainLayout itemMainLayout = getItemMainLayoutByPosition(mXDown, mYDown);
                        if (itemMainLayout != null) {
                            itemMainLayout.handleMotionEvent(ev, mXDown, mYDown, -1);
                        }
                    }
                }
                removeLongClickMessage();
                mState = STATE_NOTHING;
                mItemLeftDistance = 0;
                isItemViewHandlingMotionEvent = false;
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                mState = STATE_NOTHING;
                mItemLeftDistance = 0;
                isItemViewHandlingMotionEvent = false;
                break;
            default:
                break;

        }
        return super.onTouchEvent(ev);
    }

    /**
     * 将滑开的item归位
     *
     * @param position
     * @param x        坐标
     * @return
     */
    private int scrollBack(int position, float x) {
        //是不是当前滑开的这个
        if (mWrapperAdapter.getSlideItemPosition() == position) {
            int scrollBackSituation = mWrapperAdapter.returnSlideItemPosition(x);
            switch (scrollBackSituation) {
                case ItemMainLayout.SCROLL_BACK_CLICK_OWN:
                    return RETURN_SCROLL_BACK_OWN;
                case ItemMainLayout.SCROLL_BACK_ALREADY_CLOSED:
                    return RETURN_SCROLL_BACK_NOTHING;
                case ItemMainLayout.SCROLL_BACK_CLICK_MENU_BUTTON:
                    return RETURN_SCROLL_BACK_CLICK_MENU_BUTTON;
            }
        } else if (mWrapperAdapter.getSlideItemPosition() != -1) {
            mWrapperAdapter.returnSlideItemPosition();
            return RETURN_SCROLL_BACK_OTHER;
        }
        return RETURN_SCROLL_BACK_NOTHING;
    }

    /**
     * 用于drag的ScrollBack逻辑操作
     *
     * @param position
     * @return true--->可以drag false--->不能drag
     */
    private boolean scrollBackByDrag(int position) {
        //是不是当前滑开的这个
        if (mWrapperAdapter.getSlideItemPosition() == position) {
            return false;
        } else if (mWrapperAdapter.getSlideItemPosition() != -1) {
            mWrapperAdapter.returnSlideItemPosition();
            return true;
        }
        return true;
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
    private void sendLongClickMessage(int position) {
        if (!mHandler.hasMessages(MSG_WHAT_LONG_CLICK)) {
            Message message = new Message();
            message.what = MSG_WHAT_LONG_CLICK;
            message.arg1 = position;
            mHandler.sendMessageDelayed(message, CLICK_LONG_TRIGGER_TIME);
        }
    }

    /**
     * 上下左右不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerNotMove(MotionEvent ev) {
        return (mXDown - ev.getX() < mShortestDistance && mXDown - ev.getX() > -mShortestDistance &&
                mYDown - ev.getY() < mShortestDistance && mYDown - ev.getY() > -mShortestDistance);
    }

    /**
     * 左右得超出50，上下不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerLeftAndRightMove(MotionEvent ev) {
        return ((ev.getX() - mXDown > mShortestDistance || ev.getX() - mXDown < -mShortestDistance) &&
                ev.getY() - mYDown < mShortestDistance && ev.getY() - mYDown > -mShortestDistance);
    }

    /**
     * 是不是向右滑动
     *
     * @return
     */
    private boolean isFingerMoving2Right(MotionEvent ev) {
        return (ev.getX() - mXDown > mShortestDistance);
    }

    /**
     * 是不是向左滑动
     *
     * @return
     */
    private boolean isFingerMoving2Left(MotionEvent ev) {
        return (ev.getX() - mXDown < -mShortestDistance);
    }

    private ItemMainLayout getItemMainLayoutByPosition(int x, int y) {
        int position = pointToPosition(x, y);
        if (position != AdapterView.INVALID_POSITION) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof ItemMainLayout) {
                ItemMainLayout itemMainLayout = (ItemMainLayout) view;
                return itemMainLayout;
            }
        }
        return null;
    }

    /**
     * 设置Menu
     *
     * @param menu
     */
    public void setMenu(Menu menu) {
        if (mMenuMap != null) {
            mMenuMap.clear();
        } else {
            mMenuMap = new HashMap<>(1);
        }
        mMenuMap.put(menu.getMenuViewType(), menu);
    }

    /**
     * 设置menu
     *
     * @param list
     */
    public void setMenu(List<Menu> list) {
        if (mMenuMap != null) {
            mMenuMap.clear();
        } else {
            mMenuMap = new HashMap<>(list.size());
        }
        for (Menu menu : list) {
            mMenuMap.put(menu.getMenuViewType(), menu);
        }
    }

    /**
     * 设置Menu
     *
     * @param menus
     */
    public void setMenu(Menu... menus) {
        if (mMenuMap != null) {
            mMenuMap.clear();
        } else {
            mMenuMap = new HashMap<>(menus.length);
        }
        for (Menu menu : menus) {
            mMenuMap.put(menu.getMenuViewType(), menu);
        }
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        if (mMenuMap == null || mMenuMap.size() == 0) {
            throw new IllegalArgumentException("先设置Menu");
        }
        mWrapperAdapter = new WrapperAdapter(getContext(), this, adapter, mMenuMap) {

            @Override
            public void onScrollStateChangedProxy(AbsListView view, int scrollState) {
                if (scrollState == WrapperAdapter.SCROLL_STATE_IDLE) {
                    mIsWannaTriggerClick = true;
                } else {
                    mIsWannaTriggerClick = false;
                }
                if (mOnListScrollListener != null) {
                    mOnListScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScrollProxy(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mOnListScrollListener != null) {
                    mOnListScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }

            @Override
            public void onItemDelete(View view, int position) {
                if (mOnItemDeleteListener != null) {
                    mOnItemDeleteListener.onItemDelete(view, position);
                }
            }

        };
        mWrapperAdapter.setOnAdapterSlideListenerProxy(this);
        mWrapperAdapter.setOnAdapterMenuClickListenerProxy(this);
        setRawAdapter(adapter);
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
        void onSlideOpen(View view, View parentView, int position, int direction);

        /**
         * 当滑动归位的时候触发
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideClose(View view, View parentView, int position, int direction);
    }

    @Override
    public void onSlideOpen(View view, int position, int direction) {
        if (mOnSlideListener != null) {
            mOnSlideListener.onSlideOpen(view, this, position, direction);
        }
    }

    @Override
    public void onSlideClose(View view, int position, int direction) {
        if (mOnSlideListener != null) {
            mOnSlideListener.onSlideClose(view, this, position, direction);
        }
    }

    /**
     * 设置item中的button点击事件的监听器
     *
     * @param onMenuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }

    /**
     * item中的button监听器
     */
    public interface OnMenuItemClickListener {
        /**
         * 点击事件
         *
         * @param v
         * @param itemPosition   第几个item
         * @param buttonPosition 第几个button
         * @param direction      方向
         * @return 参考Menu的几个常量
         */
        int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        if (mOnMenuItemClickListener != null) {
            return mOnMenuItemClickListener.onMenuItemClick(v, itemPosition, buttonPosition, direction);
        }
        return Menu.ITEM_NOTHING;
    }

    @Deprecated
    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnListItemClickListener(OnListItemClickListener listener) {
        mOnListItemClickListener = listener;
    }

    /**
     * 自己的单击事件
     */
    public interface OnListItemClickListener {
        void onListItemClick(View v, int position);
    }

    /**
     * {@link #setOnListItemLongClickListener(OnListItemLongClickListener)}
     *
     * @param listener
     */
    @Deprecated
    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnListItemLongClickListener(OnListItemLongClickListener listener) {
        mOnListItemLongClickListener = listener;
    }

    /**
     * 自己写的长点击事件
     */
    public interface OnListItemLongClickListener {
        void onListItemLongClick(View view, int position);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        mOnItemDeleteListener = onItemDeleteListener;
    }

    public interface OnItemDeleteListener {
        void onItemDelete(View view, int position);
    }

    @Deprecated
    @Override
    public void setOnScrollListener(OnScrollListener l) {
        super.setOnScrollListener(l);
    }

    public void setOnListScrollListener(OnListScrollListener onListScrollListener) {
        mOnListScrollListener = onListScrollListener;
    }

    public interface OnListScrollListener {
        int SCROLL_STATE_IDLE = 0;
        int SCROLL_STATE_TOUCH_SCROLL = 1;
        int SCROLL_STATE_FLING = 2;

        void onScrollStateChanged(AbsListView view, int scrollState);

        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }
}
