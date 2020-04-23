package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import com.easemob.livedemo.common.reponsitories.AppServerRepository;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.easemob.livedemo.data.model.LiveRoom;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class StreamViewModel extends AndroidViewModel {
    private AppServerRepository repository;
    private MediatorLiveData<Resource<String>> publishUrlObservable;

    public StreamViewModel(@NonNull Application application) {
        super(application);
        repository = new AppServerRepository();
        publishUrlObservable = new MediatorLiveData<>();
    }

    public LiveData<Resource<String>> getPublishUrlObservable() {
        return publishUrlObservable;
    }

    public void getPublishUrl(String roomId) {
        publishUrlObservable.addSource(repository.getPublishUrl(roomId), response -> publishUrlObservable.postValue(response));
    }

}
