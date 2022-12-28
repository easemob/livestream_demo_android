package com.easemob.live.cdn.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public abstract class CdnBasePresenter implements LifecycleObserver {
    private static final String TAG = "FastLive";
    private boolean isDestroy;
    private Handler mMainThreadHandler;

    public abstract void attachView(IBaseDataView view);

    public abstract void detachView();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Log.i(TAG, this.toString() + " onCreate");
        isDestroy = false;
        mMainThreadHandler = new Handler(Looper.getMainLooper());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Log.i(TAG, this.toString() + " onStart");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Log.i(TAG, this.toString() + " onResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.i(TAG, this.toString() + " onPause");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Log.i(TAG, this.toString() + " onStop");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.i(TAG, this.toString() + " onDestroy");
        isDestroy = true;
    }

    public boolean isDestroy() {
        return isDestroy;
    }


    public boolean isActive() {
        return !isDestroy;
    }

    public void runOnUI(Runnable runnable) {
        mMainThreadHandler.post(runnable);
    }
}
