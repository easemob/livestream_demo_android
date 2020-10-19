package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;
import android.text.TextUtils;

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
        LiveData<Resource<LiveRoom>> liveData = null;
        if(TextUtils.isEmpty(localPath)) {
            LiveRoom liveRoom = getLiveRoom(name, description, null);
            liveData = repository.createLiveRoom(liveRoom);
        }else {
            liveData = Transformations.switchMap(emClientRepository.updateRoomCover(localPath), input -> {
                if (input.status == Status.ERROR) {
                    return new MutableLiveData<>(Resource.error(input.errorCode, input.getMessage(), null));
                } else if (input.status == Status.SUCCESS) {
                    LiveRoom liveRoom = getLiveRoom(name, description, input.data);
                    return repository.createLiveRoom(liveRoom);
                } else {
                    return new MutableLiveData<>(Resource.loading(null));
                }
            });
        }
        createObservable.addSource(liveData, response -> createObservable.postValue(response));
    }

    private LiveRoom getLiveRoom(String name, String description, String cover) {
        LiveRoom liveRoom = new LiveRoom();
        liveRoom.setName(name);
        liveRoom.setDescription(description);
        liveRoom.setOwner(EMClient.getInstance().getCurrentUser());
        liveRoom.setCover(cover);
        liveRoom.setMaxusers(200);
        //直播间默认是持续存在的，想要主播离开房间一段时间后销毁，需主动设置为false
        liveRoom.setPersistent(false);
        return liveRoom;
    }
}
