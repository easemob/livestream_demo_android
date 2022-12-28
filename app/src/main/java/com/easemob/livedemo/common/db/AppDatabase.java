package com.easemob.livedemo.common.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.easemob.livedemo.common.db.converter.DateConverter;
import com.easemob.livedemo.common.db.dao.ReceiveGiftDao;
import com.easemob.livedemo.common.db.dao.UserDao;
import com.easemob.livedemo.common.db.entity.ReceiveGiftEntity;
import com.easemob.livedemo.common.db.entity.UserEntity;

@Database(entities = {ReceiveGiftEntity.class, UserEntity.class}, version = 4, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ReceiveGiftDao receiveGiftDao();

    public abstract UserDao userDao();

}
