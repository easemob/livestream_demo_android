package com.easemob.livedemo.ui.viewmodels;

import android.app.Application;

import com.easemob.livedemo.common.reponsitories.EmClientRepository;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.hyphenate.chat.EMChatRoom;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class UserManageViewModel extends AndroidViewModel {
    private EmClientRepository repository;
    private MediatorLiveData<Resource<List<String>>> observable;
    private MediatorLiveData<Resource<List<String>>> muteObservable;
    private MediatorLiveData<Resource<EMChatRoom>> chatRoomObservable;

    public UserManageViewModel(@NonNull Application application) {
        super(application);
        repository = new EmClientRepository();
        observable = new MediatorLiveData<>();
        chatRoomObservable = new MediatorLiveData<>();
        muteObservable = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<List<String>>> getObservable() {
        return observable;
    }

    /**
     * 获取白名单
     * @param roomId
     */
    public void getWhiteList(String roomId) {
        observable.addSource(repository.getWhiteList(roomId), response -> observable.postValue(response));
    }

    /**
     * 获取成员列表
     * @param roomId
     */
    public void getMembers(String roomId) {
        observable.addSource(repository.getMembers(roomId), response -> observable.postValue(response));
    }

    public MediatorLiveData<Resource<List<String>>> getMuteObservable() {
        return muteObservable;
    }

    /**
     * 获取禁言列表
     * @param roomId
     */
    public void getMuteList(String roomId) {
        muteObservable.addSource(repository.getMuteList(roomId), response -> muteObservable.postValue(response));
    }

    public MediatorLiveData<Resource<EMChatRoom>> getChatRoomObservable() {
        return chatRoomObservable;
    }

    /**
     * 加入白名单
     * @param chatRoomId
     * @param members
     */
    public void addToChatRoomWhiteList(String chatRoomId, List<String> members) {
        chatRoomObservable.addSource(repository.addToChatRoomWhiteList(chatRoomId, members),
                response -> chatRoomObservable.postValue(response));
    }

    /**
     * 将用户从白名单中移除
     * @param chatRoomId
     * @param members
     */
    public void removeFromChatRoomWhiteList(String chatRoomId, List<String> members) {
        chatRoomObservable.addSource(repository.removeFromChatRoomWhiteList(chatRoomId, members),
                response -> chatRoomObservable.postValue(response));
    }

    /**
     * 禁止聊天室成员发言
     * @param chatRoomId
     * @param members
     * @param duration
     */
    public void muteChatRoomMembers(String chatRoomId, List<String> members, long duration) {
        chatRoomObservable.addSource(repository.MuteChatRoomMembers(chatRoomId, members, duration),
                response -> chatRoomObservable.postValue(response));
    }

    /**
     * 取消禁言
     * @param chatRoomId
     * @param members
     */
    public void unMuteChatRoomMembers(String chatRoomId, List<String> members) {
        chatRoomObservable.addSource(repository.unMuteChatRoomMembers(chatRoomId, members),
                response -> chatRoomObservable.postValue(response));
    }

    /**
     * 一键禁言
     * @param chatRoomId
     */
    public void muteAllMembers(String chatRoomId) {
        chatRoomObservable.addSource(repository.muteAllMembers(chatRoomId),
                response -> chatRoomObservable.postValue(response));
    }

    /**
     * 一键解除禁言
     * @param chatRoomId
     */
    public void unMuteAllMembers(String chatRoomId) {
        chatRoomObservable.addSource(repository.unmuteAllMembers(chatRoomId),
                response -> chatRoomObservable.postValue(response));
    }
}
