package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.easemob.livedemo.common.repository.AppServerRepository;
import com.easemob.livedemo.common.repository.Resource;
import com.easemob.livedemo.data.model.LiveRoom;


public class LivingViewModel extends AndroidViewModel {
    private final AppServerRepository repository;
    private final MediatorLiveData<Resource<LiveRoom>> changeObservable;
    private final MediatorLiveData<Resource<LiveRoom>> roomDetailObservable;
    private final MediatorLiveData<Resource<LiveRoom>> closeObservable;
    private final MediatorLiveData<Resource<LiveRoom>> memberNumberObservable;

    public LivingViewModel(@NonNull Application application) {
        super(application);
        repository = new AppServerRepository();
        changeObservable = new MediatorLiveData<>();
        roomDetailObservable = new MediatorLiveData<>();
        closeObservable = new MediatorLiveData<>();
        memberNumberObservable = new MediatorLiveData<>();
    }

    public LiveData<Resource<LiveRoom>> getChangeObservable() {
        return changeObservable;
    }

    public void changeLiveStatus(String roomId, String username, String status) {
        changeObservable.addSource(repository.changeLiveStatus(roomId, username, status), response -> changeObservable.postValue(response));
    }

    public MediatorLiveData<Resource<LiveRoom>> getRoomDetailObservable() {
        return roomDetailObservable;
    }

    public void getLiveRoomDetails(String roomId) {
        roomDetailObservable.addSource(repository.getLiveRoomDetails(roomId), response -> roomDetailObservable.postValue(response));
    }

    public MediatorLiveData<Resource<LiveRoom>> getCloseObservable() {
        return closeObservable;
    }

    public void closeLive(String roomId, String username) {
        closeObservable.addSource(repository.changeLiveStatus(roomId, username, "offline"), response -> closeObservable.postValue(response));
    }

    public LiveData<Resource<LiveRoom>> getMemberNumberObservable() {
        return memberNumberObservable;
    }

    public void getRoomMemberNumber(String roomId) {
        memberNumberObservable.addSource(repository.getLiveRoomDetails(roomId), response -> memberNumberObservable.postValue(response));
    }
}
