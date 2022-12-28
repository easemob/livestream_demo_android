package com.easemob.livedemo.common.inf;

import com.hyphenate.chat.EMUserInfo;

import java.util.Map;


public interface OnUpdateUserInfoListener {
    void onSuccess(Map<String, EMUserInfo> userInfoMap);

    void onError(int error, String errorMsg);
}
