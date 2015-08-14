package com.yydcdut.demo.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.yydcdut.demo.R;
import com.yydcdut.demo.adapter.EditAdapter;
import com.yydcdut.demo.model.Bean;
import com.yydcdut.demo.model.DemoModel;
import com.yydcdut.demo.view.SlideAndDragListView;

import java.util.List;

/**
 * Created by yuyidong on 15/7/31.
 */
public class EditActivity extends AppCompatActivity implements SlideAndDragListView.OnListItemLongClickListener, SlideAndDragListView.OnDragListener,
        SlideAndDragListView.OnSlideListener, SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnButtonClickListenerProxy {
    private SlideAndDragListView mListView;
    private List<Bean> mDataList;
    private EditAdapter mBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initUiAndListener();
    }

    public void initUiAndListener() {
        mListView = (SlideAndDragListView) findViewById(R.id.lv_edit);
        mDataList = DemoModel.getInstance().getData();
        mBaseAdapter = new EditAdapter(this, mDataList);
        mListView.setAdapter(mBaseAdapter);
        mListView.setData(mDataList);
        mListView.setOnListItemLongClickListener(this);
        mListView.setOnDragListener(this);
        mListView.setOnListItemClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnButtonClickListenerProxy(this);
    }

    @Override
    public void onListItemLongClick(View view, int position) {
        mBaseAdapter.setDragPosition(position);
    }

    @Override
    public void onDragViewMoving(int position) {
        mBaseAdapter.setDragPosition(position);

    }

    @Override
    public void onDragViewDown(int position) {
        mBaseAdapter.setDragPosition(-1);

    }

    @Override
    public void onSlideOpen(View view, int position) {
        mBaseAdapter.setBtnPosition(position);
        mListView.setOnListItemClickListener(null);
    }

    @Override
    public void onSlideClose(View view, int position) {
        mBaseAdapter.setBtnPosition(-1);
        mListView.setOnListItemClickListener(this);
    }

    @Override
    public void onClick(View v, int position, int number) {
        Log.i("yuyidong", "onClick " + position + "   number--->" + number);
    }

    @Override
    public void onListItemClick(View v, int position) {
        Log.i("yuyidong", "onListItemClick  position--->" + position);
    }
}
