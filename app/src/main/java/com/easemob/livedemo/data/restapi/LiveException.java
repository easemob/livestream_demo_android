package com.easemob.livedemo.data.restapi;

import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by wei on 2017/2/15.
 */

public class LiveException extends HyphenateException{
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
