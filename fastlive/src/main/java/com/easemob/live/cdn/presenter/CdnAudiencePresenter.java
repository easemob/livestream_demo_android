package com.easemob.live.cdn.presenter;

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

    public abstract void onLiveClosed();

    public abstract void leaveChannel();
}
