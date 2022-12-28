package com.easemob.livedemo.data.model;

import android.graphics.Bitmap;

public class HeadImageInfo {
    private String url;
    private String describe;
    private Bitmap bitmap;

    public HeadImageInfo(String url, String describe) {
        this.url = url;
        this.describe = describe;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "HeadImageInfo{" +
                "url='" + url + '\'' +
                ", describe='" + describe + '\'' +
                ", bitmap=" + bitmap +
                '}';
    }
}
