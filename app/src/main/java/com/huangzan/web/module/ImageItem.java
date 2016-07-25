package com.huangzan.web.module;

import android.graphics.drawable.BitmapDrawable;

/**
 * Created by huangzan on 16/7/18.
 */
public class ImageItem {
    private String name;

    private String date;

    private BitmapDrawable bitmapDrawable;

    public ImageItem() {
    }

    public ImageItem(String date, String name, BitmapDrawable bitmapDrawable) {
        this.date = date;
        this.name = name;
        this.bitmapDrawable = bitmapDrawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BitmapDrawable getBitmapDrawable() {
        return bitmapDrawable;
    }

    public void setBitmapDrawable(BitmapDrawable bitmapDrawable) {
        this.bitmapDrawable = bitmapDrawable;
    }
}
