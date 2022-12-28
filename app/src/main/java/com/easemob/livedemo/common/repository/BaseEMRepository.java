package com.easemob.livedemo.common.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easemob.livedemo.common.utils.ThreadManager;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMChatRoomManager;
import com.hyphenate.chat.EMClient;

import com.easemob.livedemo.DemoApplication;

public class BaseEMRepository {

    /**
     * return a new liveData
     *
     * @param item
     * @param <T>
     * @return
     */
    public <T> LiveData<T> createLiveData(T item) {
        return new MutableLiveData<>(item);
    }

    /**
     * login before
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * get current user
     *
     * @return
     */
    public String getCurrentUser() {
        return EMClient.getInstance().getCurrentUser();
    }

    /**
     * EMChatManager
     *
     * @return
     */
    public EMChatManager getChatManager() {
        return EMClient.getInstance().chatManager();
    }

    /**
     * EMChatRoomManager
     *
     * @return
     */
    public EMChatRoomManager getChatRoomManager() {
        return EMClient.getInstance().chatroomManager();
    }

    public void runOnMainThread(Runnable runnable) {
        ThreadManager.getInstance().runOnMainThread(runnable);
    }

    public void runOnIOThread(Runnable runnable) {
        ThreadManager.getInstance().runOnIOThread(runnable);
    }

    public Context getContext() {
        return DemoApplication.getInstance().getApplicationContext();
    }

}
