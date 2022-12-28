package com.easemob.live.fast.presenter;

public abstract class FastHostPresenter extends FastTokenPresenter {
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

    public abstract void onStartCamera();

    public abstract void switchCamera();

    public abstract void leaveChannel();

    public abstract void deleteRoom(String chatroomId);
}
