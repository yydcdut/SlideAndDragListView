package com.yydcdut.demo.model;

/**
 * Created by yuyidong on 16/1/25.
 */
public class QQ {
    private final String name;
    private final String content;
    private final String time;
    private final int drawableRes;
    private final boolean isQun;
    private final int qunNumber;

    public QQ(String name, String content, String time, int drawableRes) {
        this(name, content, time, drawableRes, false, 1);
    }

    public QQ(String name, String content, String time, int drawableRes, boolean isQun, int qunNumber) {
        this.name = name;
        this.content = content;
        this.time = time;
        this.drawableRes = drawableRes;
        this.isQun = isQun;
        this.qunNumber = qunNumber;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public boolean isQun() {
        return isQun;
    }

    public int getQunNumber() {
        return qunNumber;
    }
}
