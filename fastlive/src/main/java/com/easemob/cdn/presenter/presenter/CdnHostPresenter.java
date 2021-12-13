package com.easemob.cdn.presenter.presenter;

public abstract class CdnHostPresenter extends CdnTokenPresenter {
    public ICdnHostView mView;

    @Override
    public void attachView(IBaseDataView view) {
        mView = (ICdnHostView) view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachView();
    }

    /**
     * 开始推流
     */
    public abstract void onStartCamera();

    /**
     * 切换摄像头
     */
    public abstract void switchCamera();

    /**
     * 离开频道
     */
    public abstract void leaveChannel();
}
