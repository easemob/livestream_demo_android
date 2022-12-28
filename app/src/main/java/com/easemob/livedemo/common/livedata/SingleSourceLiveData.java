package com.easemob.livedemo.common.livedata;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * Use LiveData when setting up and listening to a single data source
 * It is convenient to automatically cancel the listening of the previous data source when it is necessary to switch data sources
 *
 * @param <T> The type of data source that is listening
 */
public class SingleSourceLiveData<T> extends MutableLiveData<T> {
    private LiveData<T> lastSource;
    private T lastData;
    private final Observer<T> observer = new Observer<T>() {
        @Override
        public void onChanged(T t) {
            if (t != null && t == lastData) {
                return;
            }

            lastData = t;
            setValue(t);
        }
    };

    /**
     * Set up a data source that is unmonitored when there is a data source that has already been provisioned
     *
     * @param source
     */
    public void setSource(LiveData<T> source) {
        if (lastSource == source) {
            return;
        }

        if (lastSource != null) {
            lastSource.removeObserver(observer);
        }
        lastSource = source;

        if (hasActiveObservers()) {
            lastSource.observeForever(observer);
        }
    }

    @Override
    protected void onActive() {
        super.onActive();

        if (lastSource != null) {
            lastSource.observeForever(observer);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        if (lastSource != null) {
            lastSource.removeObserver(observer);
        }
    }
}
