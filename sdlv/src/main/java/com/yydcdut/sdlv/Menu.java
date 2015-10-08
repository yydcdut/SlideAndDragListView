package com.yydcdut.sdlv;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/10/8.
 */
public class Menu {
    private List<MenuItem> mMenuItems;

    private int mItemHeight;
    private Drawable itemBackGroundDrawable;
    private int mTotalBtnLength;

    public Menu(int itemHeight, Drawable itemBackGroundDrawable) {
        this.mItemHeight = itemHeight;
        this.itemBackGroundDrawable = itemBackGroundDrawable;
        mMenuItems = new ArrayList<>();
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public Drawable getItemBackGroundDrawable() {
        return itemBackGroundDrawable;
    }

    public int getTotalBtnLength() {
        return mTotalBtnLength;
    }

    public boolean addItem(MenuItem menuItem) {
        mTotalBtnLength += menuItem.width;
        return mMenuItems.add(menuItem);
    }

    public boolean removeItem(MenuItem menuItem) {
        mTotalBtnLength -= menuItem.width;
        return mMenuItems.remove(menuItem);
    }

    public List<MenuItem> getMenuItems() {
        //todo 获取到List之后自己操作add或者remove的话btn总长度不会有操作变化
        return mMenuItems;
    }
}
