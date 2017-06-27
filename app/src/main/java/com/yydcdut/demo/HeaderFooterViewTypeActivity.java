package com.yydcdut.demo;

import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 2017/6/23.
 */
public class HeaderFooterViewTypeActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, SlideAndDragListView.OnItemScrollBackListener,
        SlideAndDragListView.OnDragDropListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnItemDeleteListener {
    private static final String TAG = DifferentMenuActivity.class.getSimpleName();

    private List<Menu> mMenuList;
    private List<ApplicationInfo> mAppList;
    private SlideAndDragListView mListView;
    private Toast mToast;
    private View mHeaderView;
    private View mFooterView;
    private ApplicationInfo mDraggedEntity;

    private static final int TYPE_VIEW_HEADER = 1;
    private static final int TYPE_VIEW_FOOTER = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdlv);
        initData();
        initMenu();
        initUiAndListener();
        mToast = Toast.makeText(HeaderFooterViewTypeActivity.this, "", Toast.LENGTH_SHORT);
    }

    public void initData() {
        mAppList = getPackageManager().getInstalledApplications(0);
    }

    public void initMenu() {
        mMenuList = new ArrayList<>();
        Menu menu0 = new Menu(true, 0);
        menu0.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn2_width))
                .setBackground(new ColorDrawable(Color.RED))
                .setText("Normal")
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.WHITE)
                .setTextSize(10)
                .build());
        menu0.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img))
                .setBackground(new ColorDrawable(Color.GREEN))
                .setText("Normal")
                .setDirection(MenuItem.DIRECTION_LEFT)
                .setTextColor(Color.WHITE)
                .setTextSize(10)
                .build());
        Menu menu1 = new Menu(false, 1);
        Menu menu2 = new Menu(false, 2);
        mMenuList.add(menu0);
        mMenuList.add(menu1);
        mMenuList.add(menu2);
    }

    public void initUiAndListener() {
        mHeaderView = LayoutInflater.from(this).inflate(R.layout.item_header_footer, null);
        mFooterView = LayoutInflater.from(this).inflate(R.layout.item_header_footer, null);
        mFooterView.setBackgroundColor(0xff0000bb);
        mListView = (SlideAndDragListView) findViewById(R.id.lv_edit);
        mListView.setMenu(mMenuList);
        mListView.setAdapter(mAdapter);
        mListView.setOnDragDropListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        mListView.setOnItemDeleteListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemScrollBackListener(this);
        mListView.setDivider(new ColorDrawable(Color.GRAY));
        mListView.setDividerHeight(1);
        mListView.setNotDragHeaderCount(1);
        mListView.setNotDragFooterCount(1);
    }

    private BaseAdapter mAdapter = new BaseAdapter() {
        private Object mHeaderObject = new Object();
        private Object mFooterObject = new Object();

        @Override
        public int getCount() {
            return mAppList.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return mHeaderObject;
            } else if (position == mAppList.size() + 1) {
                return mFooterObject;
            }
            return mAppList.get(position - 1);
        }

        @Override
        public long getItemId(int position) {
            if (position == 0) {
                return 1;
            } else if (position == mAppList.size() + 1) {
                return 2;
            }
            return mAppList.get(position - 1).hashCode();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_VIEW_HEADER;
            } else if (position == mAppList.size() + 1) {
                return TYPE_VIEW_FOOTER;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int viewType = getItemViewType(position);
            if (viewType == TYPE_VIEW_HEADER) {
                return mHeaderView;
            } else if (viewType == TYPE_VIEW_FOOTER) {
                return mFooterView;
            }
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(HeaderFooterViewTypeActivity.this).inflate(R.layout.item_custom_btn, null);
                cvh.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                cvh.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
                cvh.btnClick = (Button) convertView.findViewById(R.id.btn_item_click);
                cvh.btnClick.setVisibility(View.GONE);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            ApplicationInfo item = (ApplicationInfo) this.getItem(position);
            cvh.txtName.setText(item.loadLabel(getPackageManager()));
            cvh.imgLogo.setImageDrawable(item.loadIcon(getPackageManager()));
            return convertView;
        }

        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
            public Button btnClick;
        }
    };

    @Override
    public void onDragViewStart(int beginPosition) {
        if (beginPosition == 0) {
            return;
        }
        mDraggedEntity = mAppList.get(beginPosition - 1);//-1 --> header viewType
        toast("onDragViewStart   beginPosition--->" + beginPosition);
    }

    @Override
    public void onDragDropViewMoved(int fromPosition, int toPosition) {
        if (toPosition == 0 || toPosition == mAdapter.getCount()) {
            return;
        }
        ApplicationInfo applicationInfo = mAppList.remove(fromPosition - 1);//-1 --> header viewType
        mAppList.add(toPosition - 1, applicationInfo);//-1 --> header viewType
        toast("onDragDropViewMoved   fromPosition--->" + fromPosition + "  toPosition-->" + toPosition);
    }

    @Override
    public void onDragViewDown(int finalPosition) {
        if (finalPosition == mAdapter.getCount() - 1) {
            return;
        }
        mAppList.set(finalPosition - 1, mDraggedEntity);//-1 --> header viewType
        toast("onDragViewDown   finalPosition--->" + finalPosition);
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
        toast("onSlideOpen   position--->" + position + "  direction--->" + direction);
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
        toast("onSlideClose   position--->" + position + "  direction--->" + direction);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        toast("onMenuItemClick   itemPosition--->" + itemPosition + "  buttonPosition-->" + buttonPosition + "  direction-->" + direction);
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
    public void onItemDeleteAnimationFinished(View view, int position) {
        mAppList.remove(position - mListView.getHeaderViewsCount());
        mAdapter.notifyDataSetChanged();
        toast("onItemDeleteAnimationFinished   position--->" + position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        toast("onItemLongClick   position--->" + position);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        toast("onItemClick   position--->" + position);
    }

    @Override
    public void onScrollBackAnimationFinished(View view, int position) {
        toast("onScrollBackAnimationFinished   position--->" + position);
    }

    private void toast(String toast) {
        mToast.setText(toast);
        mToast.show();
    }

}
