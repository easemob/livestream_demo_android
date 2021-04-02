package com.easemob.fastlive.presenter;

public interface IFastHostView extends IBaseDataView{
    void onGetTokenSuccess(String token, boolean isRenew);

    void onGetTokenFail(String message);

    void onStartBroadcast();

    void switchCamera();

    void onLeaveChannel();
}
