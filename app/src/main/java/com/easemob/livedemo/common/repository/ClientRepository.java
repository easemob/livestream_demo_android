package com.easemob.livedemo.common.repository;


import static com.hyphenate.cloud.HttpClientManager.Method_POST;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.easemob.livedemo.BuildConfig;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.utils.ThreadManager;
import com.easemob.livedemo.data.model.LoginBean;
import com.easemob.livedemo.data.model.User;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.cloud.EMCloudOperationCallback;
import com.hyphenate.cloud.EMHttpClient;
import com.hyphenate.cloud.HttpClientManager;
import com.hyphenate.cloud.HttpResponse;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRepository extends BaseEMRepository {

    /**
     * upload live stream cover
     *
     * @param localPath
     * @return
     */
    public LiveData<Resource<String>> updateRoomCover(String localPath) {
        return new NetworkOnlyResource<String, String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                runOnIOThread(() -> {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + EMClient.getInstance().getAccessToken());
                    String url = BuildConfig.BASE_URL + EMClient.getInstance().getOptions().getAppKey().replace("#", "/") + "/chatfiles";
                    EMHttpClient.getInstance().uploadFile(localPath, url, headers, new EMCloudOperationCallback() {
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
     * upload avatar
     *
     * @param localPath
     * @return
     */
    public LiveData<Resource<String>> updateAvatar(String localPath) {
        return new NetworkOnlyResource<String, String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                runOnIOThread(() -> {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + EMClient.getInstance().getAccessToken());
                    String url = BuildConfig.BASE_URL + EMClient.getInstance().getOptions().getAppKey().replace("#", "/") + "/chatfiles";
                    EMHttpClient.getInstance().uploadFile(localPath, url, headers, new EMCloudOperationCallback() {
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

    public LiveData<Resource<Boolean>> getVerificationCode(String phoneNumber){
        return new NetworkOnlyResource<Boolean, Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getVerificationCodeFromServe(phoneNumber, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }

    private void getVerificationCodeFromServe(String phoneNumber, EMCallBack callBack) {
        runOnIOThread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_SEND_SMS_FROM_SERVER + "/" + phoneNumber + "/" ;
                EMLog.d("getVerificationCodeFromServe url : ", url);
                HttpResponse response = HttpClientManager.httpExecute(url, headers, null, Method_POST);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    callBack.onSuccess();
                } else {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        String errorInfo = null;
                        try {
                            JSONObject object = new JSONObject(responseInfo);
                            errorInfo = object.getString("errorInfo");
                            if(errorInfo.contains("wait a moment while trying to send")) {
                                errorInfo = getContext().getString(R.string.login_error_send_code_later);
                            }else if(errorInfo.contains("exceed the limit of")) {
                                errorInfo = getContext().getString(R.string.login_error_send_code_limit);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorInfo = responseInfo;
                        }
                        callBack.onError(code, errorInfo);
                    }else {
                        callBack.onError(code, responseInfo);
                    }
                }
            } catch (Exception e) {
                callBack.onError(EMError.NETWORK_ERROR, e.getMessage());
            }
        });
    }

    public LiveData<Resource<String>> loginFromServe(String userName, String userPassword){
        return new NetworkOnlyResource<String, String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                LoginFromAppServe(userName, userPassword, new ResultCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }

    private void LoginFromAppServe(String userName,String userPassword ,ResultCallBack<String> callBack){
        runOnIOThread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                JSONObject request = new JSONObject();
                request.putOpt("phoneNumber", userName);
                request.putOpt("smsCode", userPassword);

                String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_BASE_USER + BuildConfig.APP_SERVER_LOGIN + "?appkey="+EMClient.getInstance().getOptions().getAppKey() ;
                EMLog.d("LoginToAppServer url : ", url);
                HttpResponse response = HttpClientManager.httpExecute(url, headers, request.toString(), Method_POST);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    JSONObject object = new JSONObject(responseInfo);
                    EMLog.d("LoginToAppServer success : ", responseInfo);
                    callBack.onSuccess(object.getString("token"));
                } else {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        String errorInfo = null;
                        try {
                            JSONObject object = new JSONObject(responseInfo);
                            errorInfo = object.getString("errorInfo");
                            if(errorInfo.contains("phone number illegal")) {
                                errorInfo = getContext().getString(R.string.login_phone_illegal);
                            }else if(errorInfo.contains("verification code error") || errorInfo.contains("send SMS to get mobile phone verification code")) {
                                errorInfo = getContext().getString(R.string.login_illegal_code);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorInfo = responseInfo;
                        }
                        callBack.onError(code, errorInfo);
                    }else {
                        callBack.onError(code, responseInfo);
                    }
                }
            } catch (Exception e) {
                callBack.onError(EMError.NETWORK_ERROR, e.getMessage());
            }
        });
    }

    /**
     * log in by id and nickname
     *
     * @param user
     * @return
     */
    public LiveData<Resource<User>> log(User user) {
        return new NetworkOnlyResource<User, User>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<User>> callBack) {
                EMClient.getInstance().login(user.getId(), user.getNickName(), new EMCallBack() {
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

    public LiveData<Resource<Boolean>> loginByAppServer(String username, String nickname) {
        return new NetworkOnlyResource<Boolean, Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                loginToAppServer(username, nickname, new ResultCallBack<LoginBean>() {
                    @Override
                    public void onSuccess(LoginBean value) {
                        if (value != null && !TextUtils.isEmpty(value.getAccessToken())) {
                            EMClient.getInstance().loginWithAgoraToken(username, value.getAccessToken(), new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    success(nickname, callBack);
                                }

                                @Override
                                public void onError(int code, String error) {
                                    callBack.onError(code, error);

                                }

                                @Override
                                public void onProgress(int progress, String status) {

                                }
                            });
                        } else {
                            callBack.onError(EMError.GENERAL_ERROR, "AccessToken is null!");
                        }

                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    private void loginToAppServer(String username, String nickname, ResultCallBack<LoginBean> callBack) {
        runOnIOThread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                JSONObject request = new JSONObject();
                request.putOpt("userAccount", username);
                request.putOpt("userNickname", nickname);

                String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_SERVER_URL;
                HttpResponse response = HttpClientManager.httpExecute(url, headers, request.toString(), Method_POST);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        JSONObject object = new JSONObject(responseInfo);
                        String token = object.getString("accessToken");
                        LoginBean bean = new LoginBean();
                        bean.setAccessToken(token);
                        bean.setUserNickname(nickname);
                        if (callBack != null) {
                            callBack.onSuccess(bean);
                        }
                    } else {
                        callBack.onError(code, responseInfo);
                    }
                } else {
                    callBack.onError(code, responseInfo);
                }
            } catch (Exception e) {
                //e.printStackTrace();
                callBack.onError(EMError.NETWORK_ERROR, e.getMessage());
            }
        });
    }

    private void success(String nickname, @NonNull ResultCallBack<LiveData<Boolean>> callBack) {
        // ** manually load all local groups and conversation
        callBack.onSuccess(createLiveData(true));
    }

    public LiveData<Resource<User>> login(User user) {
        return new NetworkOnlyResource<User, User>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<User>> callBack) {
                EMClient.getInstance().login(user.getId(), user.getPwd(), new EMCallBack() {
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

    public LiveData<Resource<User>> register(User user) {
        return new NetworkOnlyResource<User, User>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<User>> callBack) {
                ThreadManager.getInstance().runOnIOThread(() -> {
                    try {
                        EMClient.getInstance().createAccount(user.getId(), user.getPwd());
                        callBack.onSuccess(createLiveData(user));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                });
            }
        }.asLiveData();
    }


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

    public LiveData<Resource<EMChatRoom>> addToChatRoomWhiteList(String chatRoomId, List<String> members) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                ThreadManager.getInstance().runOnIOThread(() -> {
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

    public LiveData<Resource<EMChatRoom>> removeFromChatRoomWhiteList(String chatRoomId, List<String> members) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                ThreadManager.getInstance().runOnIOThread(() -> {
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

    public LiveData<Resource<List<String>>> getOnlyMembers(String chatRoomId) {
        return new NetworkOnlyResource<List<String>, List<String>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                ThreadManager.getInstance().runOnIOThread(() -> {
                    try {
                        EMChatRoom chatRoom = getChatRoomManager().fetchChatRoomFromServer(chatRoomId, true);
                        List<String> memberList = chatRoom.getMemberList();
                        callBack.onSuccess(createLiveData(memberList));
                    } catch (HyphenateException e) {
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                });
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<String>>> getMembers(String roomId) {
        return new NetworkOnlyResource<List<String>, List<String>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                ThreadManager.getInstance().runOnIOThread(() -> {
                    try {
                        EMChatRoom chatRoom = getChatRoomManager().fetchChatRoomFromServer(roomId, true);
                        List<String> allMembers = new ArrayList<>();
                        List<String> memberList = chatRoom.getMemberList();
                        allMembers.add(chatRoom.getOwner());
                        if (chatRoom.getAdminList() != null) {
                            allMembers.addAll(chatRoom.getAdminList());
                        }
                        if (memberList != null) {
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

    public LiveData<Resource<List<String>>> getMuteList(String roomId) {
        return new NetworkOnlyResource<List<String>, List<String>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                ThreadManager.getInstance().runOnIOThread(() -> {
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

    public LiveData<Resource<EMChatRoom>> addChatRoomAdmin(String chatRoomId, String username) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncAddChatRoomAdmin(chatRoomId, username, new EMValueCallBack<EMChatRoom>() {
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

    public LiveData<Resource<EMChatRoom>> removeChatRoomAdmin(String chatRoomId, String username) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncRemoveChatRoomAdmin(chatRoomId, username, new EMValueCallBack<EMChatRoom>() {
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

    public LiveData<Resource<EMChatRoom>> banChatRoomMembers(String chatRoomId, List<String> members) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncBlockChatroomMembers(chatRoomId, members, new EMValueCallBack<EMChatRoom>() {
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

    public LiveData<Resource<EMChatRoom>> unbanChatRoomMembers(String chatRoomId, List<String> members) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncUnBlockChatRoomMembers(chatRoomId, members, new EMValueCallBack<EMChatRoom>() {
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

    public LiveData<Resource<EMChatRoom>> fetchChatRoom(String chatRoomId) {
        return new NetworkOnlyResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncFetchChatRoomFromServer(chatRoomId, new EMValueCallBack<EMChatRoom>() {
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
