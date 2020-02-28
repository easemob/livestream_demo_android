package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.easemob.livedemo.common.enums.Status;
import com.easemob.livedemo.common.reponsitories.AppServerRepository;
import com.easemob.livedemo.common.reponsitories.EmClientRepository;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.easemob.livedemo.data.model.LiveRoom;
import com.hyphenate.chat.EMClient;

import retrofit2.http.Body;

public class CreateLiveViewModel extends AndroidViewModel {
    private AppServerRepository repository;
    private EmClientRepository emClientRepository;
    private MediatorLiveData<Resource<LiveRoom>> createObservable;

    public CreateLiveViewModel(@NonNull Application application) {
        super(application);
        repository = new AppServerRepository();
        emClientRepository = new EmClientRepository();
        createObservable = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<LiveRoom>> getCreateObservable() {
        return createObservable;
    }

    /**
     * 创建房间
     * @param name
     * @param description
     * @param localPath
     */
    public void createLiveRoom(String name, String description,String localPath) {
        LiveData<Resource<LiveRoom>> liveData = Transformations.switchMap(emClientRepository.updateRoomCover(localPath), input -> {
            if (input.status == Status.ERROR) {
                return new MutableLiveData<>(Resource.error(input.errorCode, input.getMessage(), null));
            } else if (input.status == Status.SUCCESS) {
                LiveRoom liveRoom = new LiveRoom();
                liveRoom.setName(name);
                liveRoom.setDescription(description);
                liveRoom.setOwner(EMClient.getInstance().getCurrentUser());
                liveRoom.setCover(input.data);
                liveRoom.setMaxusers(200);
                return repository.createLiveRoom(liveRoom);
            } else {
                return new MutableLiveData<>(Resource.loading(null));
            }
        });
        createObservable.addSource(liveData, response -> createObservable.postValue(response));
    }
}
