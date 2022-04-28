package com.easemob.fastlive.presenter;

public abstract class FastHostPresenter extends FastTokenPresenter{
    public IFastHostView mView;

    @Override
    public void attachView(IBaseDataView view) {
        mView = (IFastHostView) view;
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

    public abstract void deleteRoom(String chatroomId);
}
