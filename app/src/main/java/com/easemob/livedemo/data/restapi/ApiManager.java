package com.easemob.livedemo.data.restapi;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.easemob.livedemo.DemoApplication;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.restapi.model.ResponseModule;
import com.easemob.livedemo.data.restapi.model.StatisticsType;
import com.hyphenate.chat.EMClient;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
            throw new RuntimeException("must set the easemob appkey");
        }

        //HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequestInterceptor())
                //.addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://120.26.4.73:81/"+appkey+"/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        apiService = retrofit.create(ApiService.class);

    }


    static class RequestInterceptor implements Interceptor {

        @Override public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Authorization", "Bearer " + EMClient.getInstance().getAccessToken())
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build();
            okhttp3.Response response =  chain.proceed(request);
            return response;
        }
    }

    public static ApiManager get(){
        if(instance == null){
            instance = new ApiManager();
        }
        return instance;
    }


    public LiveRoom createLiveRoom(String name, String description, String coverUrl) throws LiveException {
        return createLiveRoomWithRequest(name, description, coverUrl, null);
    }

    public LiveRoom createLiveRoom(String name, String description, String coverUrl, String liveRoomId) throws LiveException {
        return createLiveRoomWithRequest(name, description, coverUrl, liveRoomId);
    }

    private LiveRoom createLiveRoomWithRequest(String name, String description, String coverUrl, String liveRoomId) throws LiveException {
        LiveRoom liveRoom = new LiveRoom();
        liveRoom.setName(name);
        liveRoom.setDescription(description);
        liveRoom.setAnchorId(EMClient.getInstance().getCurrentUser());
        liveRoom.setCover(coverUrl);

        Call<ResponseModule<LiveRoom>> responseCall;
        if(liveRoomId != null){
            responseCall = apiService.createLiveShow(liveRoomId, liveRoom);

        }else {
            responseCall = apiService.createLiveRoom(liveRoom);
        }
        ResponseModule<LiveRoom> response = handleResponseCall(responseCall).body();
        LiveRoom room = response.data;
        if(room.getId() != null) {
            liveRoom.setId(room.getId());
        }else {
            liveRoom.setId(liveRoomId);
        }
        liveRoom.setChatroomId(room.getChatroomId());
        //liveRoom.setAudienceNum(1);
        liveRoom.setLivePullUrl(room.getLivePullUrl());
        liveRoom.setLivePushUrl(room.getLivePushUrl());
        return liveRoom;
    }



    public void joinLiveRoom(String roomId, String userId) throws LiveException {
        JSONObject jobj = new JSONObject();
        String[] arr = new String[]{userId};
        JSONArray jarr = new JSONArray(Arrays.asList(arr));
        try {
            jobj.put("usernames", jarr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handleResponseCall(apiService.joinLiveRoom(roomId, jsonToRequestBody(jobj.toString())));
    }


    public void deleteLiveRoom(String roomId) throws LiveException {
        Call respCall = apiService.deleteLiveRoom(roomId);
        handleResponseCall(respCall);
    }

    public void updateLiveRoom(LiveRoom liveRoom) throws LiveException {
        Call respCall = apiService.updateLiveRoom(liveRoom.getId(), liveRoom);
        handleResponseCall(respCall);
    }

    public void terminateLiveRoom(String roomId) throws LiveException {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("status", "completed");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handleResponseCall(apiService.updateStatus(roomId, jsonToRequestBody(jobj.toString())));
    }

    public void closeLiveRoom(String roomId) throws LiveException {
        Call respCall = apiService.closeLiveRoom(roomId);
        handleResponseCall(respCall);
    }

    public List<LiveRoom> getLiveRoomList(int pageNum, int pageSize) throws LiveException {
        Call<ResponseModule<List<LiveRoom>>> respCall = apiService.getLiveRoomList(pageNum, pageSize);

        ResponseModule<List<LiveRoom>> response = handleResponseCall(respCall).body();
        return response.data;
    }

    public ResponseModule<List<LiveRoom>> getLivingRoomList(int limit, String cursor) throws LiveException {
        Call<ResponseModule<List<LiveRoom>>> respCall = apiService.getLivingRoomList(limit, cursor);

        ResponseModule<List<LiveRoom>> response = handleResponseCall(respCall).body();

        return response;
    }

    public LiveRoom getLiveRoomDetails(String roomId) throws LiveException {
        return handleResponseCall(apiService.getLiveRoomDetails(roomId)).body().data;
    }

    public List<String> getAssociatedRooms(String userId) throws LiveException {
        ResponseModule<List<String>> response = handleResponseCall(apiService.getAssociatedRoom(userId)).body();
        return response.data;
    }

    //public void grantLiveRoomAdmin(String roomId, String adminId) throws LiveException {
    //    GrantAdminModule module = new GrantAdminModule();
    //    module.newAdmin = adminId;
    //    handleResponseCall(apiService.grantAdmin(roomId, module));
    //}
    //
    //public void revokeLiveRoomAdmin(String roomId, String adminId) throws LiveException {
    //    handleResponseCall(apiService.revokeAdmin(roomId, adminId));
    //}
    //
    //public void grantLiveRoomAnchor(String roomId, String anchorId) throws LiveException {
    //    handleResponseCall(apiService.grantAnchor(roomId, anchorId));
    //}
    //
    //public void revokeLiveRoomAnchor(String roomId, String anchorId) throws LiveException {
    //    handleResponseCall(apiService.revokeAdmin(roomId, anchorId));
    //}
    //
    //public void kickLiveRoomMember(String roomId, String memberId) throws LiveException {
    //    handleResponseCall(apiService.kickMember(roomId, memberId));
    //}

    public void postStatistics(StatisticsType type, String roomId, int count) throws LiveException {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("type", type);
            jobj.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handleResponseCall(apiService.postStatistics(roomId, jsonToRequestBody(jobj.toString())));
    }

    public void postStatistics(StatisticsType type, String roomId, String username) throws LiveException {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("type", type);
            jobj.put("count", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handleResponseCall(apiService.postStatistics(roomId, jsonToRequestBody(jobj.toString())));
    }

    private <T> Response<T>handleResponseCall(Call<T> responseCall) throws LiveException{
        try {
            Response<T> response = responseCall.execute();
            if(!response.isSuccessful()){
                throw new LiveException(response.code(), response.errorBody().string());
            }
            return response;
        } catch (IOException e) {
            throw new LiveException(e.getMessage());
        }
    }

    private RequestBody jsonToRequestBody(String jsonStr){
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
    }
}
