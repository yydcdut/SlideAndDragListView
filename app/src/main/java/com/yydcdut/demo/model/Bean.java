package com.yydcdut.demo.model;

/**
 * Created by yuyidong on 15/8/14.
 */
public class Bean {
    public String name;

    public Bean(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "name='" + name + '\'' +
                '}';
    }
}
