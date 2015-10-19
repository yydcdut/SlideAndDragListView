# SlideAndDragListView

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
Menu menu = new Menu((int) getResources().getDimension(R.dimen.slv_item_height), new ColorDrawable(Color.WHITE), true);//第三个参数表示滑动item是否能滑的过量(true表示过量，就像Gif中显示的那样；false表示不过量，就像QQ中的那样)
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

### 步骤3

- 实现 menu item的单击事件

``` java
listView.setOnSlideListener(new SlideAndDragListView.OnSlideListener() {
            @Override
            public void onSlideOpen(View view, View parentView, int position, int direction) {

            }

            @Override
            public void onSlideClose(View view, View parentView, int position, int direction) {

            }
        });
listView.setOnMenuItemClickListener(new SlideAndDragListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
                switch (direction) {
                    case MenuItem.DIRECTION_LEFT:
                        switch (buttonPosition) {
                            case 0://One
                                return true;
                        }
                        break;
                    case MenuItem.DIRECTION_RIGHT:
                        switch (buttonPosition) {
                            case 0://icon
                                return false;
                        }
                        break;
                }
            }
        });
```

## 拖放

``` java
listView.setOnDragListener(new SlideAndDragListView.OnDragListener() {
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
listView.setOnListItemClickListener(new SlideAndDragListView.OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {

            }
        });
```

### Item长单击

``` java
listView.setOnListItemLongClickListener(new SlideAndDragListView.OnListItemLongClickListener() {
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

# 权限

``` xml
<uses-permission android:name="android.permission.VIBRATE"/>
```












