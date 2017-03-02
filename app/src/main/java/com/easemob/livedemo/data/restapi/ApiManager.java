package com.easemob.livedemo.data.restapi;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.easemob.livedemo.DemoApplication;
import com.easemob.livedemo.data.LiveException;
import com.easemob.livedemo.data.model.LiveRoom;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wei on 2017/2/14.
 */

public class ApiManager {
    private String appkey;
    private ApiService apiService;

    private static  ApiManager instance;

    private ApiManager(){
        try {
            ApplicationInfo appInfo = DemoApplication.getInstance().getPackageManager().getApplicationInfo(
                    DemoApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            appkey = appInfo.metaData.getString("EASEMOB_APPKEY");
            appkey = appkey.replace("#","/");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://120.26.4.73:81/"+appkey+"/liverooms/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

    }

    public static ApiManager get(){
        if(instance == null){
            instance = new ApiManager();
        }
        return instance;
    }


    private Response handleResponseCall(Call responseCall) throws LiveException{
        try {
            Response response = responseCall.execute();
            if(!response.isSuccessful()){
                throw new LiveException(response.code(), response.errorBody().string());
            }
            return response;
        } catch (IOException e) {
            throw new LiveException(e.getMessage());
        }
    }

    public LiveRoom createLiveRoom(String name, String description) throws LiveException {
        LiveRoom liveRoom = new LiveRoom();
        Call<RoomResponse> responseCall = apiService.createLiveRoom(liveRoom);
        Response<RoomResponse> response = handleResponseCall(responseCall);
        RoomResponse roomResponse = response.body();
        liveRoom.setId(roomResponse.id);
        return liveRoom;
    }

    public void deleteLiveRoom(String roomId) throws LiveException {
        Call respCall = apiService.deleteLiveRoom(roomId);
        handleResponseCall(respCall);
    }

    public void updateLiveRoom(String roomId, LiveRoom liveRoom) throws LiveException {
        Call respCall = apiService.updateLiveRoom(roomId, liveRoom);
        handleResponseCall(respCall);
    }

    public void closeLiveRoom(String roomId) throws LiveException {
        Call respCall = apiService.closeLiveRoom(roomId);
        handleResponseCall(respCall);
    }

    public List<LiveRoom> getLiveRoomList(int pageNum, int pageSize) throws LiveException {
        Call<List<LiveRoom>> respCall = apiService.getLiveRoomList(pageNum, pageSize);
        Response<List<LiveRoom>> response = handleResponseCall(respCall);
        return response.body();
    }

    public LiveRoom fetchLiveRoom(String roomId) throws LiveException {
        Response<LiveRoom> response = handleResponseCall(apiService.getLiveRoomDetails(roomId));
        return response.body();
    }

    public void grantLiveRoomAdmin(String roomId, String adminId) throws LiveException {
        handleResponseCall(apiService.grantAdmin(roomId, adminId));
    }

    public void revokeLiveRoomAdmin(String roomId, String adminId) throws LiveException {
        handleResponseCall(apiService.revokeAdmin(roomId, adminId));
    }

    public void grantLiveRoomAnchor(String roomId, String anchorId) throws LiveException {
        handleResponseCall(apiService.grantAnchor(roomId, anchorId));
    }

    public void revokeLiveRoomAnchor(String roomId, String anchorId) throws LiveException {
        handleResponseCall(apiService.revokeAdmin(roomId, anchorId));
    }

    public void kickLiveRoomMember(String roomId, String memberId) throws LiveException {
        handleResponseCall(apiService.kickMember(roomId, memberId));
    }
}
