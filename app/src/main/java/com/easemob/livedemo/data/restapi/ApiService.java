package com.easemob.livedemo.data.restapi;

import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.model.User;
import java.util.List;
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
    @POST
    Call<RoomResponse> createLiveRoom(@Body LiveRoom module);

    @DELETE("{id}")
    Call<RoomResponse> deleteLiveRoom(@Path("id") String roomId);

    @PUT("{id}")
    Call updateLiveRoom(@Path("id") String roomId, @Body LiveRoom module);

    @POST("{id}/close")
    Call<RoomResponse> closeLiveRoom(@Path("id") String roomId);

    @GET
    Call<List<LiveRoom>> getLiveRoomList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);

    @GET("{id}")
    Call<LiveRoom> getLiveRoomDetails(@Path("id") String roomId);

    //=========================================================================================

    @POST("{id}/anchors")
    Call createAnchor(@Path("id") String roomId, @Body User user);

    @POST("{id}/admin/{IMUser}")
    Call grantAdmin(@Path("id") String roomId, @Path("IMUser") String userId);

    @DELETE("{id}/admin/{adminName}")
    Call revokeAdmin(@Path("id") String roomId, @Path("adminName") String adminName);

    @POST("{id}/anchors/{IMUser}")
    Call grantAnchor(@Path("id") String roomId, @Path("IMUser") String userId);

    @DELETE("{id}/anchors/{anchor}")
    Call revokeAnchor(@Path("id") String roomId, @Path("anchor") String userId);

    @DELETE("{id}/kick/{member}")
    Call kickMember(@Path("id") String roomId, @Path("member") String userId);

    //=========================================================================================


}
