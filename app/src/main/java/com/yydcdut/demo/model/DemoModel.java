package com.yydcdut.demo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/8/14.
 */
public class DemoModel {
    private static DemoModel mInstance = new DemoModel();
    private List<Bean> mList;

    private DemoModel() {
        mList = new ArrayList<Bean>();
        for (int i = 0; i < 30; i++) {
            mList.add(new Bean(i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" +
                    i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" +
                    i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" +
                    i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" +
                    i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" +
                    i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" +
                    i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + "" + i + ""));
        }
    }

    public static DemoModel getInstance() {
        return mInstance;
    }

    public List<Bean> getData() {
        return mList;
    }

}
