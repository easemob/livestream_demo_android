package com.easemob.livedemo;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import com.hyphenate.exceptions.HyphenateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by wei on 2017/3/8.
 */

public class ThreadPoolManager {
    private ExecutorService executor;
    private Handler handler = new Handler(Looper.getMainLooper());

    private ThreadPoolManager(){
        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r){
                    @Override public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        super.run();
                    }
                };
                return thread;
            }
        });
    }

    private static final ThreadPoolManager POOL_MANAGER = new ThreadPoolManager();

    public static ThreadPoolManager getInstance(){
        return POOL_MANAGER;
    }

    public void executeRunnable(Runnable runnable){
        executor.execute(runnable);
    }

    public <Result> void executeTask(final Task<Result> task){
        executeRunnable(new Runnable() {
            @Override public void run() {
                final Result result;
                try {
                    result = task.onRequest();
                    //if(t != null) {
                        handler.post(new Runnable() {
                            @Override public void run() {
                                task.onSuccess(result);
                            }
                        });
                    //}
                } catch (final HyphenateException e) {
                    handler.post(new Runnable() {
                        @Override public void run() {
                            task.onError(e);
                        }
                    });
                }

            }
        });
    }

    public interface Task<Result> {
        /**
         * execute on background
         * @return
         * @throws HyphenateException
         */
        @WorkerThread
        Result onRequest() throws HyphenateException;

        /**
         * execute on ui thread
         * @param result
         */
        @UiThread
        void onSuccess(Result result);

        /**
         * execute on ui thread
         * @param exception
         */
        @UiThread
        void onError(HyphenateException exception);
    }


}
