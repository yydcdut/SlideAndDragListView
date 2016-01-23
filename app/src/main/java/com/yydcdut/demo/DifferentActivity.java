package com.yydcdut.demo;

import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 16/1/23.
 */
public class DifferentActivity extends AppCompatActivity implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener {
    private static final String TAG = DifferentActivity.class.getSimpleName();

    private List<Menu> mMenuList;
    private List<ApplicationInfo> mAppList;
    private SlideAndDragListView<ApplicationInfo> mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initData();
        initMenu();
        initUiAndListener();
    }

    public void initData() {
        mAppList = getPackageManager().getInstalledApplications(0);
    }

    public void initMenu() {
        mMenuList = new ArrayList<>(2);
        Menu menu0 = new Menu((int) getResources().getDimension(R.dimen.slv_item_height), new ColorDrawable(Color.LTGRAY), true, 0);
        menu0.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) * 2)
                .setBackground(new ColorDrawable(Color.RED))
                .setText("One")
                .setTextColor(Color.GRAY)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());
        menu0.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setBackground(new ColorDrawable(Color.GREEN))
                .setText("Two")
                .setTextColor(Color.BLACK)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());
        menu0.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) + 30)
                .setBackground(new ColorDrawable(Color.BLUE))
                .setText("Three")
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.BLACK)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());
        menu0.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img))
                .setBackground(new ColorDrawable(Color.BLACK))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.drawable.ic_launcher))
                .build());
        Menu menu1 = new Menu((int) getResources().getDimension(R.dimen.slv_item_height2), new ColorDrawable(Color.YELLOW), false, 1);
        menu1.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) * 2)
                .setBackground(new ColorDrawable(Color.RED))
                .setText("Four")
                .setTextColor(Color.GRAY)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());
        menu1.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) + 30)
                .setBackground(new ColorDrawable(Color.BLUE))
                .setText("Five")
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.BLACK)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());
        mMenuList.add(menu0);
        mMenuList.add(menu1);
    }

    public void initUiAndListener() {
        mListView = (SlideAndDragListView) findViewById(R.id.lv_edit);
        mListView.setMenu(mMenuList);
        mListView.setAdapter(mAdapter);
        mListView.setOnListItemLongClickListener(this);
        mListView.setOnDragListener(this, mAppList);
        mListView.setOnListItemClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        mListView.setOnItemDeleteListener(this);
    }

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position % 2;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh = null;
            CustomViewHolder2 cvh2 = null;
            if (convertView == null) {
                if (getItemViewType(position) == 0) {
                    cvh = new CustomViewHolder();
                    convertView = LayoutInflater.from(DifferentActivity.this).inflate(R.layout.item_custom, null);
                    cvh.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                    cvh.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
                    cvh.imgLogo2 = (ImageView) convertView.findViewById(R.id.img_item_edit2);
                    convertView.setTag(cvh);
                } else {
                    cvh2 = new CustomViewHolder2();
                    convertView = LayoutInflater.from(DifferentActivity.this).inflate(R.layout.item_custom_2, null);
                    cvh2.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                    cvh2.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
                    convertView.setTag(cvh2);
                }
            } else {
                if (getItemViewType(position) == 0) {
                    cvh = (CustomViewHolder) convertView.getTag();
                } else {
                    cvh2 = (CustomViewHolder2) convertView.getTag();
                }
            }
            ApplicationInfo item = (ApplicationInfo) this.getItem(position);
            if (getItemViewType(position) == 0) {
                cvh.txtName.setText(item.loadLabel(getPackageManager()));
                cvh.imgLogo.setImageDrawable(item.loadIcon(getPackageManager()));
                cvh.imgLogo2.setImageDrawable(item.loadIcon(getPackageManager()));
            } else {
                cvh2.txtName.setText(item.loadLabel(getPackageManager()));
                cvh2.imgLogo.setImageDrawable(item.loadIcon(getPackageManager()));
            }
            return convertView;
        }

        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
            public ImageView imgLogo2;
        }

        class CustomViewHolder2 {
            public ImageView imgLogo;
            public TextView txtName;
        }
    };

    @Override
    public void onListItemLongClick(View view, int position) {
//        Toast.makeText(DemoActivity.this, "onItemLongClick   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onListItemLongClick   " + position);
    }

    @Override
    public void onDragViewStart(int position) {
//        Toast.makeText(DemoActivity.this, "onDragViewStart   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDragViewStart   " + position);
    }

    @Override
    public void onDragViewMoving(int position) {
//        Toast.makeText(DemoActivity.this, "onDragViewMoving   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDragViewMoving   " + position);
    }

    @Override
    public void onDragViewDown(int position) {
//        Toast.makeText(DemoActivity.this, "onDragViewDown   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDragViewDown   " + position);
    }

    @Override
    public void onListItemClick(View v, int position) {
//        Toast.makeText(DemoActivity.this, "onItemClick   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onListItemClick   " + position);
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
//        Toast.makeText(DemoActivity.this, "onSlideOpen   position--->" + position + "  direction--->" + direction, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onSlideOpen   " + position);
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
//        Toast.makeText(DemoActivity.this, "onSlideClose   position--->" + position + "  direction--->" + direction, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onSlideClose   " + position);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        Log.i(TAG, "onMenuItemClick   " + itemPosition + "   " + buttonPosition + "   " + direction);
        int viewType = mAdapter.getItemViewType(itemPosition);
        switch (viewType) {
            case 0:
                return clickMenuBtn0(buttonPosition, direction);
            case 1:
                return clickMenuBtn1(buttonPosition, direction);
            default:
                return Menu.ITEM_NOTHING;
        }
    }

    private int clickMenuBtn0(int buttonPosition, int direction) {
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_NOTHING;
                    case 1:
                        return Menu.ITEM_SCROLL_BACK;
                }
                break;
            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_SCROLL_BACK;
                    case 1:
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                }
        }
        return Menu.ITEM_NOTHING;
    }

    private int clickMenuBtn1(int buttonPosition, int direction) {
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_SCROLL_BACK;
                }
                break;
            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                }
        }
        return Menu.ITEM_NOTHING;
    }

    @Override
    public void onItemDelete(View view, int position) {
        mAppList.remove(position - mListView.getHeaderViewsCount());
        mAdapter.notifyDataSetChanged();
    }
}
