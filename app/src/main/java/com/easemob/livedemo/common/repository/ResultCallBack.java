package com.easemob.livedemo.common.repository;


import com.hyphenate.EMValueCallBack;

public abstract class ResultCallBack<T> implements EMValueCallBack<T> {

    public void onError(int error) {
        onError(error, null);
    }
}
