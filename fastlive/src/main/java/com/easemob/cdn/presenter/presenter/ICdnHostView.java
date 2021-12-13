package com.easemob.cdn.presenter.presenter;

public interface ICdnHostView extends IBaseDataView {
    void onGetTokenSuccess(String token, int uid, boolean isRenew);

    void onGetTokenFail(String message);

    void onGetCdnUrlSuccess(String cdnUrl);

    void onGetCdnUrlFail(String msg);

    void onStartBroadcast();

    void switchCamera();

    void onLeaveChannel();
}
