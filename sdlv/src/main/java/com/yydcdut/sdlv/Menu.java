package com.yydcdut.sdlv;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/10/8.
 */
public final class Menu {
    private List<MenuItem> mLeftMenuItems;
    private List<MenuItem> mRightMenuItems;

    private int mItemHeight;
    private Drawable itemBackGroundDrawable;
    private boolean mWannaOver = true;
    private int mTotalLeftBtnLength = 0;
    private int mTotalRightBtnLength = 0;

    public Menu(int itemHeight, Drawable itemBackGroundDrawable) {
        this(itemHeight, itemBackGroundDrawable, true);
    }

    public Menu(int itemHeight, Drawable itemBackGroundDrawable, boolean wannaOver) {
        this.mItemHeight = itemHeight;
        this.itemBackGroundDrawable = itemBackGroundDrawable;
        this.mWannaOver = wannaOver;
        mLeftMenuItems = new ArrayList<>();
        mRightMenuItems = new ArrayList<>();
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public Drawable getItemBackGroundDrawable() {
        return itemBackGroundDrawable;
    }

    public boolean isWannaOver() {
        return mWannaOver;
    }

    public int getTotalBtnLength(int direction) {
        if (direction == MenuItem.DERACTION_LEFT) {
            return mTotalLeftBtnLength;
        } else {
            return mTotalRightBtnLength;
        }
    }

    public void addItem(MenuItem menuItem) {
        if (menuItem.direction == MenuItem.DERACTION_LEFT) {
            mTotalLeftBtnLength += menuItem.width;
            mLeftMenuItems.add(menuItem);
        } else {
            mTotalRightBtnLength += menuItem.width;
            mRightMenuItems.add(menuItem);
        }
    }

    public void addItem(MenuItem menuItem, int position) {
        if (menuItem.direction == MenuItem.DERACTION_LEFT) {
            mTotalLeftBtnLength += menuItem.width;
            mLeftMenuItems.add(position, menuItem);
        } else {
            mTotalRightBtnLength += menuItem.width;
            mRightMenuItems.add(position, menuItem);
        }
    }

    public boolean removeItem(MenuItem menuItem) {
        if (menuItem.direction == MenuItem.DERACTION_LEFT) {
            mTotalLeftBtnLength -= menuItem.width;
            return mLeftMenuItems.remove(menuItem);
        } else {
            mTotalRightBtnLength -= menuItem.width;
            return mRightMenuItems.remove(menuItem);
        }
    }

    public List<MenuItem> getMenuItems(int direction) {
        //todo 获取到List之后自己操作add或者remove的话btn总长度不会有操作变化
        if (direction == MenuItem.DERACTION_LEFT) {
            return mLeftMenuItems;
        } else {
            return mRightMenuItems;
        }
    }

}
