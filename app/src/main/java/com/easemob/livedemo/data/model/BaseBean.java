package com.easemob.livedemo.data.model;

import java.io.Serializable;

public class BaseBean implements Serializable {
    public int code;
    public String message;

    @Override
    public String toString() {
        return "BaseBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
