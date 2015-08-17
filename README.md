# SlideAndDragListView

A ListView that you can slide the item or drag and drop the item to other positions.

![slide](https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/slide.gif)

![drag](https://raw.githubusercontent.com/yydcdut/SlideAndDragListView/master/gif/drag.gif)

# Overview

SlideAndDragListView (SDLV) is an extension of the Android ListView that enables slide and drag-and-drop reordering of list items.

 Some key features are:

1. Clean drag and drop.
2. Intuitive and smooth scrolling while dragging or sliding.
3. support onItemClick and onItemLongClick listener.
4. public callback methods
5. so on...

SlideAndDragListView is useful for all kinds of prioritized lists: favorites, playlists, checklists, etc. Would love to hear about your use case or app by email. I hope you find it useful; and please, help me improve the thing!

# Widget Usage

## XML usage

``` xml
 <com.yydcdut.sdlv.SlideAndDragListView
        xmlns:sdlv="http://schemas.android.com/apk/res-auto"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:divider="@android:color/black"
        android:dividerHeight="0.5dip"
        android:paddingLeft="8dip"
        android:paddingRight="8dip"
        sdlv:item_background="@android:color/white"
        sdlv:item_btn1_background="@drawable/btn1_drawable"
        sdlv:item_btn1_text="Delete1"
        sdlv:item_btn1_text_color="#00ff00"
        sdlv:item_btn2_background="@drawable/btn2_drawable"
        sdlv:item_btn2_text="Rename1"
        sdlv:item_btn2_text_color="#ff0000"
        sdlv:item_btn_number="2"
        sdlv:item_btn_width="70dip"
        sdlv:item_height="80dip">
    </com.yydcdut.sdlv.SlideAndDragListView>
```

## XML attributes

`item_background` - the background of sliding opening.

`item_btn1_background` - the background of the first button.

`item_btn1_text` - the text of the first button.

`item_btn1_text_color` - the color of the first button’s text.

`item_btn2_background` - the background of the second button.

`item_btn2_text` - the text of the second button.

`item_btn2_text_color` - the color of the second button’s text.

`item_btn_number` - the number of button you need, the max is 2 and the min is 0.

`item_btn_width` - the width of button.

`item_height` - the height of the item.

## Listeners

> SlideAndDragListView.OnListItemLongClickListener

``` java
sdlv.setOnListItemLongClickListener(new SlideAndDragListView.OnListItemLongClickListener() {
            @Override
            public void onListItemLongClick(View view, int position) {
                
            }
        });
```

`public void onListItemLongClick(View view, int position)` . The parameter `view` is the ListView item that is long clicked, and the parameter `position` is the position of the view in the list.

> SlideAndDragListView.OnListItemClickListener

``` java
sdlv.setOnListItemClickListener(new SlideAndDragListView.OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                
            }
        });
```

`public void onListItemClick(View view, int position)` . The parameter `view` is the ListView item that is clicked, and the parameter `position` is the position of the view in the list.

> SlideAndDragListView.OnDragListener

``` java
sdlv.setOnDragListener(new SlideAndDragListView.OnDragListener() {
            @Override
            public void onDragViewMoving(int position) {
                
            }

            @Override
            public void onDragViewDown(int position) {

            }
        });
```

`public void onDragViewMoving(int position)` .The parameter `position` is the position in ListView where dragged from, and this method will be called while the dragged item moving, as the same time, the position is changing.

`public void onDragViewDown(int position)` . The parameter `position` is the position in ListView where dropped down.

> SlideAndDragListView.OnSlideListener

``` java
sdlv.setOnSlideListener(new SlideAndDragListView.OnSlideListener() {
            @Override
            public void onSlideOpen(View view, int position) {
                
            }

            @Override
            public void onSlideClose(View view, int position) {

            }
        });
```

`public void onSlideOpen(View view, int position)`. The parameter `view` is the ListView item that is slide open, and the parameter `position` is the position of the view in the list.

`public void onSlideClose(View view, int position)`. The parameter `view` is the ListView item that is slide close, and the parameter `position` is the position of the view in the list.

> SlideAndDragListView.OnButtonClickListenerProxy

``` java
sdlv.setOnButtonClickListenerProxy(new SlideAndDragListView.OnButtonClickListenerProxy() {
            @Override
            public void onClick(View v, int position, int number) {
                
            }
        });
```

`public void onClick(View view, int position, int number)` . The parameter `view` is the button (when sliding open) in ListView item that is clicked, the parameter `position` is the position of the view in the list, and the parameter `number` represents which one is clicked. 

# License

Copyright 2014 yydcdut

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

