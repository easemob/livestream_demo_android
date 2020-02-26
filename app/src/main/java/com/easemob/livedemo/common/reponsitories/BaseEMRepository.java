package com.easemob.livedemo.common.reponsitories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easemob.livedemo.common.ThreadManager;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMChatRoomManager;
import com.hyphenate.chat.EMClient;

public class BaseEMRepository {

    /**
     * return a new liveData
     * @param item
     * @param <T>
     * @return
     */
    public <T> LiveData<T> createLiveData(T item) {
        return new MutableLiveData<>(item);
    }

    /**
     * login before
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * 获取当前用户
     * @return
     */
    public String getCurrentUser() {
        return EMClient.getInstance().getCurrentUser();
    }

    /**
     * EMChatManager
     * @return
     */
    public EMChatManager getChatManager() {
        return EMClient.getInstance().chatManager();
    }

    /**
     * EMChatRoomManager
     * @return
     */
    public EMChatRoomManager getChatRoomManager() {
        return EMClient.getInstance().chatroomManager();
    }

    /**
     * 在主线程执行
     * @param runnable
     */
    public void runOnMainThread(Runnable runnable) {
        ThreadManager.getInstance().runOnMainThread(runnable);
    }

    /**
     * 在异步线程
     * @param runnable
     */
    public void runOnIOThread(Runnable runnable) {
        ThreadManager.getInstance().runOnIOThread(runnable);
    }

}
