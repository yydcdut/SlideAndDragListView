# SlideAndDragListView

  [ ![Download](https://api.bintray.com/packages/yydcdut/maven/sdlv/images/download.svg) ](https://bintray.com/yydcdut/maven/sdlv/_latestVersion)       [![License](http://img.shields.io/:license-apache-blue.svg)](LICENSE.txt)  [![Build Status](https://travis-ci.org/yydcdut/SlideAndDragListView.svg?branch=master)](https://travis-ci.org/yydcdut/SlideAndDragListView)    [![API](https://img.shields.io/badge/API-11%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=11)  <a href="http://www.methodscount.com/?lib=com.yydcdut.sdlv%3Asdlv%3A0.4.0"><img src="https://img.shields.io/badge/Methods count-287-e91e63.svg"></img></a>   <a href="http://www.methodscount.com/?lib=com.yydcdut.sdlv%3Asdlv%3A0.4.0"><img src="https://img.shields.io/badge/Size-29 KB-e91e63.svg"></img></a>  

一个可以左右滑动item和拖放item的ListView。

<img width="300" height="553" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/v1.1.gif" />

Demo: <a href="https://github.com/yydcdut/SlideAndDragListView/blob/master/apk/sdlv.apk?raw=true">下载</a>

# 简介

SlideAndDragListView (SDLV) 继承与ListView，SDLV可以向左或者向右滑动Item，并且可以拖放item达到排序的目的。

一些特点：

1. 清晰的拖放操作。
2. 在拖放的时候的直观和平滑滚动。
3. 支持item的单击和长单击事件。
4. 丰富的回调接口。
5. 滑动item的方向可以是向左、向右或者两者。
6. 等等......

SlideAndDragListView 用于各种优先级列表：收藏夹，播放列表，清单等。我希望你觉得它有请，同时，如果发现bug或者不人性化的地方，或者有什么建议，请麻烦告诉我或者帮助我！

# 引用



## Gradle

``` groovy
compile 'com.yydcdut.sdlv:sdlv:0.4.0@aar'
```

## aar

<a href="https://github.com/yydcdut/SlideAndDragListView/blob/master/aar/sdlv.aar?raw=true">下载</a>

## Jar

<a href="https://github.com/yydcdut/SlideAndDragListView/blob/master/jar/sdlv.jar?raw=true">下载</a>

# 控件的使用

## 菜单的单击事件和item的滑动方向

### 步骤1

- 在layout的xml文件中添加SlideAndDragListView

``` xml
<com.yydcdut.sdlv.SlideAndDragListView
        android:layout_width="fill_parent"
        android:layout_height="fill_parent">
</com.yydcdut.sdlv.SlideAndDragListView>
```

### 步骤2

- 创建`Menu`并添加`MenuItem`

``` java
Menu menu = new Menu((int) getResources().getDimension(R.dimen.slv_item_height), new ColorDrawable(Color.WHITE), true, 0);//第三个参数表示滑动item是否能滑的过量(true表示过量，就像Gif中显示的那样；false表示不过量，就像QQ中的那样)
menu.addItem(new MenuItem.Builder().setWidth(90)//单个菜单button的宽度
                .setBackground(new ColorDrawable(Color.RED))//设置菜单的背景
                .setText("One")//set text string
                .setTextColor(Color.GRAY)//set text color
                .setTextSize(20)//set text color
                .build());
menu.addItem(new MenuItem.Builder().setWidth(120)
                .setBackground(new ColorDrawable(Color.BLACK))
                .setDirection(MenuItem.DIRECTION_RIGHT)//设置方向 (默认方向为DIRECTION_LEFT)
                .setIcon(getResources().getDrawable(R.drawable.ic_launcher))// set icon
                .build());
//set in sdlv
listView.setMenu(menu);
```

类 `Menu` 的构造函数中的第三个参数表示滑动item是否能滑的过量(true表示过量，就像Gif中显示的那样；false表示不过量。

如果是`true`:

<img width="350" height="70" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/wannaOver_true.gif" />

如果是 `false`:

<img width="350" height="70" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/wannaOver_false.gif" />

第四个参数表示ItemViewType类型，也就是`BaseAdapter`中的`int getItemViewType(int )`。

### 步骤3

- 实现 menu item的单击事件

``` java
slideAndDragListView.setOnSlideListener(new SlideAndDragListView.OnSlideListener() {
            @Override
            public void onSlideOpen(View view, View parentView, int position, int direction) {

            }

            @Override
            public void onSlideClose(View view, View parentView, int position, int direction) {

            }
        });
slideAndDragListView.setOnMenuItemClickListener(new SlideAndDragListView.OnMenuItemClickListener() {
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
            }
        });
```

注意：必须得设置OnSlideListener监听器。

Menu.ITEM_NOTHING`:

<img width="350" height="70" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/ITEM_NOTHING.gif" />

`Menu.ITEM_SCROLL_BACK`:

<img width="350" height="70" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/ITEM_SCROLL_BACK.gif" />

`Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP`:

<img width="350" height="70" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/ITEM_DELETE_FROM_BOTTOM_TO_TOP.gif" />

## 创建不同类型的Menu

- 设置adapter中的ViewType

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

- 通过adapter中设置的来创建不同的Menu

``` java
List<Menu> menuList = new ArrayList<>(2);
Menu menu0 = new Menu(60, new ColorDrawable(Color.WHITE), true, 0);
menu0.addItem(new MenuItem.Builder().setWidth(90)//set Width
                .setBackground(new ColorDrawable(Color.RED))// set background
                .setText("One")//set text string
                .setTextColor(Color.GRAY)//set text color
                .setTextSize(20)//set text color
                .build());
menu0.addItem(new MenuItem.Builder().setWidth(120)
                .setBackground(new ColorDrawable(Color.BLACK))
                .setDirection(MenuItem.DIRECTION_RIGHT)//set direction (default DIRECTION_LEFT)
                .setIcon(getResources().getDrawable(R.drawable.ic_launcher))// set icon
                .build());
Menu menu1 = new Menu(80, new ColorDrawable(Color.YELLOW), false, 1);
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

- Demo效果

<img width="350" height="140" src="https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/deferrentMenu.gif" />

## 拖放

``` java
slideAndDragListView.setOnDragListener(new SlideAndDragListView.OnDragListener() {
            @Override
            public void onDragViewStart(int position) {

            }

            @Override
            public void onDragViewMoving(int position) {

            }

            @Override
            public void onDragViewDown(int position) {

            }
        }, mDataList);
```

`public void onDragViewStart(int position)`.参数 `position` 表示的是刚开始拖动的时候取的item在ListView中的位置。

`public void onDragViewMoving(int position)` .参数 `position` 表示的是当前拖动的item在ListView的位置，当处于拖动的时候这个函数是会一直回调的。

`public void onDragViewDown(int position)` . 参数 `position` i傲世的是拖动的item最放到了ListView的哪个位置。

## 其他

### Item单击

``` java
slideAndDragListView.setOnListItemClickListener(new SlideAndDragListView.OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {

            }
        });
```

### Item长单击

``` java
slideAndDragListView.setOnListItemLongClickListener(new SlideAndDragListView.OnListItemLongClickListener() {
            @Override
            public void onListItemLongClick(View view, int position) {

            }
        });
```

### Item滑动监听器

``` java
SlideAndDragListView.OnSlideListener() {
            @Override
            public void onSlideOpen(View view, View parentView, int position, int direction) {

            }

            @Override
            public void onSlideClose(View view, View parentView, int position, int direction) {

            }
        });
```

### Item删除监听器

``` java
slideAndDragListView.setOnItemDeleteListener(new SlideAndDragListView.OnItemDeleteListener() {
            @Override
            public void onItemDelete(View view, int position) {

            }
        });
```

`public void onItemDelete(View view, int position)` 的调用是在 `int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction)` 返回`Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP`之后.

# 权限

``` xml
<uses-permission android:name="android.permission.VIBRATE"/>
```