package com.easemob.qiniu_sdk;

public interface OnCallBack<T> {
    void onSuccess(T data);
    void onFail(String message);
}
