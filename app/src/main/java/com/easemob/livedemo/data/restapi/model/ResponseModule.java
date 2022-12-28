package com.easemob.livedemo.data.restapi.model;

import com.easemob.livedemo.data.model.BaseBean;
import com.google.gson.annotations.SerializedName;

public class ResponseModule<T> extends BaseBean {
    @SerializedName("entities")
    public T data;
    public int count;
    public String cursor;
}
