package com.yydcdut.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by yuyidong on 16/1/22.
 */
public class NormalActivity extends AppCompatActivity {
    private static final String TAG = NormalActivity.class.getSimpleName();

    private List<ApplicationInfo> mAppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        mAppList = getPackageManager().getInstalledApplications(0);
        final ListView listView = (ListView) findViewById(R.id.lv_normal);
        View header = LayoutInflater.from(this).inflate(R.layout.item_header_footer, null);
        View footer = LayoutInflater.from(this).inflate(R.layout.item_header_footer, null);
        footer.setBackgroundColor(0xff0000bb);
        listView.addHeaderView(header);
        listView.addFooterView(footer);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.i(TAG, "onItemClick   position--->" + position);
//                Toast.makeText(NormalActivity.this, "onItemClick   position--->" + position, Toast.LENGTH_SHORT).show();
                final View viewTop = listView.getChildAt(position - listView.getFirstVisiblePosition());
                Log.i(TAG, "" + viewTop.getLeft() + "  " + viewTop.getTop() + "  " + viewTop.getRight() + "  " + viewTop.getBottom());
                final View viewBottom = listView.getChildAt(position - listView.getFirstVisiblePosition() + 1);
                Log.i(TAG, "" + viewBottom.getLeft() + "  " + viewBottom.getTop() + "  " + viewBottom.getRight() + "  " + viewBottom.getBottom());
                final int delta = viewBottom.getTop() - viewTop.getTop();
//                UDAnimation animation = new UDAnimation(delta, viewTop, viewBottom);
//                animation.start();
                ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float disF = delta * ((Float) animation.getAnimatedValue());
                        viewTop.setTranslationY(disF);
                        viewBottom.setTranslationY(-disF);
                        viewTop.invalidate();
                        viewBottom.invalidate();
                    }

                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        viewTop.setTranslationY(0);
                        viewBottom.setTranslationY(0);
                        ApplicationInfo applicationInfo = mAppList.get(position - listView.getHeaderViewsCount());
                        mAppList.set(position - listView.getHeaderViewsCount(), mAppList.get(position + 1 - listView.getHeaderViewsCount()));
                        mAppList.set(position + 1 - listView.getHeaderViewsCount(), applicationInfo);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                animator.setDuration(250).start();

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemLongClick   position--->" + position);
                Toast.makeText(NormalActivity.this, "onItemLongClick   position--->" + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    class UDAnimation extends Animation {
        private int dis;
        private View upView;
        private View downView;

        public UDAnimation(int dis, View upView, View downView) {
            Log.i(TAG, "UDAnimationUDAnimationUDAnimation");
            this.dis = dis;
            this.upView = upView;
            this.downView = downView;
            setDuration(1000);
            start();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float disF = dis * interpolatedTime;
            Log.i(TAG, "" + disF);
            downView.setTranslationY(disF);
            upView.setTranslationY(-disF);
            downView.invalidate();
            downView.requestLayout();
            upView.invalidate();
        }
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
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(NormalActivity.this).inflate(R.layout.item_custom, null);
                cvh.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                cvh.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
                cvh.imgLogo2 = (ImageView) convertView.findViewById(R.id.img_item_edit2);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            ApplicationInfo item = (ApplicationInfo) this.getItem(position);
            cvh.txtName.setText(item.loadLabel(getPackageManager()));
            cvh.imgLogo.setImageDrawable(item.loadIcon(getPackageManager()));
            cvh.imgLogo2.setImageDrawable(item.loadIcon(getPackageManager()));
            return convertView;
        }

        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
            public ImageView imgLogo2;
        }
    };
}
