package com.easemob.livedemo.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.hyphenate.exceptions.HyphenateException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class ThreadPoolManager {
    private final ExecutorService executor;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private ThreadPoolManager() {
        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r) {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        super.run();
                    }
                };
            }
        });
    }

    private static final ThreadPoolManager POOL_MANAGER = new ThreadPoolManager();

    public static ThreadPoolManager getInstance() {
        return POOL_MANAGER;
    }

    public void executeRunnable(Runnable runnable) {
        executor.execute(runnable);
    }

    public <Result> void executeTask(final Task<Result> task) {
        executeRunnable(new Runnable() {
            @Override
            public void run() {
                final Result result;
                try {
                    result = task.onRequest();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            task.onSuccess(result);
                        }
                    });
                } catch (final HyphenateException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
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
         *
         * @return result
         * @throws HyphenateException exception
         */
        @WorkerThread
        Result onRequest() throws HyphenateException;

        /**
         * execute on ui thread
         *
         * @param result result
         */
        @UiThread
        void onSuccess(Result result);

        /**
         * execute on ui thread
         *
         * @param exception exception
         */
        @UiThread
        void onError(HyphenateException exception);
    }


}
