package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.easemob.livedemo.common.reponsitories.AppServerRepository;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.easemob.livedemo.data.model.LiveRoom;
import com.hyphenate.chat.EMClient;

import retrofit2.http.Body;

public class CreateLiveViewmodel extends AndroidViewModel {
    private AppServerRepository repository;
    private MediatorLiveData<Resource<LiveRoom>> createObservable;

    public CreateLiveViewmodel(@NonNull Application application) {
        super(application);
        repository = new AppServerRepository();
        createObservable = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<LiveRoom>> getCreateObservable() {
        return createObservable;
    }

    public void createLiveRoom(String name, String description, String coverUrl) {
        LiveRoom liveRoom = new LiveRoom();
        liveRoom.setName(name);
        liveRoom.setDescription(description);
        liveRoom.setOwner(EMClient.getInstance().getCurrentUser());
        liveRoom.setCover(coverUrl);
        liveRoom.setMaxusers(200);
        createObservable.addSource(repository.createLiveRoom(liveRoom), response -> createObservable.postValue(response));
    }
}
