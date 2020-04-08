package com.easemob.livedemo.qiniu;

import android.content.Context;
import com.qiniu.pili.droid.streaming.StreamingEnv;

/**
 * 作为推流的帮助类
 */
public class PushStreamHelper {
    private static final String TAG = "PushStreamHelper";
    private static PushStreamHelper instance;

    private PushStreamHelper(){}

    public static PushStreamHelper getInstance() {
        if(instance == null) {
            synchronized (PushStreamHelper.class) {
                if(instance == null) {
                    instance = new PushStreamHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 根据推流官方文档，可以在application的onCreate()中进行初始化
     * @param context
     */
    public void init(Context context) {
        StreamingEnv.init(context.getApplicationContext());
    }

}
