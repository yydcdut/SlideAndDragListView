/*
 * Copyright (C) 2015 yydcdut (yuyidong2015@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yydcdut.sdlv;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yuyidong on 2016/11/18.
 */
public abstract class BaseLayout extends FrameLayout {
    protected MenuItem mMenuItem;

    protected BaseLayout(Context context, MenuItem menuItem) {
        super(context);
        mMenuItem = menuItem;
    }

    protected abstract void build();

    public abstract TextView getTextView();

    public abstract ImageView getImageView();
}
