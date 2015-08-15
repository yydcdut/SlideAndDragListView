package com.yydcdut.sdlv;

import android.content.Context;
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
            holder.layoutCustom = (FrameLayout) convertView.findViewById(R.id.layout_custom);
            holder.layoutScroll = convertView.findViewById(R.id.layout_item_edit);
            holder.btnDelete = (TextView) convertView.findViewById(R.id.txt_item_edit_btn1);
            holder.btnRename = (TextView) convertView.findViewById(R.id.txt_item_edit_btn2);
            holder.layoutBG = convertView.findViewById(R.id.layout_item_edit_bg);
            holder.imgBG = convertView.findViewById(R.id.img_item_edit_bg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
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
        //设置监听器
        holder.btnDelete.setOnClickListener(this);
        holder.btnRename.setOnClickListener(this);
        //把背景显示出来（因为在drag的时候会将背景透明，因为好看）
        holder.imgBG.setVisibility(View.VISIBLE);
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
        public View layoutScroll;
        public TextView btnDelete;
        public TextView btnRename;
        public View layoutBG;
        public View imgBG;
        public FrameLayout layoutCustom;
    }

    /**
     * 设置drag的位置
     *
     * @param dragPosition
     */
    public void setDragPosition(int dragPosition) {
        mDragPosition = dragPosition;
    }

    /**
     * 设置button的位置，或许即将要操作这个位置
     *
     * @param btnPosition
     */
    public void setBtnPosition(int btnPosition) {
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
    public List<T> getDataList() {
        return mDataList;
    }


}
