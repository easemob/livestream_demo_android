package com.easemob.fastlive.presenter;

import android.util.Log;

public abstract class FastAudiencePresenter extends FastTokenPresenter{
    public IFastAudienceView mView;

    @Override
    public void attachView(IBaseDataView view) {
        mView = (IFastAudienceView) view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "FastAudiencePresenter onDestroy");
        detachView();
    }

    /**
     * 直播关闭
     */
    public abstract void onLiveClosed();

    /**
     * 离开频道
     */
    public abstract void leaveChannel();
}
