package com.easemob.cdn.presenter;

import android.util.Log;

public abstract class CdnAudiencePresenter extends CdnTokenPresenter {
    public ICdnAudienceView mView;

    @Override
    public void attachView(IBaseDataView view) {
        mView = (ICdnAudienceView) view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "CdnAudiencePresenter onDestroy");
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
