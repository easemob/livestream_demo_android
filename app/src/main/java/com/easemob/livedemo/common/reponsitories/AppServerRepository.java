package com.easemob.livedemo.common.reponsitories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.easemob.livedemo.BuildConfig;
import com.easemob.livedemo.data.model.AgoraTokenBean;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.model.LiveRoomUrlBean;
import com.easemob.livedemo.data.restapi.ApiService;
import com.easemob.livedemo.data.restapi.LiveManager;
import com.easemob.livedemo.data.restapi.model.ResponseModule;

import java.util.List;

import okhttp3.RequestBody;

/**
 * 用于从app server获取数据
 */
public class AppServerRepository {
    private ApiService apiService;

    public AppServerRepository() {
        apiService = LiveManager.getInstance().getApiService();
    }

    public LiveData<Resource<LiveRoom>> createLiveRoom(LiveRoom module) {
        return new NetworkOnlyResource<LiveRoom, LiveRoom>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<LiveRoom>> callBack) {
                callBack.onSuccess(apiService.createLiveRoom(module));
            }

        }.asLiveData();
    }

    public LiveData<Resource<ResponseModule<List<LiveRoom>>>> getLiveRoomList(int limit, String cursor) {
        return new NetworkOnlyResource<ResponseModule<List<LiveRoom>>, ResponseModule<List<LiveRoom>>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<ResponseModule<List<LiveRoom>>>> callBack) {
                callBack.onSuccess(apiService.getLiveRoomList(limit, cursor));
            }
        }.asLiveData();
    }

    public LiveData<Resource<ResponseModule<List<LiveRoom>>>> getLivingRoomLists(int limit, String cursor, String type) {
        return new NetworkOnlyResource<ResponseModule<List<LiveRoom>>, ResponseModule<List<LiveRoom>>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<ResponseModule<List<LiveRoom>>>> callBack) {
                callBack.onSuccess(apiService.getLivingRoomList(limit, cursor, type));
            }
        }.asLiveData();
    }

    public LiveData<Resource<LiveRoom>> changeLiveStatus(String roomId, String username, String status) {
        return new NetworkOnlyResource<LiveRoom, LiveRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<LiveRoom>> callBack) {
                callBack.onSuccess(apiService.changeLiveStatus(roomId, username, status));
            }
        }.asLiveData();
    }

    public LiveData<Resource<LiveRoom>> getLiveRoomDetails(String roomId) {
        return new NetworkOnlyResource<LiveRoom, LiveRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<LiveRoom>> callBack) {
                callBack.onSuccess(apiService.getLiveRoomDetail(roomId));
            }
        }.asLiveData();
    }

    public LiveData<Resource<LiveRoom>> updateLiveRoom(String roomId, RequestBody body) {
        return new NetworkOnlyResource<LiveRoom, LiveRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<LiveRoom>> callBack) {
                callBack.onSuccess(apiService.updateLiveRoom(roomId, body));
            }
        }.asLiveData();
    }

    public LiveData<Resource<LiveRoomUrlBean>> getPublishUrl(String roomId) {
        return new NetworkOnlyResource<LiveRoomUrlBean, LiveRoomUrlBean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<LiveRoomUrlBean>> callBack) {
                callBack.onSuccess(apiService.getLiveRoomPublishUrl(roomId));
            }
        }.asLiveData();
    }

    public LiveData<Resource<LiveRoomUrlBean>> getPlayUrl(String roomId) {
        return new NetworkOnlyResource<LiveRoomUrlBean, LiveRoomUrlBean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<LiveRoomUrlBean>> callBack) {
                callBack.onSuccess(apiService.getLiveRoomPlayUrl(roomId));
            }
        }.asLiveData();
    }

    public LiveData<Resource<AgoraTokenBean>> getAgoraToken(String userId, String channel, String hxAppkey, int uid) {
        return new NetworkOnlyResource<AgoraTokenBean, AgoraTokenBean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<AgoraTokenBean>> callBack) {
                callBack.onSuccess(apiService.getAgoraToken(userId, channel, hxAppkey, uid));
            }
        }.asLiveData();
    }
}
