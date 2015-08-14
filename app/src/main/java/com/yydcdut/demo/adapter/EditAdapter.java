package com.yydcdut.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yydcdut.demo.R;
import com.yydcdut.demo.model.Bean;
import com.yydcdut.demo.utils.RandomColor;
import com.yydcdut.demo.view.TextDrawable;

import java.util.List;

/**
 * Created by yuyidong on 15/8/14.
 */
public class EditAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private List<Bean> mDataList;
    private RandomColor mColor = RandomColor.MATERIAL;
    /* Drag的位置 */
    private int mDragPosition = -1;
    /* 点击button的位置 */
    private int mBtnPosition = -1;
    /* button的单击监听器 */
    private OnButtonClickListener mOnButtonClickListener;


    public EditAdapter(Context context, List<Bean> dataList) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_edit, null);
            holder.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
            holder.layoutScroll = convertView.findViewById(R.id.layout_item_edit);
            holder.btnDelete = (TextView) convertView.findViewById(R.id.txt_item_edit_delete);
            holder.btnRename = (TextView) convertView.findViewById(R.id.txt_item_edit_rename);
            holder.layoutBG = convertView.findViewById(R.id.layout_item_edit_bg);
            holder.imgBG = convertView.findViewById(R.id.img_item_edit_bg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //把当前选中的颜色变为红色
        String name = mDataList.get(position).name;
        if (mDragPosition == position) {
            holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(name, mContext.getResources().getColor(R.color.red_colorPrimary)));
            holder.txtName.setTextColor(mContext.getResources().getColor(R.color.red_colorPrimary));
        } else {
            holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(name, mColor.getColor(name)));
            holder.txtName.setTextColor(mContext.getResources().getColor(R.color.txt_gray));
        }
        //设置name
        holder.txtName.setText(mDataList.get(position).name);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_item_edit_delete:
                if (mOnButtonClickListener != null && mBtnPosition != -1) {
                    mOnButtonClickListener.onClick(v, mBtnPosition, 0);
                }
                break;
            case R.id.txt_item_edit_rename:
                if (mOnButtonClickListener != null && mBtnPosition != -1) {
                    mOnButtonClickListener.onClick(v, mBtnPosition, 1);
                }
                break;
        }
    }

    class ViewHolder {
        public View layoutScroll;
        public ImageView imgLogo;
        public TextView txtName;
        public TextView btnDelete;
        public TextView btnRename;
        public View layoutBG;
        public View imgBG;
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
    public List<Bean> getDataList() {
        return mDataList;
    }
}
