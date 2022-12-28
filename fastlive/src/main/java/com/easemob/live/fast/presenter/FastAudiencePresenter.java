package com.easemob.live.fast.presenter;

import android.util.Log;

public abstract class FastAudiencePresenter extends FastTokenPresenter {
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

    public abstract void onLiveClosed();

    public abstract void leaveChannel();
}
