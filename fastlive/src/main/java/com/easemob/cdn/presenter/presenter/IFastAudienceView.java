package com.easemob.cdn.presenter.presenter;

public interface IFastAudienceView extends IBaseDataView {

    void onGetTokenSuccess(String token, int uid, boolean isRenew);

    void onGetTokenFail(String message);

    void onLiveClosed();

    void onLeaveChannel();
}
