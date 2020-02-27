package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import com.easemob.livedemo.common.reponsitories.AppServerRepository;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.easemob.livedemo.data.model.LiveRoom;

import okhttp3.RequestBody;

public class UpdateRoomViewmodel extends AndroidViewModel {
    private AppServerRepository repository;
    private MediatorLiveData<Resource<LiveRoom>> updateObservable;

    public UpdateRoomViewmodel(@NonNull Application application) {
        super(application);
        repository = new AppServerRepository();
        updateObservable = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<LiveRoom>> getUpdateObservable() {
        return updateObservable;
    }

    public void updateLiveRoom(String roomId, RequestBody body) {
        updateObservable.addSource(repository.updateLiveRoom(roomId, body), response -> updateObservable.postValue(response));
    }
}
