package com.easemob.livedemo.data.restapi;

import androidx.lifecycle.LiveData;

import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.model.LiveRoomUrlBean;
import com.easemob.livedemo.data.restapi.model.ResponseModule;
import java.util.List;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by wei on 2017/2/14.
 */

public interface ApiService {

    /**
     * 创建直播室
     * @param module 包括直播室名称，直播室描述，直播室房主，直播室封面，直播室最大人数
     * @return
     */
    @POST("liverooms")
    LiveData<LiveRoom> createLiveRoom(@Body LiveRoom module);

    /**
     * 获取直播室列表
     * @param limit 一次取的条数
     * @param cursor 在这个游标基础上取数据，首次获取传null
     * @return
     */
    @GET("liverooms")
    LiveData<ResponseModule<List<LiveRoom>>> getLiveRoomList(@Query("limit") int limit, @Query("cursor") String cursor);

    /**
     * 获取正在直播的直播室列表
     * @param limit 一次取的条数
     * @param cursor 在这个游标基础上取数据，首次获取传null
     * @return
     */
    @GET("liverooms/ongoing")
    LiveData<ResponseModule<List<LiveRoom>>> getLivingRoomList(@Query("limit") int limit, @Query("cursor") String cursor);

    /**
     * 更新直播室
     * @param roomId 直播室id
     * @param body 包括直播室名称(name)，直播室描述(description)，
     *             直播间成员最大数(maxusers)，直播间封面Url(page)，直播间自定义属性(ext，Map类型)
     * @return
     */
    @PUT("liverooms/{id}")
    LiveData<LiveRoom> updateLiveRoom(@Path("id") String roomId, @Body RequestBody body);

    /**
     * 改变直播状态
     * @param roomId
     * @param username
     * @param status
     * @return
     */
    @POST("liverooms/{liveroomid}/users/{username}/{status}")
    LiveData<LiveRoom> changeLiveStatus(@Path("liveroomid") String roomId,
                                        @Path("username") String username,
                                        @Path("status") String status);

    /**
     * 直播室详情
     * @param roomId 直播室id
     * @return
     */
    @GET("liverooms/{id}")
    LiveData<LiveRoom> getLiveRoomDetail(@Path("id") String roomId);

    /**
     * 获取推流地址
     * @param streamKey
     * @return
     */
    @GET("streams/url/publish/")
    LiveData<LiveRoomUrlBean> getLiveRoomPublishUrl(@Query("streamKey")String streamKey);

    /**
     * 获取播放地址
     * @param streamKey
     * @return
     */
    @GET("streams/url/play/")
    LiveData<LiveRoomUrlBean> getLiveRoomPlayUrl(@Query("streamKey")String streamKey);

}
