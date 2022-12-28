package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.easemob.livedemo.common.repository.ClientRepository;
import com.easemob.livedemo.common.repository.Resource;

public class UserInfoViewModel extends AndroidViewModel {
    private final ClientRepository clientRepository;
    private final MediatorLiveData<Resource<String>> uploadAvatarObservable;

    public UserInfoViewModel(@NonNull Application application) {
        super(application);
        clientRepository = new ClientRepository();
        uploadAvatarObservable = new MediatorLiveData<>();
    }

    public LiveData<Resource<String>> getUploadAvatarObservable() {
        return uploadAvatarObservable;
    }

    public void uploadAvatar(String path) {
        uploadAvatarObservable.addSource(clientRepository.updateAvatar(path),
                response -> uploadAvatarObservable.postValue(response));
    }

}
