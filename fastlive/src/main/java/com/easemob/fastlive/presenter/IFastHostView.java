package com.easemob.fastlive.presenter;

public interface IFastHostView extends IBaseDataView{
    void onGetTokenSuccess(String token, int uid, boolean isRenew);

    void onGetTokenFail(String message);


    void onStartBroadcast();

    void switchCamera();

    void onLeaveChannel();
}
