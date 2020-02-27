package com.easemob.livedemo.data.restapi.model;

import com.easemob.livedemo.data.model.BaseBean;
import com.google.gson.annotations.SerializedName;

/**
 * Created by wei on 2017/3/8.
 */

public class ResponseModule<T> extends BaseBean {
    @SerializedName("entities")
    public T data;
    public String cursor;
}
