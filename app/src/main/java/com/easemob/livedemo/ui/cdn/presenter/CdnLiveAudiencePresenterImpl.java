package com.easemob.livedemo.ui.cdn.presenter;

import com.easemob.fastlive.presenter.FastAudiencePresenter;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.model.AgoraTokenBean;
import com.easemob.livedemo.data.restapi.LiveException;
import com.easemob.livedemo.data.restapi.LiveManager;

import retrofit2.Response;

public class CdnLiveAudiencePresenterImpl extends FastAudiencePresenter {
    @Override
    public void onLiveClosed() {
        runOnUI(()-> {
            if(isActive()) {
                mView.onLiveClosed();
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

