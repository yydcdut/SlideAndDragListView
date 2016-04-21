package com.yydcdut.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by yuyidong on 16/1/23.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_normal).setOnClickListener(this);
        findViewById(R.id.btn_simple).setOnClickListener(this);
        findViewById(R.id.btn_header_footer).setOnClickListener(this);
        findViewById(R.id.btn_view_type).setOnClickListener(this);
        findViewById(R.id.btn_touch_drag).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_normal:
                startActivity(new Intent(this, NormalActivity.class));
                break;
            case R.id.btn_simple:
                startActivity(new Intent(this, SimpleActivity.class));
                break;
            case R.id.btn_header_footer:
                startActivity(new Intent(this, HeaderFooterActivity.class));
                break;
            case R.id.btn_view_type:
                startActivity(new Intent(this, DifferentActivity.class));
                break;
            case R.id.btn_touch_drag:
                startActivity(new Intent(this, ItemDragActivity.class));
                break;
        }
    }
}
