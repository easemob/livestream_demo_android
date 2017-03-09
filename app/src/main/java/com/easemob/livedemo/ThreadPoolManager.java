package com.easemob.livedemo;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.easemob.livedemo.data.restapi.LiveException;
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

    public <T> void executeTask(final Task<T> task){
        executeRunnable(new Runnable() {
            @Override public void run() {
                final T t;
                try {
                    t = task.onRequest();
                    if(t != null) {
                        handler.post(new Runnable() {
                            @Override public void run() {
                                task.onSuccess(t);
                            }
                        });
                    }
                } catch (final LiveException e) {
                    handler.post(new Runnable() {
                        @Override public void run() {
                            task.onError(e);
                        }
                    });
                }

            }
        });
    }

    public interface Task<T> {
        /**
         * execute on background
         * @return
         * @throws LiveException
         */
        T onRequest() throws LiveException;

        /**
         * execute on ui thread
         * @param t
         */
        void onSuccess(T t);

        /**
         * execute on ui thread
         * @param exception
         */
        void onError(LiveException exception);
    }

}
