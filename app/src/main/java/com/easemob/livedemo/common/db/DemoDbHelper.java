package com.easemob.livedemo.common.db;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.hyphenate.util.EMLog;

import com.easemob.livedemo.common.db.dao.ReceiveGiftDao;
import com.easemob.livedemo.common.db.dao.UserDao;

public class DemoDbHelper {
    private static final String TAG = DemoDbHelper.class.getSimpleName();
    private final static String DB_NAME = "live_stream";
    private static DemoDbHelper instance;
    private Context mContext;
    private AppDatabase mDatabase;
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    private DemoDbHelper(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static DemoDbHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DemoDbHelper.class) {
                if (instance == null) {
                    instance = new DemoDbHelper(context);
                }
            }
        }
        return instance;
    }

    /**
     * init db
     */
    public void initDb() {
        mDatabase = Room.databaseBuilder(mContext, AppDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        mIsDatabaseCreated.postValue(true);
    }

    private void closeDb() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public ReceiveGiftDao getReceiveGiftDao() {
        if (mDatabase != null) {
            return mDatabase.receiveGiftDao();
        }
        EMLog.i(TAG, "get ReceiveGiftDao failed, should init db first");
        return null;
    }

    public UserDao getUserDao() {
        if (mDatabase != null) {
            return mDatabase.userDao();
        }
        EMLog.i(TAG, "get userDao failed, should init db first");
        return null;
    }

}
