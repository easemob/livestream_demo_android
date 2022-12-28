package com.easemob.livedemo.common.repository;


public class Result<T> {
    public int code;
    public T result;
    public String message;

    public Result() {
    }

    public Result(int code, T result) {
        this.code = code;
        this.result = result;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return code == ErrorCode.EM_NO_ERROR;
    }

}
