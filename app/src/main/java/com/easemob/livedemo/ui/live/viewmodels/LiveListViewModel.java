package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.easemob.livedemo.common.reponsitories.AppServerRepository;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.easemob.livedemo.data.model.LiveRoom;

import java.util.List;

public class LiveListViewModel extends AndroidViewModel {
    private AppServerRepository repository;
    private MediatorLiveData<Resource<List<LiveRoom>>> AllObservable;
    private MediatorLiveData<Resource<List<LiveRoom>>> livingRoomsObservable;


    public LiveListViewModel(@NonNull Application application) {
        super(application);
        repository = new AppServerRepository();
        AllObservable = new MediatorLiveData<>();
        livingRoomsObservable = new MediatorLiveData<>();
    }

    public LiveData<Resource<List<LiveRoom>>> getAllObservable() {
        return AllObservable;
    }

    public void getLiveRoomList(int limit) {
        AllObservable.addSource(repository.getLiveRoomList(limit, null), response -> AllObservable.postValue(response));
    }

    public MediatorLiveData<Resource<List<LiveRoom>>> getLivingRoomsObservable() {
        return livingRoomsObservable;
    }

    public void getLivingRoomLists(int limit) {
        livingRoomsObservable.addSource(repository.getLivingRoomLists(limit, null), response -> livingRoomsObservable.postValue(response));
    }
}
