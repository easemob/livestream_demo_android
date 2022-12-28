package com.easemob.livedemo.data.model;

public class CdnUrlBean {
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private String data;

    @Override
    public String toString() {
        return "CdnUrlBean{" +
                "data='" + data + '\'' +
                '}';
    }
}
