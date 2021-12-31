package com.easemob.livedemo.ui.fast.presenter;


import com.easemob.fastlive.presenter.FastHostPresenter;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.model.AgoraTokenBean;
import com.easemob.livedemo.data.restapi.LiveException;
import com.easemob.livedemo.data.restapi.LiveManager;
import retrofit2.Response;

public class FastLiveHostPresenterImpl extends FastHostPresenter {
    @Override
    public void onStartCamera() {
        runOnUI(()-> {
            if(isActive()) {
                mView.onStartBroadcast();
            }
        });
    }

    @Override
    public void switchCamera() {
        runOnUI(()-> {
            if(isActive()) {
                mView.switchCamera();
            }
        });
    }

    @Override
    public void leaveChannel() {
        runOnUI(()-> {
            if(isActive()) {
                mView.onLeaveChannel();
            }
        });
    }

    @Override
    public void getFastToken(String hxId, String channel, String hxAppkey, int uid, boolean isRenew) {
        ThreadManager.getInstance().runOnIOThread(()-> {
            try {
                Response<AgoraTokenBean> response = LiveManager.getInstance().getAgoraToken(hxId, channel, hxAppkey, uid);
                runOnUI(()-> {
                    if(isActive()) {
                        mView.onGetTokenSuccess(response.body().getAccessToken(), response.body().getAgoraUserId(), isRenew);
                    }
                });
            } catch (LiveException e) {
                e.printStackTrace();
                runOnUI(()-> {
                    if(isActive()) {
                        mView.onGetTokenFail(e.getDescription());
                    }
                });
            }
        });
    }
}

