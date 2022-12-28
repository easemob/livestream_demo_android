package com.easemob.livedemo.data.restapi;

import androidx.lifecycle.LiveData;

import com.easemob.livedemo.data.model.AgoraTokenBean;
import com.easemob.livedemo.data.model.CdnUrlBean;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.model.LiveRoomUrlBean;

import java.util.List;

import com.easemob.livedemo.data.restapi.model.ResponseModule;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    /**
     * create live room
     *
     * @param module Including the name of the live room, the description of the live room, the owner of the live room, the cover of the live room, and the maximum number of people in the live room
     * @return
     */
    @POST("appserver/liverooms")
    LiveData<LiveRoom> createLiveRoom(@Body LiveRoom module);


    @DELETE("appserver/liverooms/{id}")
    LiveData<LiveRoom> deleteLiveRoom(@Path("id") String roomId);

    /**
     * Get a list of live room
     *
     * @param limit  the number of list
     * @param cursor the cursor
     * @return
     */
    @GET("appserver/liverooms")
    LiveData<ResponseModule<List<LiveRoom>>> getLiveRoomList(@Query("limit") int limit, @Query("cursor") String cursor);

    /**
     * Get a list of living room
     *
     * @param limit  the number
     * @param cursor the cursor
     * @return
     */
    @GET("appserver/liverooms/ongoing")
    LiveData<ResponseModule<List<LiveRoom>>> getLivingRoomList(@Query("limit") int limit, @Query("cursor") String cursor, @Query("video_type") String videoType);

    /**
     * update live room
     *
     * @param roomId room id
     * @param body   Including live room name (name), live room description (description),
     *               * Maximum number of live room members (maxusers), live room cover Url (page), live room custom attributes (ext, Map type)
     * @return
     */
    @PUT("appserver/liverooms/{id}")
    LiveData<LiveRoom> updateLiveRoom(@Path("id") String roomId, @Body RequestBody body);

    /**
     * change live status
     *
     * @param roomId
     * @param username
     * @param status
     * @return
     */
    @POST("appserver/liverooms/{liveroomid}/users/{username}/{status}")
    LiveData<LiveRoom> changeLiveStatus(@Path("liveroomid") String roomId,
                                        @Path("username") String username,
                                        @Path("status") String status);

    /**
     * the detail of live room
     *
     * @param roomId room id
     * @return
     */
    @GET("appserver/liverooms/{id}")
    LiveData<LiveRoom> getLiveRoomDetail(@Path("id") String roomId);

    /**
     * get the url of publish
     *
     * @param streamKey
     * @return
     */
    @GET("appserver/streams/url/publish/")
    LiveData<LiveRoomUrlBean> getLiveRoomPublishUrl(@Query("streamKey") String streamKey);

    /**
     * get live room play url
     *
     * @param streamKey
     * @return
     */
    @GET("appserver/streams/url/play/")
    LiveData<LiveRoomUrlBean> getLiveRoomPlayUrl(@Query("streamKey") String streamKey);

    /**
     * get Agora Token
     *
     * @param userId  user id
     * @param channel channel name
     * @param appkey  app key
     * @return
     */
    @GET("token/liveToken/")
    LiveData<AgoraTokenBean> getAgoraToken(@Query("userAccount") String userId, @Query("channelName") String channel, @Query("appkey") String appkey, @Query("uid") int uid);

    /**
     * get Agora Token
     *
     * @param userId  user id
     * @param channel channel name
     * @param appkey  app key
     * @return
     */
    @GET("token/rtcToken/v1")
    Call<AgoraTokenBean> getAgoraTokenByHx(@Query("userAccount") String userId, @Query("channelName") String channel, @Query("appkey") String appkey);

    @GET("appserver/agora/cdn/streams/url/push")
    Call<CdnUrlBean> getCdnPushUrl(@Query("domain") String domain, @Query("pushPoint") String pushPoint, @Query("streamKey") String streamKey, @Query("expire") int expire);

    @GET("appserver/agora/cdn/streams/url/play")
    Call<CdnUrlBean> getCdnPullUrl(@Query("protocol") String protocol, @Query("domain") String domain, @Query("pushPoint") String pushPoint, @Query("streamKey") String streamKey);
}
