package com.easemob.livedemo.common.reponsitories;

import android.icu.util.LocaleData;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.UserRepository;
import com.easemob.livedemo.data.model.User;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.cloud.EMCloudOperationCallback;
import com.hyphenate.cloud.HttpFileManager;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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
                    new HttpFileManager().uploadFile(localPath, "", "", "", headers, new EMCloudOperationCallback() {

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
                        public void onProgress(int progress) {

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
}
