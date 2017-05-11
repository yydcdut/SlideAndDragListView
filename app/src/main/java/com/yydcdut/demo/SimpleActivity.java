package com.yydcdut.demo;

import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.List;

/**
 * Created by yuyidong on 16/1/23.
 */
public class SimpleActivity extends AppCompatActivity implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener, SlideAndDragListView.OnListScrollListener {
    private static final String TAG = SimpleActivity.class.getSimpleName();

    private Menu mMenu;
    private List<ApplicationInfo> mAppList;
    private SlideAndDragListView<ApplicationInfo> mListView;
    private Toast mToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdlv);
        initData();
        initMenu();
        initUiAndListener();
    }

    public void initData() {
        mAppList = getPackageManager().getInstalledApplications(0);
    }

    public void initMenu() {
        mMenu = new Menu(true, true);
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) * 2)
                .setBackground(Utils.getDrawable(this, R.drawable.btn_left0))
                .setText("One")
                .setTextColor(Color.GRAY)
                .setTextSize(14)
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setBackground(Utils.getDrawable(this, R.drawable.btn_left1))
                .setText("Two")
                .setTextColor(Color.BLACK)
                .setTextSize((14))
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) + 30)
                .setBackground(Utils.getDrawable(this, R.drawable.btn_right0))
                .setText("Three")
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.BLACK)
                .setTextSize(14)
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img))
                .setBackground(Utils.getDrawable(this, R.drawable.btn_right1))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.drawable.ic_launcher))
                .build());
    }

    public void initUiAndListener() {
        mListView = (SlideAndDragListView) findViewById(R.id.lv_edit);
        mListView.setMenu(mMenu);
        mListView.setAdapter(mAdapter);
        mListView.setOnListItemLongClickListener(this);
        mListView.setOnDragListener(this, mAppList);
        mListView.setOnListItemClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        mListView.setOnItemDeleteListener(this);
        mListView.setOnListScrollListener(this);
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
            return mAppList.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(SimpleActivity.this).inflate(R.layout.item_custom_btn, null);
                cvh.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                cvh.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
                cvh.btnClick = (Button) convertView.findViewById(R.id.btn_item_click);
                cvh.btnClick.setOnClickListener(mOnClickListener);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            ApplicationInfo item = (ApplicationInfo) this.getItem(position);
            cvh.txtName.setText(item.loadLabel(getPackageManager()));
            cvh.imgLogo.setImageDrawable(item.loadIcon(getPackageManager()));
            cvh.btnClick.setText(position + "");
            cvh.btnClick.setTag(position);
            return convertView;
        }

        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
            public Button btnClick;
        }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object o = v.getTag();
                if (o instanceof Integer) {
                    Toast.makeText(SimpleActivity.this, "button click-->" + ((Integer) o), Toast.LENGTH_SHORT).show();
                }
            }
        };
    };

    @Override
    public void onListItemLongClick(View view, int position) {
//        boolean bool = mListView.startDrag(position);
//        Toast.makeText(SimpleActivity.this, "onItemLongClick   position--->" + position + "   drag-->" + bool, Toast.LENGTH_SHORT).show();
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(SimpleActivity.this, "onItemLongClick   position--->" + position, Toast.LENGTH_SHORT);
        mToast.show();
        Log.i(TAG, "onListItemLongClick   " + position);
    }

    @Override
    public void onDragViewStart(int position) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(SimpleActivity.this, "onDragViewStart   position--->" + position, Toast.LENGTH_SHORT);
        mToast.show();
        Log.i(TAG, "onDragViewStart   " + position);
    }

    @Override
    public void onDragViewMoving(int position) {
//        Toast.makeText(DemoActivity.this, "onDragViewMoving   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i("yuyidong", "onDragViewMoving   " + position);
    }

    @Override
    public void onDragViewDown(int position) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(SimpleActivity.this, "onDragViewDown   position--->" + position, Toast.LENGTH_SHORT);
        mToast.show();
        Log.i(TAG, "onDragViewDown   " + position);
    }

    @Override
    public void onListItemClick(View v, int position) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(SimpleActivity.this, "onItemClick   position--->" + position, Toast.LENGTH_SHORT);
        mToast.show();
        Log.i(TAG, "onListItemClick   " + position);
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(SimpleActivity.this, "onSlideOpen   position--->" + position + "  direction--->" + direction, Toast.LENGTH_SHORT);
        mToast.show();
        Log.i(TAG, "onSlideOpen   " + position);
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(SimpleActivity.this, "onSlideClose   position--->" + position + "  direction--->" + direction, Toast.LENGTH_SHORT);
        mToast.show();
        Log.i(TAG, "onSlideClose   " + position);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        Log.i(TAG, "onMenuItemClick   " + itemPosition + "   " + buttonPosition + "   " + direction);
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

    @Override
    public void onItemDelete(View view, int position) {
        mAppList.remove(position - mListView.getHeaderViewsCount());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_IDLE:
                break;
            case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_FLING:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_drag:
                if (item.getTitle().toString().startsWith("Enable")) {
                    mListView.setOnDragListener(this, mAppList);
                    item.setTitle("Disable Drag");
                } else {
                    mListView.setOnDragListener(null, null);
                    item.setTitle("Enable Drag");
                }
                break;
            case R.id.menu_item_click:
                if (item.getTitle().toString().startsWith("Enable")) {
                    mListView.setOnListItemClickListener(this);
                    item.setTitle("Disable Item Click");
                } else {
                    mListView.setOnListItemClickListener(null);
                    item.setTitle("Enable Item Click");
                }
                break;
            case R.id.menu_item_long_click:
                if (item.getTitle().toString().startsWith("Enable")) {
                    mListView.setOnListItemLongClickListener(this);
                    item.setTitle("Disable Item Long Click");
                } else {
                    mListView.setOnListItemLongClickListener(null);
                    item.setTitle("Enable Item Long Click");
                }
                break;
            case R.id.menu_item_close_menu:
                mListView.closeSlidedItem();
                break;
            case R.id.menu_item_delete_menu:
                mListView.deleteSlideItem();
                break;
        }
        return true;
    }

}
