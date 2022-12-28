package com.easemob.livedemo.data.model;

public class LiveRoomUrlBean extends BaseBean {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LiveRoomUrlBean{" +
                "data='" + data + '\'' +
                '}';
    }
}
