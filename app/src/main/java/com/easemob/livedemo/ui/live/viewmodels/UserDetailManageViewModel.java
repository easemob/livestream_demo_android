package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import com.easemob.livedemo.common.reponsitories.EmClientRepository;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.hyphenate.chat.EMChatRoom;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

public class UserDetailManageViewModel extends AndroidViewModel {
    private EmClientRepository repository;
    private MediatorLiveData<Resource<EMChatRoom>> whiteObservable;
    private MediatorLiveData<Resource<EMChatRoom>> muteObservable;

    public UserDetailManageViewModel(@NonNull Application application) {
        super(application);
        repository = new EmClientRepository();
        whiteObservable = new MediatorLiveData<>();
        muteObservable = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<EMChatRoom>> getWhiteObservable() {
        return whiteObservable;
    }

    public MediatorLiveData<Resource<EMChatRoom>> getMuteObservable() {
        return muteObservable;
    }

    /**
     * 加入白名单
     * @param chatRoomId
     * @param members
     */
    public void addToChatRoomWhiteList(String chatRoomId, List<String> members) {
        whiteObservable.addSource(repository.addToChatRoomWhiteList(chatRoomId, members),
                response -> whiteObservable.postValue(response));
    }

    /**
     * 将用户从白名单中移除
     * @param chatRoomId
     * @param members
     */
    public void removeFromChatRoomWhiteList(String chatRoomId, List<String> members) {
        whiteObservable.addSource(repository.removeFromChatRoomWhiteList(chatRoomId, members),
                response -> whiteObservable.postValue(response));
    }

    /**
     * 禁止聊天室成员发言
     * @param chatRoomId
     * @param members
     * @param duration
     */
    public void muteChatRoomMembers(String chatRoomId, List<String> members, long duration) {
        muteObservable.addSource(repository.MuteChatRoomMembers(chatRoomId, members, duration),
                response -> muteObservable.postValue(response));
    }

    /**
     * 取消禁言
     * @param chatRoomId
     * @param members
     */
    public void unMuteChatRoomMembers(String chatRoomId, List<String> members) {
        muteObservable.addSource(repository.unMuteChatRoomMembers(chatRoomId, members),
                response -> muteObservable.postValue(response));
    }
}
