package com.easemob.livedemo.common.repository;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.easemob.livedemo.common.utils.ThreadManager;
import com.easemob.livedemo.data.model.BaseBean;

/**
 * This class is used to pull asynchronous data from the Loop Signal SDK or other time-consuming operations
 *
 * @param <ResultType>
 */
public abstract class NetworkOnlyResource<ResultType, RequestType> {
    private static final String TAG = "NetworkBoundResource";
    private final ThreadManager mThreadManager;
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    public NetworkOnlyResource() {
        mThreadManager = ThreadManager.getInstance();
        if (mThreadManager.isMainThread()) {
            init();
        } else {
            mThreadManager.runOnMainThread(this::init);
        }
    }

    /**
     * work on main thread
     */
    private void init() {
        // notify ui load
        result.setValue(Resource.loading(null));
        fetchFromNetwork();
    }

    /**
     * work on main thread
     */
    private void fetchFromNetwork() {
        createCall(new ResultCallBack<LiveData<RequestType>>() {
            @Override
            public void onSuccess(LiveData<RequestType> apiResponse) {
                // run main thread
                mThreadManager.runOnMainThread(() -> {
                    result.addSource(apiResponse, response -> {
                        result.removeSource(apiResponse);
                        if (response != null) {
                            if (response instanceof BaseBean) {
                                int code = ((BaseBean) response).code;
                                if (code != ErrorCode.EM_NO_ERROR) {
                                    fetchFailed(code, ((BaseBean) response).message);
                                    return;
                                }
                            }
                            mThreadManager.runOnIOThread(() -> {
                                ResultType resultType = transformRequestType(response);
                                if (resultType == null) {
                                    resultType = transformDefault(response);
                                }
                                try {
                                    saveCallResult(processResponse(resultType));
                                } catch (Exception e) {
                                    Log.e(TAG, "save call result failed: " + e.toString());
                                }
                                result.postValue(Resource.success(resultType));
                            });

                        } else {
                            fetchFailed(ErrorCode.ERR_UNKNOWN, null);
                        }
                    });
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                mThreadManager.runOnMainThread(() -> {
                    fetchFailed(error, errorMsg);
                });
            }
        });


    }

    /**
     * default transform
     *
     * @param response
     * @return
     */
    @WorkerThread
    private ResultType transformDefault(RequestType response) {
        try {
            return (ResultType) response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * custom transform
     *
     * @param response
     * @return
     */
    @WorkerThread
    protected ResultType transformRequestType(RequestType response) {
        return null;
    }

    @MainThread
    private void fetchFailed(int code, String message) {
        onFetchFailed();
        result.setValue(Resource.error(code, message, null));
    }

    /**
     * Called to save the result of the API response into the database
     *
     * @param item
     */
    @WorkerThread
    protected void saveCallResult(ResultType item) {
    }

    /**
     * Process request response
     *
     * @param response
     * @return
     */
    @WorkerThread
    protected ResultType processResponse(ResultType response) {
        return response;
    }

    /**
     * This is designed as a callback pattern to facilitate asynchronous operations in this method
     *
     * @return
     */
    @MainThread
    protected abstract void createCall(@NonNull ResultCallBack<LiveData<RequestType>> callBack);

    /**
     * Called when the fetch fails. The child class may want to reset components like rate limiter.
     */
    protected void onFetchFailed() {
    }

    /**
     * Returns a LiveData object that represents the resource that's implemented
     * in the base class.
     *
     * @return
     */
    protected LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

}
