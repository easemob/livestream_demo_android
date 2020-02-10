package com.easemob.livedemo.data.restapi.model;

/**
 * Created by wei on 2017/3/27.
 */

public class LiveStatusModule {
    public LiveStatus status;

    public enum  LiveStatus{
        not_start,
        ongoing,
        completed,
        closed
    }
}

