package com.easemob.livedemo.common.reponsitories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.easemob.livedemo.data.model.LiveRoom;

public class AppServerRepository extends BaseEMRepository {

    public LiveData<Resource<LiveRoom>> startLive(String roomId, String username) {
        return new NetworkOnlyResource<LiveRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<LiveRoom>> callBack) {

            }

        }.asLiveData();
    }
}
