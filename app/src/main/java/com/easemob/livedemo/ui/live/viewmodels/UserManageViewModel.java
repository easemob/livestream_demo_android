package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import com.easemob.livedemo.common.livedata.SingleSourceLiveData;
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
    private final SingleSourceLiveData<Resource<EMChatRoom>> addAdminObservable;
    private final SingleSourceLiveData<Resource<EMChatRoom>> removeAdminObservable;
    private final SingleSourceLiveData<Resource<EMChatRoom>> muteSetObservable;
    private final SingleSourceLiveData<Resource<EMChatRoom>> unmuteSetObservable;
    private final SingleSourceLiveData<Resource<EMChatRoom>> addWhiteObservable;
    private final SingleSourceLiveData<Resource<EMChatRoom>> removeWhiteObservable;
    private final SingleSourceLiveData<Resource<EMChatRoom>> banSetObservable;
    private final SingleSourceLiveData<Resource<EMChatRoom>> unbanSetObservable;

    public UserManageViewModel(@NonNull Application application) {
        super(application);
        repository = new ClientRepository();
        observable = new MediatorLiveData<>();
        whitesObservable = new MediatorLiveData<>();
        chatRoomObservable = new MediatorLiveData<>();
        muteObservable = new MediatorLiveData<>();
        checkInWhiteObservable = new MediatorLiveData<>();
        addAdminObservable = new SingleSourceLiveData<>();
        removeAdminObservable = new SingleSourceLiveData<>();
        muteSetObservable = new SingleSourceLiveData<>();
        unmuteSetObservable = new SingleSourceLiveData<>();
        addWhiteObservable = new SingleSourceLiveData<>();
        removeWhiteObservable = new SingleSourceLiveData<>();
        banSetObservable = new SingleSourceLiveData<>();
        unbanSetObservable = new SingleSourceLiveData<>();
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

    public SingleSourceLiveData<Resource<EMChatRoom>> getUnbanSetObservable() {
        return unbanSetObservable;
    }

    public SingleSourceLiveData<Resource<EMChatRoom>> getBanSetObservable() {
        return banSetObservable;
    }

    public SingleSourceLiveData<Resource<EMChatRoom>> getRemoveWhiteObservable() {
        return removeWhiteObservable;
    }

    public SingleSourceLiveData<Resource<EMChatRoom>> getAddWhiteObservable() {
        return addWhiteObservable;
    }

    public SingleSourceLiveData<Resource<EMChatRoom>> getUnmuteSetObservable() {
        return unmuteSetObservable;
    }

    public SingleSourceLiveData<Resource<EMChatRoom>> getMuteSetObservable() {
        return muteSetObservable;
    }

    public SingleSourceLiveData<Resource<EMChatRoom>> getRemoveAdminObservable() {
        return removeAdminObservable;
    }

    public SingleSourceLiveData<Resource<EMChatRoom>> getAddAdminObservable() {
        return addAdminObservable;
    }

    public void addToChatRoomWhiteList(String chatRoomId, List<String> members) {
        addWhiteObservable.setSource(repository.addToChatRoomWhiteList(chatRoomId, members));
    }

    public void removeFromChatRoomWhiteList(String chatRoomId, List<String> members) {
        removeWhiteObservable.setSource(repository.removeFromChatRoomWhiteList(chatRoomId, members));
    }

    public MediatorLiveData<Resource<Boolean>> getCheckInWhiteObservable() {
        return checkInWhiteObservable;
    }

    public void checkIfInGroupWhiteList(String username) {
        checkInWhiteObservable.addSource(repository.checkIfInGroupWhiteList(username),
                response -> checkInWhiteObservable.postValue(response));
    }

    public void muteChatRoomMembers(String chatRoomId, List<String> members, long duration) {
        muteSetObservable.setSource(repository.MuteChatRoomMembers(chatRoomId, members, duration));
    }

    public void unMuteChatRoomMembers(String chatRoomId, List<String> members) {
        unmuteSetObservable.setSource(repository.unMuteChatRoomMembers(chatRoomId, members));
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
        banSetObservable.setSource(repository.banChatRoomMembers(chatRoomId, members));
    }

    public void unbanChatRoomMembers(String chatRoomId, List<String> members) {
        unbanSetObservable.setSource(repository.unbanChatRoomMembers(chatRoomId, members));
    }

    public void addChatRoomAdmin(String chatRoomId, String member) {
        addAdminObservable.setSource(repository.addChatRoomAdmin(chatRoomId, member));
    }

    public void removeChatRoomAdmin(String chatRoomId, String member) {
        removeAdminObservable.setSource(repository.removeChatRoomAdmin(chatRoomId, member));
    }

    public void fetchChatRoom(String chatRoomId) {
        chatRoomObservable.addSource(repository.fetchChatRoom(chatRoomId),
                response -> chatRoomObservable.postValue(response));
    }
}
