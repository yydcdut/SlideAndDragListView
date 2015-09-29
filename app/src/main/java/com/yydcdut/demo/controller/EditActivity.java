package com.yydcdut.demo.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.demo.R;
import com.yydcdut.demo.model.Bean;
import com.yydcdut.demo.model.DemoModel;
import com.yydcdut.demo.utils.RandomColor;
import com.yydcdut.demo.view.TextDrawable;
import com.yydcdut.sdlv.SDAdapter;
import com.yydcdut.sdlv.SlideAndDragListView;
import com.yydcdut.sdlv.SlideAndDragListView1;

/**
 * Created by yuyidong on 15/7/31.
 */
public class EditActivity extends AppCompatActivity implements SlideAndDragListView.OnListItemLongClickListener, SlideAndDragListView.OnDragListener,
        SlideAndDragListView1.OnSlideListener, SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnButtonClickListenerProxy {

    private RandomColor mColor = RandomColor.MATERIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initUiAndListener();
    }

    public void initUiAndListener() {
        SlideAndDragListView1 listView = (SlideAndDragListView1) findViewById(R.id.lv_edit);
        listView.setAdapter(mAdapter);
//        listView.setOnListItemLongClickListener(this);
//        listView.setOnDragListener(this);
//        listView.setOnListItemClickListener(this);
        listView.setOnSlideListener(this);
//        listView.setOnButtonClickListenerProxy(this);
    }

    private SDAdapter mBaseAdapter = new SDAdapter(this, DemoModel.getInstance().getData()) {

        @Override
        public View getView(Context context, View convertView, int position, int dragPosition) {
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_custom, null);
                cvh.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                cvh.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            Bean bean = (Bean) this.getItem(position);
            cvh.txtName.setText(bean.name);
            //把当前选中的颜色变为红色
            if (dragPosition == position) {
                cvh.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(bean.name, context.getResources().getColor(R.color.red_colorPrimary)));
                cvh.txtName.setTextColor(context.getResources().getColor(R.color.red_colorPrimary));
            } else {
                cvh.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(bean.name, mColor.getColor(bean.name)));
                cvh.txtName.setTextColor(context.getResources().getColor(R.color.txt_gray));
            }
            return convertView;
        }

        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
        }

    };

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return DemoModel.getInstance().getData().size();
        }

        @Override
        public Object getItem(int position) {
            return DemoModel.getInstance().getData().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(EditActivity.this).inflate(R.layout.item_custom, null);
                cvh.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                cvh.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            Bean bean = (Bean) this.getItem(position);
            cvh.txtName.setText(bean.name);
            //把当前选中的颜色变为红色
//            if (dragPosition == position) {
//                cvh.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(bean.name, EditActivity.this.getResources().getColor(R.color.red_colorPrimary)));
//                cvh.txtName.setTextColor(EditActivity.this.getResources().getColor(R.color.red_colorPrimary));
//            } else {
            cvh.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(bean.name, mColor.getColor(bean.name)));
            cvh.txtName.setTextColor(EditActivity.this.getResources().getColor(R.color.txt_gray));
//            }
            return convertView;
        }

        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
        }
    };

    @Override
    public void onListItemLongClick(View view, int position) {
//        Toast.makeText(EditActivity.this, "onItemLongClick   position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDragViewMoving(int position) {
//        Toast.makeText(EditActivity.this, "onDragViewMoving   position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDragViewDown(int position) {
//        Toast.makeText(EditActivity.this, "onDragViewDown   position--->" + position, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onSlideOpen(View view, int position) {
////        Toast.makeText(EditActivity.this, "onSlideOpen   position--->" + position, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onSlideClose(View view, int position) {
////        Toast.makeText(EditActivity.this, "onSlideClose   position--->" + position, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onClick(View v, int position, int number) {
        Toast.makeText(EditActivity.this, "onClick   position--->" + position + "   number--->" + number, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListItemClick(View v, int position) {
        Toast.makeText(EditActivity.this, "onItemClick   position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position) {
        Toast.makeText(EditActivity.this, "onSlideOpen   position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSlideClose(View view, View parentView, int position) {
        Toast.makeText(EditActivity.this, "onSlideClose   position--->" + position, Toast.LENGTH_SHORT).show();
    }
}
