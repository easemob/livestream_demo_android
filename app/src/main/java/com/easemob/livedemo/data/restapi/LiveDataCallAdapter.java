package com.easemob.livedemo.data.restapi;

import androidx.lifecycle.LiveData;

import com.easemob.livedemo.data.model.BaseBean;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import com.easemob.livedemo.common.repository.ErrorCode;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataCallAdapter<T> implements CallAdapter<T, LiveData<T>> {
    private final Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<T> adapt(Call<T> call) {
        return new LiveData<T>() {
            private AtomicBoolean started = new AtomicBoolean(false);

            @Override
            protected void onActive() {
                super.onActive();
                if (started.compareAndSet(false, true)) {
                    call.enqueue(new Callback<T>() {
                        @Override
                        public void onResponse(Call<T> call, Response<T> response) {
                            T body = response.body();
                            BaseBean result = null;
                            if (!response.isSuccessful()) {
                                if (body == null) {
                                    result = new BaseBean();
                                    setErrorInfo(response, result);
                                    try {
                                        body = (T) result;
                                    } catch (Exception e) {
                                        //未包裹ResponseModule的话，无法获取error
                                    }
                                } else {
                                    if (body instanceof BaseBean) {
                                        setErrorInfo(response, (BaseBean) body);
                                    }
                                }
                            }
                            postValue(body);
                        }

                        @Override
                        public void onFailure(Call<T> call, Throwable t) {
                            BaseBean result = new BaseBean();
                            result.code = ErrorCode.REQUEST_ERROR;
                            result.message = t.getMessage();
                            T body = null;
                            try {
                                body = (T) result;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            postValue(body);
                        }
                    });
                }
            }
        };
    }

    private void setErrorInfo(Response<T> response, BaseBean result) {
        try {
            result.code = response.code();
            result.message = response.errorBody().string();
        } catch (IOException e) {
            e.printStackTrace();
            result.code = ErrorCode.REQUEST_ERROR;
            result.message = e.getMessage();
        }
    }
}
