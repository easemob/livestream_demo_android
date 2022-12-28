package com.easemob.livedemo.common.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import com.easemob.livedemo.common.db.entity.UserEntity;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(UserEntity... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<UserEntity> users);

    @Query("select * from users where username = :arg0")
    LiveData<List<UserEntity>> loadUserById(String arg0);

    @Query("select * from users where username = :arg0")
    List<UserEntity> loadUserByUserId(String arg0);

    @Query("select contact from users where username = :arg0")
    int getUserContactById(String arg0);

    @Query("select * from users where contact = 0")
    LiveData<List<UserEntity>> loadUsers();

    @Query("select * from users where contact = 0")
    List<UserEntity> loadContacts();

    @Query("select * from users where contact = 1")
    LiveData<List<UserEntity>> loadBlackUsers();

    @Query("select * from users where contact = 1")
    List<UserEntity> loadBlackEaseUsers();

    @Query("select username from users")
    List<String> loadAllUsers();

    @Query("select username from users where contact = 0 or contact = 1")
    List<String> loadContactUsers();

    @Query("select * from users")
    List<UserEntity> loadAllEaseUsers();

    @Query("select * from users where contact = 0 or contact = 1")
    List<UserEntity> loadAllContactUsers();

    @Query("delete from users")
    int clearUsers();

    @Query("delete from users where contact = 1")
    int clearBlackUsers();

    @Query("delete from users where username = :arg0")
    int deleteUser(String arg0);

    @Query("update users set contact = :arg0  where username = :arg1")
    int updateContact(int arg0, String arg1);

    @Query("select username from users where lastModifyTimestamp + :arg0  <= :arg1")
    List<String> loadTimeOutEaseUsers(long arg0, long arg1);

    @Query("select username from users where lastModifyTimestamp + :arg0  <= :arg1 and contact = 1")
    List<String> loadTimeOutFriendUser(long arg0, long arg1);
}
