package com.easemob.live.cdn.presenter;

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
     * start push stream
     */
    public abstract void onStartCamera();

    /**
     * switch camera
     */
    public abstract void switchCamera();

    /**
     * leave channel
     */
    public abstract void leaveChannel();

    public abstract void deleteRoom(String roomId);
}
