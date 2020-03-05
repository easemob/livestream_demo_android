package com.easemob.livedemo.common.db;

import com.easemob.livedemo.common.db.converter.DateConverter;
import com.easemob.livedemo.common.db.dao.ReceiveGiftDao;
import com.easemob.livedemo.common.db.entity.ReceiveGiftEntity;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {ReceiveGiftEntity.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ReceiveGiftDao receiveGiftDao();

}
