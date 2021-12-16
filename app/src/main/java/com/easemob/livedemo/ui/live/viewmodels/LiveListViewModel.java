package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.easemob.livedemo.common.reponsitories.AppServerRepository;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.restapi.model.ResponseModule;

import java.util.List;

public class LiveListViewModel extends AndroidViewModel {
    private AppServerRepository repository;
    private MediatorLiveData<Resource<ResponseModule<List<LiveRoom>>>> AllObservable;
    private MediatorLiveData<Resource<ResponseModule<List<LiveRoom>>>> livingRoomsObservable;
    private MediatorLiveData<Resource<ResponseModule<List<LiveRoom>>>> vodRoomsObservable;
    private MediatorLiveData<Resource<ResponseModule<List<LiveRoom>>>> fastRoomsObservable;
    private MediatorLiveData<Resource<ResponseModule<List<LiveRoom>>>> fastVodRoomsObservable;


    public LiveListViewModel(@NonNull Application application) {
        super(application);
        repository = new AppServerRepository();
        AllObservable = new MediatorLiveData<>();
        livingRoomsObservable = new MediatorLiveData<>();
        vodRoomsObservable = new MediatorLiveData<>();
        fastRoomsObservable = new MediatorLiveData<>();
        fastVodRoomsObservable = new MediatorLiveData<>();
    }

    public LiveData<Resource<ResponseModule<List<LiveRoom>>>> getAllObservable() {
        return AllObservable;
    }

    public void getLiveRoomList(int limit, String cursor) {
        AllObservable.addSource(repository.getLiveRoomList(limit, cursor), response -> AllObservable.postValue(response));
    }

    public MediatorLiveData<Resource<ResponseModule<List<LiveRoom>>>> getLivingRoomsObservable() {
        return livingRoomsObservable;
    }

    public void getLivingRoomList(int limit, String cursor) {
        livingRoomsObservable.addSource(repository.getLivingRoomLists(limit, cursor, LiveRoom.Type.live.name()), response -> livingRoomsObservable.postValue(response));
    }

    public MediatorLiveData<Resource<ResponseModule<List<LiveRoom>>>> getVodRoomsObservable() {
        return vodRoomsObservable;
    }

    public void getVodRoomList(int limit, String cursor) {
        vodRoomsObservable.addSource(repository.getLivingRoomLists(limit, cursor, LiveRoom.Type.vod.name()), response -> vodRoomsObservable.postValue(response));
    }

    public MediatorLiveData<Resource<ResponseModule<List<LiveRoom>>>> getFastRoomsObservable() {
        return fastRoomsObservable;
    }

    public void getFastRoomList(int limit, String cursor) {
        fastRoomsObservable.addSource(repository.getLivingRoomLists(limit, cursor, LiveRoom.Type.agora_speed_live.name()), response -> fastRoomsObservable.postValue(response));
    }

    public MediatorLiveData<Resource<ResponseModule<List<LiveRoom>>>> getFastVodRoomsObservable() {
        return fastVodRoomsObservable;
    }

    public void getFastVodRoomList(int limit, String cursor) {
        fastVodRoomsObservable.addSource(repository.getLivingRoomLists(limit, cursor, LiveRoom.Type.agora_vod.name()), response -> fastVodRoomsObservable.postValue(response));
    }

    public void getInteractionVodRoomList(int limit, String cursor) {
        fastVodRoomsObservable.addSource(repository.getLivingRoomLists(limit, cursor, LiveRoom.Type.agora_interaction_live.name()), response -> fastVodRoomsObservable.postValue(response));
    }

    public void getFastCdnRoomList(int limit, String cursor) {
        fastVodRoomsObservable.addSource(repository.getLivingRoomLists(limit, cursor, LiveRoom.Type.agora_cdn_live.name()), response -> fastVodRoomsObservable.postValue(response));
    }
}
