package com.easemob.livedemo.data.restapi;

import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.model.User;
import com.easemob.livedemo.data.restapi.model.GrantAdminModule;
import com.easemob.livedemo.data.restapi.model.LiveStatusModule;
import com.easemob.livedemo.data.restapi.model.ResponseModule;
import java.util.List;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by wei on 2017/2/14.
 */

interface ApiService {
    @POST("liverooms?status=ongoing")
    Call<ResponseModule<LiveRoom>> createLiveRoom(@Body LiveRoom module);

    @PUT("liverooms/{id}")
    Call<ResponseModule> updateLiveRoom(@Path("id") String roomId, @Body RequestBody body);

    @POST("liverooms/{id}/close")
    Call<RoomResponse> closeLiveRoom(@Path("id") String roomId);

    @GET("liverooms")
    Call<ResponseModule<List<LiveRoom>>> getLiveRoomList(@Query("pagenum") int pageNum, @Query("pagesize") int pageSize);

    @GET("liverooms?ongoing=true")
    Call<ResponseModule<List<LiveRoom>>> getLivingRoomList(@Query("limit") int limit, @Query("cursor") String cursor);

    @GET("liverooms/{id}")
    Call<ResponseModule<LiveRoom>> getLiveRoomDetails(@Path("id") String roomId);

    @POST("liverooms/{id}/liveshows?status=ongoing")
    Call<ResponseModule<LiveRoom>> createLiveShow(@Path("id") String roomId, @Body LiveRoom module);

    @POST("liverooms/{id}/anchors")
    Call createAnchor(@Path("id") String roomId, @Body User user);

    @POST("liverooms/{id}/admin")
    Call grantAdmin(@Path("id") String roomId, @Body GrantAdminModule module);

    @DELETE("liverooms/{id}/admin/{adminName}")
    Call revokeAdmin(@Path("id") String roomId, @Path("adminName") String adminName);

    @POST("liverooms/{id}/anchors/{IMUser}")
    Call grantAnchor(@Path("id") String roomId, @Path("IMUser") String userId);

    @DELETE("liverooms/{id}/anchors/{anchor}")
    Call revokeAnchor(@Path("id") String roomId, @Path("anchor") String userId);

    @DELETE("liverooms/{id}/members/{member}")
    Call<ResponseModule> kickMember(@Path("id") String roomId, @Path("member") String userId);

    @POST("liverooms/{id}/members")
    Call<ResponseModule> joinLiveRoom(@Path("id") String roomId, @Body RequestBody body);

    @GET("liverooms/anchors/{username}/joined_liveroom_list")
    Call<ResponseModule<List<String>>> getAssociatedRoom(@Path("username") String username);

    @POST("liverooms/{id}/counters")
    Call<ResponseModule> postStatistics(@Path("id") String roomId, @Body RequestBody body);

    @PUT("liverooms/{id}/status")
    Call<ResponseModule> updateStatus(@Path("id") String roomId, @Body LiveStatusModule module);

    @GET("liverooms/{id}/status")
    Call<ResponseModule<LiveStatusModule>> getStatus(@Path("id") String roomId);


    //=========================================================================================



}
