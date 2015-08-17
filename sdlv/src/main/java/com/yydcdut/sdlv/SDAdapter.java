package com.yydcdut.sdlv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yuyidong on 15/8/14.
 */
public abstract class SDAdapter<T> extends BaseAdapter implements View.OnClickListener {
    /* 上下文 */
    private Context mContext;
    /* 数据 */
    private List<T> mDataList;
    /* Drag的位置 */
    private int mDragPosition = -1;
    /* 点击button的位置 */
    private int mBtnPosition = -1;
    /* button的单击监听器 */
    private OnButtonClickListener mOnButtonClickListener;
    /* 当前滑开的item的位置 */
    private int mSlideOpenItemPosition;
    /* ---------- attrs ----------- */
    private float mItemHeight;
    private int mItemBtnNumber;
    private String mItemBtn1Text;
    private String mItemBtn2Text;
    private float mItemBtnWidth;
    private Drawable mItemBGDrawable;
    /* ---------- attrs ----------- */


    public SDAdapter(Context context, List<T> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sdlv, null);
            holder.layoutMain = (SDItemLayout) convertView.findViewById(R.id.layout_item_main);
            holder.layoutMain.setItemHeight((int) mItemHeight);
            holder.layoutScroll = (SDItemLayout) convertView.findViewById(R.id.layout_item_scroll);
            holder.layoutScroll.setItemHeight((int) mItemHeight);
            holder.layoutBG = (SDItemLayout) convertView.findViewById(R.id.layout_item_bg);
            holder.layoutBG.setItemHeight((int) mItemHeight);
            holder.imgBGScroll = (SDItemBGImage) convertView.findViewById(R.id.img_item_scroll_bg);
            holder.imgBGScroll.setItemHeight((int) mItemHeight);
            holder.imgBG = (SDItemBGImage) convertView.findViewById(R.id.img_item_bg);
            holder.imgBG.setItemHeight((int) mItemHeight);
            holder.layoutCustom = (FrameLayout) convertView.findViewById(R.id.layout_custom);
            holder.btn1 = (TextView) convertView.findViewById(R.id.txt_item_edit_btn1);
            holder.btn2 = (TextView) convertView.findViewById(R.id.txt_item_edit_btn2);
            if (mItemBGDrawable != null) {
                holder.imgBG.setBackgroundDrawable(mItemBGDrawable);
                holder.imgBGScroll.setBackgroundDrawable(mItemBGDrawable);
            }
            //判断哪些隐藏哪些显示
            checkVisible(holder);
            //设置text
            holder.btn1.setText(mItemBtn1Text);//setText有容错处理
            holder.btn2.setText(mItemBtn2Text);//setText有容错处理
            //设置监听器
            holder.btn1.setOnClickListener(this);
            holder.btn2.setOnClickListener(this);

            holder.btn1.setClickable(false);
            holder.btn2.setClickable(false);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //没有展开的item里面的btn是不可点击的
        if (mSlideOpenItemPosition == position) {
            holder.btn1.setClickable(true);
            holder.btn2.setClickable(true);
        } else {
            holder.btn1.setClickable(false);
            holder.btn2.setClickable(false);
        }

        //用户的view
        View customView = getView(mContext, holder.layoutCustom.getChildAt(0), position, mDragPosition);
        if (holder.layoutCustom.getChildAt(0) == null) {
            holder.layoutCustom.addView(customView);
        } else {
            holder.layoutCustom.removeViewAt(0);
            holder.layoutCustom.addView(customView);
        }
        //所有的都归位
        holder.layoutScroll.scrollTo(0, 0);

        //把背景显示出来（因为在drag的时候会将背景透明，因为好看）
        holder.imgBGScroll.setVisibility(View.VISIBLE);
        holder.layoutBG.setVisibility(View.VISIBLE);
        return convertView;
    }

    /**
     * 与BaseAdapter类似
     *
     * @param context
     * @param convertView
     * @param position
     * @param dragPosition 当前拖动的item的位置，如果没有拖动item的话值是-1
     * @return
     */
    public abstract View getView(Context context, View convertView, int position, int dragPosition);

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txt_item_edit_btn1) {
            if (mOnButtonClickListener != null && mBtnPosition != -1) {
                mOnButtonClickListener.onClick(v, mBtnPosition, 0);
            }
        } else if (v.getId() == R.id.txt_item_edit_btn2) {
            if (mOnButtonClickListener != null && mBtnPosition != -1) {
                mOnButtonClickListener.onClick(v, mBtnPosition, 1);
            }
        }
    }

    class ViewHolder {
        public SDItemLayout layoutMain;
        public SDItemLayout layoutScroll;
        public SDItemLayout layoutBG;
        public SDItemBGImage imgBGScroll;
        public SDItemBGImage imgBG;
        public TextView btn1;
        public TextView btn2;
        public FrameLayout layoutCustom;
    }

    /**
     * 判断用户要几个button
     *
     * @param vh
     */
    private void checkVisible(ViewHolder vh) {
        switch (mItemBtnNumber) {
            case 0:
                vh.btn1.setVisibility(View.GONE);
                vh.btn2.setVisibility(View.GONE);
                break;
            case 1:
                vh.btn1.setVisibility(View.VISIBLE);
                vh.btn2.setVisibility(View.GONE);
                break;
            case 2:
                vh.btn1.setVisibility(View.VISIBLE);
                vh.btn2.setVisibility(View.VISIBLE);
                break;
            default:
                throw new IllegalArgumentException("");
        }
        vh.btn1.setClickable(false);
        vh.btn2.setClickable(false);
    }

    /**
     * 设置drag的位置
     *
     * @param dragPosition
     */
    protected void setDragPosition(int dragPosition) {
        mDragPosition = dragPosition;
    }

    /**
     * 设置button的位置，或许即将要操作这个位置
     *
     * @param btnPosition
     */
    protected void setBtnPosition(int btnPosition) {
        mBtnPosition = btnPosition;
    }

    /**
     * “删除”，“重命名”的单击事件
     */
    public interface OnButtonClickListener {
        /**
         * 点击事件
         *
         * @param v
         * @param position 当前点击的是哪个item的
         * @param number   当前点击的是第几个
         */
        void onClick(View v, int position, int number);
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnButtonClickListener(OnButtonClickListener listener) {
        mOnButtonClickListener = listener;
    }

    /**
     * 获取数据
     *
     * @return
     */
    protected List<T> getDataList() {
        return mDataList;
    }

    protected void setItemHeight(float height) {
        mItemHeight = height;
    }

    protected void setItemBtnNumber(int number, String... text) {
        mItemBtnNumber = number;
        mItemBtn1Text = text[0];
        mItemBtn2Text = text[1];
    }

    protected void setItemBtnWidth(float width) {
        mItemBtnWidth = width;
    }

    protected void setSlideOpenItemPosition(int position) {
        mSlideOpenItemPosition = position;
        notifyDataSetChanged();
    }

    protected void setItemBGDrawable(Drawable drawable) {
        mItemBGDrawable = drawable;
    }

}
