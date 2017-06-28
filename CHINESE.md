# SlideAndDragListView

  [![Download](https://api.bintray.com/packages/yydcdut/maven/sdlv/images/download.svg)](https://bintray.com/yydcdut/maven/sdlv/_latestVersion)       [![License](http://img.shields.io/:license-apache-blue.svg)](LICENSE.txt)  [![Build Status](https://travis-ci.org/yydcdut/SlideAndDragListView.svg?branch=master)](https://travis-ci.org/yydcdut/SlideAndDragListView)    [![API](https://img.shields.io/badge/API-11%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=11)  <a href="http://www.methodscount.com/?lib=com.yydcdut.sdlv%3Asdlv%3A0.7.0"><img src="https://img.shields.io/badge/Methods count-287-e91e63.svg"></img></a>   <a href="http://www.methodscount.com/?lib=com.yydcdut.sdlv%3Asdlv%3A0.7.0"><img src="https://img.shields.io/badge/Size-29 KB-e91e63.svg"></img></a>

一个可以左右滑动 item 和拖放 item 的 ListView

![sdlv](https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/v1.1.gif)

Demo: [下载](https://github.com/yydcdut/SlideAndDragListView/blob/master/apk/sdlv.apk?raw=true) 或者 [二维码](http://fir.im/sjfh)

更新日志: [CHANGELOG.md](https://github.com/yydcdut/SlideAndDragListView/blob/master/CHANGELOG.md)

# 简介

SlideAndDragListView (SDLV) 继承与 ListView，SDLV 可以向左或者向右滑动 item，并且可以拖放 item 达到排序的目的

一些特点：

1. 清晰的拖放操作
2. 在拖放的时候的直观和平滑滚动
3. 支持item的单击和长单击事件
4. 丰富的回调接口
5. 滑动 item 的方向可以是向左、向右或者两者
6. 等等......

SlideAndDragListView 用于各种优先级列表：收藏夹，播放列表，清单等。我希望你觉得它有用，同时，如果遇到什么问题，或者有什么建议，可以邮件我或者 issue！

# 引用



## Gradle

``` groovy
compile 'com.yydcdut.sdlv:sdlv:0.7.0'
```

## Jar

[下载](https://github.com/yydcdut/SlideAndDragListView/blob/master/jar/sdlv.jar?raw=true)

# 控件的使用

## 菜单的单击事件和 item 的滑动方向

### 步骤1

- 在 layout 的 xml 文件中添加 SlideAndDragListView

``` xml
<com.yydcdut.sdlv.SlideAndDragListView
        android:layout_width="match_parent"
        android:layout_height="match_parent">
</com.yydcdut.sdlv.SlideAndDragListView>
```

### 步骤2

- 创建 `Menu` 并添加 `MenuItem`

``` java
Menu menu = new Menu(true, 0);//第1个参数表示滑动 item 是否能滑的过头，像弹簧那样( true 表示过头，就像 Gif 中显示的那样；false 表示不过头，就像 Android QQ 中的那样)
menu.addItem(new MenuItem.Builder().setWidth(90)//单个菜单 button 的宽度
                .setBackground(new ColorDrawable(Color.RED))//设置菜单的背景
                .setText("One")//set text string
                .setTextColor(Color.GRAY)//set text color
                .setTextSize(20)//set text size
                .setIcon(getResources().getDrawable(R.drawable.ic_launcher))// set icon
                .build());
menu.addItem(new MenuItem.Builder().setWidth(120)
                .setBackground(new ColorDrawable(Color.BLACK))
                .setDirection(MenuItem.DIRECTION_RIGHT)//设置方向 (默认方向为 DIRECTION_LEFT )
                .setIcon(getResources().getDrawable(R.drawable.ic_launcher))// set icon
                .build());
//set in sdlv
listView.setMenu(menu);
```

类 `Menu` 的构造函数中的第一个参数表示滑动 item 是否能滑的过头，就像弹簧效果那样， true 表示过头，就像 Gif 中显示的那样；false 表示不过头

如果是`true`:

<img width="350" height="70" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/wannaOver_true.gif" />

如果是 `false`:

<img width="350" height="70" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/wannaOver_false.gif" />

第二个参数表示 ItemViewType 类型，也就是 `BaseAdapter` 中的 `int getItemViewType( int )`

### 步骤3

- 实现 menu item 的单击事件

``` java
slideAndDragListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0://One
                        return Menu.ITEM_SCROLL_BACK;
                }
                break;
            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0://icon
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                }
                break;
            default :
                return Menu.ITEM_NOTHING;
        }
        return Menu.ITEM_NOTHING;
    }
});
```

`Menu.ITEM_NOTHING`:

<img width="350" height="70" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/ITEM_NOTHING.gif" />

`Menu.ITEM_SCROLL_BACK`:

<img width="350" height="70" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/ITEM_SCROLL_BACK.gif" />

`Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP`:

<img width="350" height="70" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/ITEM_DELETE_FROM_BOTTOM_TO_TOP.gif" />

## 创建不同类型的 Menu

- 设置 adapter 中的 `ViewType`

``` java
private BaseAdapter mAdapter = new BaseAdapter() {
        // .......
        @Override
        public int getItemViewType(int position) {
            return position % 2;//current menu type
        }

        @Override
        public int getViewTypeCount() {
            return 2;//menu type count
        }
  		// ......
}
```

- 通过 adapter 中设置的来创建不同的 Menu

``` java
List<Menu> menuList = new ArrayList<>(2);
Menu menu0 = new Menu(new ColorDrawable(Color.WHITE), true, 0);
menu0.addItem(new MenuItem.Builder().setWidth(90)//set Width
                .setBackground(new ColorDrawable(Color.RED))// set background
                .setText("One")//set text string
                .setTextColor(Color.GRAY)//set text color
                .setTextSize(20)//set text size
                .build());
menu0.addItem(new MenuItem.Builder().setWidth(120)
                .setBackground(new ColorDrawable(Color.BLACK))
                .setDirection(MenuItem.DIRECTION_RIGHT)//set direction (default DIRECTION_LEFT)
                .setIcon(getResources().getDrawable(R.drawable.ic_launcher))// set icon
                .build());
Menu menu1 = new Menu(new ColorDrawable(Color.YELLOW), false, 1);
menu1.addItem(new MenuItem.Builder().setWidth(60)
                .setBackground(new ColorDrawable(Color.RED))
                .setText("Two")
                .setTextColor(Color.GRAY)
                .setTextSize(25)
                .build());
menu1.addItem(new MenuItem.Builder().setWidth(70)
                .setBackground(new ColorDrawable(Color.BLUE))
                .setText("Three")
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.BLACK)
                .setTextSize(20)
                .build());
menuList.add(menu0);
menuList.add(menu1);
listView.setMenu(menuList)
```

- Demo 效果

<img width="350" height="140" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/deferrentMenu.gif" />

## 拖放

``` java
ApplicationInfo mDraggedEntity;
List<ApplicationInfo> mDataList;

// ...init...

slideAndDragListView.setOnDragDropListener(new OnDragDropListener() {
    @Override
    public void onDragViewStart(int beginPosition) {
        mDraggedEntity = mDataList.get(beginPosition);
    }

    @Override
    public void onDragDropViewMoved(int fromPosition, int toPosition) {
		ApplicationInfo applicationInfo = mDataList.remove(fromPosition);
      	mDataList.add(toPosition, applicationInfo);
    }

    @Override
    public void onDragViewDown(int finalPosition) {
		mDataList.set(finalPosition, mDraggedEntity);
    }
});
```

`public void onDragViewStart(int position)`.参数 `position` 表示的是刚开始拖动的时候取的 item 在 ListView 中的位置

`public void onDragDropViewMoved(int fromPosition, int toPosition)` .参数 `fromPosition` 和 `toPosition` 表示从哪个位置拖动到哪个位置

`public void onDragViewDown(int position)` . 参数 `position` 表示的是拖动的 item 最放到了 ListView 的哪个位置

## 其他监听器

### Item 滑动监听器

``` java
slideAndDragListView.setOnSlideListener(new OnSlideListener() {
    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
        
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {

    }
});
```

### Item 删除监听器

``` java
slideAndDragListView.setOnItemDeleteListener(new OnItemDeleteListener() {
    @Override
    public void onItemDeleteAnimationFinished(View view, int position) {
        
    }
});
```

`public void onItemDelete(View view, int position)` 的调用是在 `int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction)` 返回 `Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP` 之后

### Item 回滚监听器

```java
slideAndDragListView.setOnItemScrollBackListener(new OnItemScrollBackListener() {
    @Override
    public void onScrollBackAnimationFinished(View view, int position) {

    }
});
```

`public void onScrollBackAnimationFinished(View view, int position)` 的调用是在 `int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction)` 返回 `Menu.ITEM_SCROLL_BACK` 之后

## API

### 关闭 Menu

```java
slideAndDragListView.closeSlidedItem();
```

调用 API 手动关闭 Menu

### 删除 Menu

```java
slideAndDragListView.deleteSlideItem();
```

调用 API 手动删除 Menu

### 拖放

```java
slideAndDragListView.startDrag(position);
```

调用 API 手动实施拖拽

###  不拖动 ViewType 类型的 Header 或 Footer

```java
slideAndDragListView.setNotDragHeaderCount(1);
slideAndDragListView.setNotDragFooterCount(1);
```

具体操作： [HeaderFooterViewTypeActivity.java](https://github.com/yydcdut/SlideAndDragListView/blob/master/app/src/main/java/com/yydcdut/demo/HeaderFooterViewTypeActivity.java)

# License

Copyright 2015 yydcdut

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.