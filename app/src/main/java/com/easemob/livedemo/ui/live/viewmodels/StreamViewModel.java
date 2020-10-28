package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import com.easemob.livedemo.common.reponsitories.AppServerRepository;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.model.LiveRoomUrlBean;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class StreamViewModel extends AndroidViewModel {
    private AppServerRepository repository;
    private MediatorLiveData<Resource<LiveRoomUrlBean>> publishUrlObservable;
    private MediatorLiveData<Resource<LiveRoomUrlBean>> newPublishUrlObservable;
    private MediatorLiveData<Resource<LiveRoomUrlBean>> playUrlObservable;

    public StreamViewModel(@NonNull Application application) {
        super(application);
        repository = new AppServerRepository();
        publishUrlObservable = new MediatorLiveData<>();
        newPublishUrlObservable = new MediatorLiveData<>();
        playUrlObservable = new MediatorLiveData<>();
    }

    public LiveData<Resource<LiveRoomUrlBean>> getPublishUrlObservable() {
        return publishUrlObservable;
    }

    public LiveData<Resource<LiveRoomUrlBean>> getNewPublishUrlObservable() {
        return newPublishUrlObservable;
    }

    public LiveData<Resource<LiveRoomUrlBean>> getPlayUrlObservable() {
        return playUrlObservable;
    }

    public void getPublishUrl(String roomId) {
        publishUrlObservable.addSource(repository.getPublishUrl(roomId), response -> publishUrlObservable.postValue(response));
    }

    public void getNewPublishUrl(String roomId) {
        newPublishUrlObservable.addSource(repository.getPublishUrl(roomId), response -> newPublishUrlObservable.postValue(response));
    }

    public void getPlayUrl(String roomId) {
        playUrlObservable.addSource(repository.getPlayUrl(roomId), response -> playUrlObservable.postValue(response));
    }

}
