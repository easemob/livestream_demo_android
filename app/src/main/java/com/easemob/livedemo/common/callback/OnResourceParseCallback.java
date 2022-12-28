package com.easemob.livedemo.common.callback;

/**
 * Used to parse Sources <T>to reduce duplicate code
 * HideErrorMsg defaults to false, i.e. by default, an error message is displayed
 *
 * @param <T>
 */
public abstract class OnResourceParseCallback<T> {
    public boolean hideErrorMsg;

    public OnResourceParseCallback() {
    }

    public OnResourceParseCallback(boolean hideErrorMsg) {
        this.hideErrorMsg = hideErrorMsg;
    }


    public abstract void onSuccess(T data);


    public void onError(int code, String message) {
    }


    public void onLoading() {
    }

    public void hideLoading() {
    }
}
