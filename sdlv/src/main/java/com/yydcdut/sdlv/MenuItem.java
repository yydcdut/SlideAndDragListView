package com.yydcdut.sdlv;

import android.graphics.drawable.Drawable;

/**
 * Created by yuyidong on 15/10/8.
 */
public class MenuItem {
    public static final int DERACTION_LEFT = 1;
    public static final int DERACTION_RIGHT = -1;

    public final int width;
    public final String text;
    public final int textSize;
    public final int textColor;
    public final Drawable icon;
    public final Drawable background;
    public final int direction;

    public MenuItem(int width, String text, int textSize, int textColor, Drawable icon, Drawable background, int direction) {
        this.width = width;
        this.text = text;
        this.textSize = textSize;
        this.textColor = textColor;
        this.icon = icon;
        this.background = background;
        this.direction = direction;
    }

    public static class Builder {
        //todo 默认值
        private int width;
        private String text;
        private int textSize;
        private int textColor;
        private Drawable icon;
        private Drawable background;
        private int direction = 1;

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder setIcon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder setBackground(Drawable background) {
            this.background = background;
            return this;
        }

        public Builder setDirection(int direction) {
            this.direction = direction;
            return this;
        }

        public int getWidth() {
            return width;
        }

        public String getText() {
            return text;
        }

        public int getTextSize() {
            return textSize;
        }

        public int getTextColor() {
            return textColor;
        }

        public Drawable getIcon() {
            return icon;
        }

        public Drawable getBackground() {
            return background;
        }

        public int getDirection() {
            return direction;
        }

        public MenuItem build() {
            return new MenuItem(width, text, textSize, textColor, icon, background, direction);
        }
    }

}
