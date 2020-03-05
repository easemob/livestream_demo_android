package com.easemob.livedemo.common.reponsitories;

import android.icu.util.LocaleData;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.UserRepository;
import com.easemob.livedemo.data.model.User;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.cloud.EMCloudOperationCallback;
//import com.hyphenate.cloud.HttpFileManager;
import com.hyphenate.cloud.EMHttpClient;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmClientRepository extends BaseEMRepository {

    /**
     * 上传直播间url
     * @param localPath
     * @return
     */
    public LiveData<Resource<String>> updateRoomCover(String localPath) {
        return new NetworkOnlyResource<String, String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                runOnIOThread(()-> {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + EMClient.getInstance().getAccessToken());
                    EMHttpClient.getInstance().uploadFile(localPath, null, headers, new EMCloudOperationCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject jsonObj = new JSONObject(result);
                                JSONObject entitys = jsonObj.getJSONArray("entities").getJSONObject(0);
                                String uuid = entitys.getString("uuid");
                                String url = jsonObj.getString("uri");
                                callBack.onSuccess(createLiveData(url + "/" + uuid));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String msg) {
                            callBack.onError(ErrorCode.UNKNOWN_ERROR, msg);
                        }

                        @Override
                        public void onProgress(int i) {

                        }
                    });
                });
            }
        }.asLiveData();

    }

    /**
     * 登录
     * @param user
     * @return
     */
    public LiveData<Resource<User>> Login(User user) {
        return new NetworkOnlyResource<User, User>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<User>> callBack) {
                EMClient.getInstance().login(user.getUsername(), UserRepository.getInstance().getDefaultPsw(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(user));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 注册
     * @param user
     * @return
     */
    public LiveData<Resource<User>> register(User user) {
        return new NetworkOnlyResource<User, User>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<User>> callBack) {
                ThreadManager.getInstance().runOnIOThread(()-> {
                    try {
                        EMClient.getInstance().createAccount(user.getUsername(), UserRepository.getInstance().getDefaultPsw());
                        callBack.onSuccess(createLiveData(user));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 获取白名单
     * @param roomId
     * @return
     */
    public LiveData<Resource<List<String>>> getWhiteList(String roomId) {
        return new NetworkOnlyResource<List<String>, List<String>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                getChatRoomManager().fetchChatRoomWhiteList(roomId, new EMValueCallBack<List<String>>() {
                    @Override
                    public void onSuccess(List<String> value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 将用户加入白名单
     * @param chatRoomId
     * @param members
     * @return
     */
    public LiveData<Resource<EMChatRoom>> addToChatRoomWhiteList(String chatRoomId, List<String> members) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                ThreadManager.getInstance().runOnIOThread(()-> {
                    getChatRoomManager().addToChatRoomWhiteList(chatRoomId, members, new EMValueCallBack<EMChatRoom>() {
                        @Override
                        public void onSuccess(EMChatRoom value) {
                            callBack.onSuccess(createLiveData(value));
                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                            callBack.onError(error, errorMsg);
                        }
                    });
                });
            }
        }.asLiveData();
    }

    /**
     * 将用户从白名单中移除
     * @param chatRoomId
     * @param members
     * @return
     */
    public LiveData<Resource<EMChatRoom>> removeFromChatRoomWhiteList(String chatRoomId, List<String> members) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                ThreadManager.getInstance().runOnIOThread(()-> {
                    getChatRoomManager().removeFromChatRoomWhiteList(chatRoomId, members, new EMValueCallBack<EMChatRoom>() {
                        @Override
                        public void onSuccess(EMChatRoom value) {
                            callBack.onSuccess(createLiveData(value));
                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                            callBack.onError(error, errorMsg);
                        }
                    });
                });
            }
        }.asLiveData();
    }

    /**
     * 检查是否在白名单中
     * @param username
     * @return
     */
    public LiveData<Resource<Boolean>> checkIfInGroupWhiteList(String username) {
        return new NetworkOnlyResource<Boolean, Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getChatRoomManager().checkIfInChatRoomWhiteList(username, new EMValueCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        callBack.onSuccess(createLiveData(aBoolean));
                    }

                    @Override
                    public void onError(int i, String s) {
                        callBack.onError(i, s);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 获取成员列表
     * @param roomId
     * @return
     */
    public LiveData<Resource<List<String>>> getMembers(String roomId) {
        return new NetworkOnlyResource<List<String>, List<String>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                ThreadManager.getInstance().runOnIOThread(()-> {
                    try {
                        EMChatRoom chatRoom = getChatRoomManager().fetchChatRoomFromServer(roomId, true);
                        List<String> allMembers = new ArrayList<>();
                        List<String> memberList = chatRoom.getMemberList();
                        allMembers.add(chatRoom.getOwner());
                        if(chatRoom.getAdminList() != null) {
                            allMembers.addAll(chatRoom.getAdminList());
                        }
                        if(memberList != null) {
                            allMembers.addAll(memberList);
                        }
                        callBack.onSuccess(createLiveData(allMembers));
                    } catch (HyphenateException e) {
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 获取禁言列表
     * @param roomId
     * @return
     */
    public LiveData<Resource<List<String>>> getMuteList(String roomId) {
        return new NetworkOnlyResource<List<String>, List<String>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                ThreadManager.getInstance().runOnIOThread(()-> {
                    try {
                        Map<String, Long> map = getChatRoomManager().fetchChatRoomMuteList(roomId, 1, 50);
                        callBack.onSuccess(createLiveData(new ArrayList<String>(map.keySet())));
                    } catch (HyphenateException e) {
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 禁止聊天室成员发言，需要聊天室拥有者或者管理员权限
     * @param chatRoomId
     * @return
     */
    public LiveData<Resource<EMChatRoom>> MuteChatRoomMembers(String chatRoomId, List<String> muteMembers, long duration) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncMuteChatRoomMembers(chatRoomId, muteMembers, duration, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 取消禁言，需要聊天室拥有者或者管理员权限
     * @param chatRoomId
     * @return
     */
    public LiveData<Resource<EMChatRoom>> unMuteChatRoomMembers(String chatRoomId, List<String> muteMembers) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncUnMuteChatRoomMembers(chatRoomId, muteMembers, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 一键禁言
     * @param chatRoomId
     * @return
     */
    public LiveData<Resource<EMChatRoom>> muteAllMembers(String chatRoomId) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().muteAllMembers(chatRoomId, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 一键解除禁言
     * @param chatRoomId
     * @return
     */
    public LiveData<Resource<EMChatRoom>> unmuteAllMembers(String chatRoomId) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().unmuteAllMembers(chatRoomId, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

}
