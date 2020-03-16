package com.easemob.livedemo.data.restapi;

import androidx.lifecycle.LiveData;

import com.easemob.livedemo.data.model.LiveRoom;
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

    @POST("liverooms")
    LiveData<LiveRoom> createLiveRoom(@Body LiveRoom module);

    @GET("liverooms")
    LiveData<ResponseModule<List<LiveRoom>>> getLiveRoomList(@Query("limit") int limit, @Query("cursor") String cursor);

    @GET("liverooms/ongoing")
    LiveData<ResponseModule<List<LiveRoom>>> getLivingRoomList(@Query("limit") int limit, @Query("cursor") String cursor);

    @PUT("liverooms/{id}")
    LiveData<LiveRoom> updateLiveRoom(@Path("id") String roomId, @Body RequestBody body);

    @POST("liverooms/{liveroomid}/users/{username}/{status}")
    LiveData<LiveRoom> changeLiveStatus(@Path("liveroomid") String roomId,
                                        @Path("username") String username,
                                        @Path("status") String status);

    @GET("liverooms/{id}")
    LiveData<LiveRoom> getLiveRoomDetail(@Path("id") String roomId);

}
