package com.easemob.livedemo.data.restapi;

import android.util.Log;

import com.easemob.livedemo.BuildConfig;
import com.easemob.livedemo.common.LoggerInterceptor;
import com.easemob.livedemo.data.model.AgoraTokenBean;
import com.easemob.livedemo.data.restapi.model.ResponseModule;
import com.hyphenate.chat.EMClient;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wei on 2017/2/14.
 * 此类用户初始化相关网络请求类，具体接口调用，请通过viewmodel+livedata+repository实现
 */

public class LiveManager {
    private ApiService apiService;

    private static LiveManager instance;

    private LiveManager(){

        LoggerInterceptor logger = new LoggerInterceptor("LoggerInterceptor", true, true);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequestInterceptor())
                .addInterceptor(logger)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(httpClient)
                .build();

        apiService = retrofit.create(ApiService.class);

    }


    static class RequestInterceptor implements Interceptor {

        @Override public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Authorization", "Bearer " + EMClient.getInstance().getAccessToken())
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build();
            okhttp3.Response response =  chain.proceed(request);
            return response;
        }
    }

    public static LiveManager getInstance(){
        if(instance == null){
            instance = new LiveManager();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    public Response<AgoraTokenBean> getAgoraToken(String hxId, String channel, String hxAppkey, int uid) throws LiveException {
        return handleResponseCall(apiService.getAgoraTokenByHx(hxId, channel, hxAppkey, uid));
    }

    private <T> Response<T> handleResponseCall(Call<T> responseCall) throws LiveException{
        try {
            Response<T> response = responseCall.execute();
            if(!response.isSuccessful()){
                throw new LiveException(response.code(), response.errorBody().string());
            }
            return response;
        } catch (IOException e) {
            throw new LiveException(e.getMessage());
        }
    }

    private RequestBody jsonToRequestBody(String jsonStr){
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
    }
}
