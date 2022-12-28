package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import com.hyphenate.chat.EMChatRoom;

import java.util.List;

import com.easemob.livedemo.common.repository.ClientRepository;
import com.easemob.livedemo.common.repository.Resource;

public class UserDetailManageViewModel extends AndroidViewModel {
    private final ClientRepository repository;
    private final MediatorLiveData<Resource<EMChatRoom>> whiteObservable;
    private final MediatorLiveData<Resource<EMChatRoom>> muteObservable;

    public UserDetailManageViewModel(@NonNull Application application) {
        super(application);
        repository = new ClientRepository();
        whiteObservable = new MediatorLiveData<>();
        muteObservable = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<EMChatRoom>> getWhiteObservable() {
        return whiteObservable;
    }

    public MediatorLiveData<Resource<EMChatRoom>> getMuteObservable() {
        return muteObservable;
    }

    public void addToChatRoomWhiteList(String chatRoomId, List<String> members) {
        whiteObservable.addSource(repository.addToChatRoomWhiteList(chatRoomId, members),
                response -> whiteObservable.postValue(response));
    }


    public void removeFromChatRoomWhiteList(String chatRoomId, List<String> members) {
        whiteObservable.addSource(repository.removeFromChatRoomWhiteList(chatRoomId, members),
                response -> whiteObservable.postValue(response));
    }


    public void muteChatRoomMembers(String chatRoomId, List<String> members, long duration) {
        muteObservable.addSource(repository.MuteChatRoomMembers(chatRoomId, members, duration),
                response -> muteObservable.postValue(response));
    }

    public void unMuteChatRoomMembers(String chatRoomId, List<String> members) {
        muteObservable.addSource(repository.unMuteChatRoomMembers(chatRoomId, members),
                response -> muteObservable.postValue(response));
    }
}
