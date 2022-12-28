package com.easemob.live.cdn.presenter;

public interface ICdnAudienceView extends IBaseDataView {

    void onGetTokenSuccess(String token, int uid, boolean isRenew);

    void onGetTokenFail(String message);

    void onGetCdnUrlSuccess(String cdnUrl);

    void onGetCdnUrlFail(String msg);

    void onLiveClosed();

    void onLeaveChannel();
}
