package com.easemob.livedemo.data;

/**
 * Created by wei on 2017/2/15.
 */

public class LiveException extends Exception{
    protected int errorCode = -1;

    public LiveException(){}

    public LiveException(int errorCode, String desc){
        super(desc);
        this.errorCode = errorCode;
    }

    public LiveException(String message) {
        super(message);
    }


    public int getErrorCode() {
        return errorCode;
    }
}
