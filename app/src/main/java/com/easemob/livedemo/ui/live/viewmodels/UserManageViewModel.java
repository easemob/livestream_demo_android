package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import com.hyphenate.chat.EMChatRoom;

import java.util.List;

import com.easemob.livedemo.common.repository.ClientRepository;
import com.easemob.livedemo.common.repository.Resource;

public class UserManageViewModel extends AndroidViewModel {
    private final ClientRepository repository;
    private final MediatorLiveData<Resource<List<String>>> observable;
    private final MediatorLiveData<Resource<List<String>>> whitesObservable;
    private final MediatorLiveData<Resource<List<String>>> muteObservable;
    private final MediatorLiveData<Resource<EMChatRoom>> chatRoomObservable;
    private final MediatorLiveData<Resource<Boolean>> checkInWhiteObservable;

    public UserManageViewModel(@NonNull Application application) {
        super(application);
        repository = new ClientRepository();
        observable = new MediatorLiveData<>();
        whitesObservable = new MediatorLiveData<>();
        chatRoomObservable = new MediatorLiveData<>();
        muteObservable = new MediatorLiveData<>();
        checkInWhiteObservable = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<List<String>>> getObservable() {
        return observable;
    }

    public MediatorLiveData<Resource<List<String>>> getWhitesObservable() {
        return whitesObservable;
    }

    public void getWhiteList(String roomId) {
        whitesObservable.addSource(repository.getWhiteList(roomId), response -> whitesObservable.postValue(response));
    }

    public void getMembers(String roomId) {
        observable.addSource(repository.getMembers(roomId), response -> observable.postValue(response));
    }

    public MediatorLiveData<Resource<List<String>>> getMuteObservable() {
        return muteObservable;
    }

    public void getMuteList(String roomId) {
        muteObservable.addSource(repository.getMuteList(roomId), response -> muteObservable.postValue(response));
    }

    public MediatorLiveData<Resource<EMChatRoom>> getChatRoomObservable() {
        return chatRoomObservable;
    }

    public void addToChatRoomWhiteList(String chatRoomId, List<String> members) {
        chatRoomObservable.addSource(repository.addToChatRoomWhiteList(chatRoomId, members),
                response -> chatRoomObservable.postValue(response));
    }

    public void removeFromChatRoomWhiteList(String chatRoomId, List<String> members) {
        chatRoomObservable.addSource(repository.removeFromChatRoomWhiteList(chatRoomId, members),
                response -> chatRoomObservable.postValue(response));
    }

    public MediatorLiveData<Resource<Boolean>> getCheckInWhiteObservable() {
        return checkInWhiteObservable;
    }

    public void checkIfInGroupWhiteList(String username) {
        checkInWhiteObservable.addSource(repository.checkIfInGroupWhiteList(username),
                response -> checkInWhiteObservable.postValue(response));
    }

    public void muteChatRoomMembers(String chatRoomId, List<String> members, long duration) {
        chatRoomObservable.addSource(repository.MuteChatRoomMembers(chatRoomId, members, duration),
                response -> chatRoomObservable.postValue(response));
    }

    public void unMuteChatRoomMembers(String chatRoomId, List<String> members) {
        chatRoomObservable.addSource(repository.unMuteChatRoomMembers(chatRoomId, members),
                response -> chatRoomObservable.postValue(response));
    }


    public void muteAllMembers(String chatRoomId) {
        chatRoomObservable.addSource(repository.muteAllMembers(chatRoomId),
                response -> chatRoomObservable.postValue(response));
    }

    public void unMuteAllMembers(String chatRoomId) {
        chatRoomObservable.addSource(repository.unmuteAllMembers(chatRoomId),
                response -> chatRoomObservable.postValue(response));
    }

    public void banChatRoomMembers(String chatRoomId, List<String> members) {
        chatRoomObservable.addSource(repository.banChatRoomMembers(chatRoomId, members),
                response -> chatRoomObservable.postValue(response));
    }

    public void unbanChatRoomMembers(String chatRoomId, List<String> members) {
        chatRoomObservable.addSource(repository.unbanChatRoomMembers(chatRoomId, members),
                response -> chatRoomObservable.postValue(response));
    }

    public void addChatRoomAdmin(String chatRoomId, String member) {
        chatRoomObservable.addSource(repository.addChatRoomAdmin(chatRoomId, member),
                response -> chatRoomObservable.postValue(response));
    }

    public void removeChatRoomAdmin(String chatRoomId, String member) {
        chatRoomObservable.addSource(repository.removeChatRoomAdmin(chatRoomId, member),
                response -> chatRoomObservable.postValue(response));
    }

    public void fetchChatRoom(String chatRoomId) {
        chatRoomObservable.addSource(repository.fetchChatRoom(chatRoomId),
                response -> chatRoomObservable.postValue(response));
    }
}
