package com.easemob.livedemo.common.db.dao;

import com.easemob.livedemo.common.db.entity.ReceiveGiftEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ReceiveGiftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(ReceiveGiftEntity... entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<ReceiveGiftEntity> entities);

    @Query("select count(distinct `from`) from em_receive_gift")
    LiveData<Integer> loadSenders();

    @Query("select * from em_receive_gift order by timestamp desc")
    LiveData<List<ReceiveGiftEntity>> loadAll();

    @Query("select count(gift_num) from em_receive_gift")
    int loadGiftTotalNum();

    @Query("delete from em_receive_gift")
    int clearData();
}
