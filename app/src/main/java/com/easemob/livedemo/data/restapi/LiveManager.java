package com.easemob.livedemo.data.restapi;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.easemob.livedemo.BuildConfig;
import com.easemob.livedemo.DemoApplication;
import com.easemob.livedemo.common.LoggerInterceptor;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.UserRepository;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.model.User;
import com.easemob.livedemo.data.restapi.model.LiveStatusModule;
import com.easemob.livedemo.data.restapi.model.ResponseModule;
import com.easemob.livedemo.data.restapi.model.StatisticsType;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.io.IOException;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wei on 2017/2/14.
 */

public class LiveManager {
    private String appkey;
    private ApiService apiService;

    private static LiveManager instance;

    private LiveManager(){
        try {
            ApplicationInfo appInfo = DemoApplication.getInstance().getPackageManager().getApplicationInfo(
                    DemoApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            appkey = appInfo.metaData.getString("EASEMOB_APPKEY");
            appkey = appkey.replace("#","/");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("must set the easemob appkey");
        }

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        LoggerInterceptor logger = new LoggerInterceptor("LoggerInterceptor", true, true);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequestInterceptor())
                .addInterceptor(logger)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(httpClient)
                .build();

        apiService = retrofit.create(ApiService.class);

    }


    static class RequestInterceptor implements Interceptor {

        @Override public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Log.e("TAG", "token = "+"Bearer " + EMClient.getInstance().getAccessToken());
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

    public static LiveManager getInstance(){
        if(instance == null){
            instance = new LiveManager();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    /**
     * 创建直播室
     * @param name 直播室名称
     * @param description 直播室描述
     * @param coverUrl 直播封面图片url
     * @return
     * @throws LiveException
     */
    public LiveRoom createLiveRoom(String name, String description, String coverUrl) throws LiveException {
        return createLiveRoomWithRequest(name, description, coverUrl, null);
    }

    /**
     * 根据指定的已经关联的直播室id创建直播
     * @param name 直播室名称
     * @param description 直播室描述
     * @param coverUrl 直播封面图片url
     * @param liveRoomId 要关联直播的直播室id
     * @return
     * @throws LiveException
     */
    public LiveRoom createLiveRoom(String name, String description, String coverUrl, String liveRoomId) throws LiveException {
        return createLiveRoomWithRequest(name, description, coverUrl, liveRoomId);
    }

    private LiveRoom createLiveRoomWithRequest(String name, String description, String coverUrl, String liveRoomId) throws LiveException {
        LiveRoom liveRoom = new LiveRoom();
        liveRoom.setName(name);
        liveRoom.setDescription(description);
        liveRoom.setOwner(EMClient.getInstance().getCurrentUser());
        liveRoom.setCover(coverUrl);

        Call<LiveRoom> responseCall;
        if(liveRoomId != null){
            responseCall = apiService.createLiveShow(liveRoomId, liveRoom);

        }else {
            liveRoom.setMaxusers(200);
            responseCall = apiService.createLiveRoom(liveRoom);
        }
//        if(room.getId() != null) {
//            liveRoom.setId(room.getId());
//        }else {
//            liveRoom.setId(liveRoomId);
//        }
//        liveRoom.setChatroomId(room.getChatroomId());
//        //liveRoom.setAudienceNum(1);
//        liveRoom.setLivePullUrl(room.getLivePullUrl());
//        liveRoom.setLivePushUrl(room.getLivePushUrl());
        return handleResponseCall(responseCall).body();
    }

    /**
     * 更新直播室封面
     * @param roomId
     * @param coverUrl
     * @throws LiveException
     */
    public LiveRoom updateLiveRoomCover(String roomId, String coverUrl) throws LiveException {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("cover", coverUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<LiveRoom> responseCall = apiService.updateLiveRoom(roomId, jsonToRequestBody(jobj.toString()));
        return handleResponseCall(responseCall).body();
    }



    //public void joinLiveRoom(String roomId, String userId) throws LiveException {
    //    JSONObject jobj = new JSONObject();
    //    String[] arr = new String[]{userId};
    //    JSONArray jarr = new JSONArray(Arrays.asList(arr));
    //    try {
    //        jobj.put("usernames", jarr);
    //    } catch (JSONException e) {
    //        e.printStackTrace();
    //    }
    //    handleResponseCall(apiService.joinLiveRoom(roomId, jsonToRequestBody(jobj.toString())));
    //}



    //public void updateLiveRoom(LiveRoom liveRoom) throws LiveException {
    //    Call respCall = apiService.updateLiveRoom(liveRoom.getId(), liveRoom);
    //    handleResponseCall(respCall);
    //}

    /**
     * 获取直播室直播状态
     * @param roomId
     * @return
     * @throws LiveException
     */
    public LiveStatusModule.LiveStatus getLiveRoomStatus(String roomId) throws LiveException {
        Call<ResponseModule<LiveStatusModule>> respCall = apiService.getStatus(roomId);
        return handleResponseCall(respCall).body().data.status;
    }

    /**
     * 结束直播
     * @param roomId
     * @throws LiveException
     */
    public void terminateLiveRoom(String roomId) throws LiveException {
        LiveStatusModule module = new LiveStatusModule();
        module.status = LiveStatusModule.LiveStatus.completed;
        handleResponseCall(apiService.updateStatus(roomId, module));
    }

    /**
     * 开始直播
     * @param roomId
     * @param username
     * @return
     * @throws LiveException
     */
    public LiveRoom startLive(String roomId, String username) throws LiveException {
        Call<LiveRoom> respCall = apiService.changeLiveStatus(roomId, username, "ongoing");
        return handleResponseCall(respCall).body();
    }

    /**
     * 结束直播
     * @param roomId
     * @param username
     * @throws LiveException
     */
    public void closeLiveRoom(String roomId, String username) throws LiveException {
        Call<LiveRoom> respCall = apiService.changeLiveStatus(roomId, username, "offline");
        handleResponseCall(respCall);
    }

    public ResponseModule<List<LiveRoom>> getLiveRoomList(int limit, String cursor) throws LiveException {
        Call<ResponseModule<List<LiveRoom>>> respCall = apiService.getLiveRoomList(limit, cursor);

        ResponseModule<List<LiveRoom>> response = handleResponseCall(respCall).body();
        return response;
    }

    /**
     * 获取正在直播的直播室列表
     * @param limit 取多少
     * @param cursor 在这个游标基础上取数据，首次获取传null
     * @return
     * @throws LiveException
     */
    public ResponseModule<List<LiveRoom>> getLivingRoomList(int limit, String cursor) throws LiveException {
        Call<ResponseModule<List<LiveRoom>>> respCall = apiService.getLivingRoomList(limit, cursor);

        ResponseModule<List<LiveRoom>> response = handleResponseCall(respCall).body();

        return response;
    }

    /**
     * 获取直播间详情
     * @param roomId
     * @return
     * @throws LiveException
     */
    public LiveRoom getLiveRoomDetails(String roomId) throws LiveException {
        return handleResponseCall(apiService.getLiveRoomDetails(roomId)).body();
    }

    /**
     * 获取用户已经关联的直播间
     * @param userId
     * @return
     * @throws LiveException
     */
    public List<String> getAssociatedRooms(String userId) throws LiveException {
        ResponseModule<List<String>> response = handleResponseCall(apiService.getAssociatedRoom(userId)).body();
        return response.data;
    }

    /**
     * 登录
     * @param user
     * @param callBack
     */
    public void login(User user, EMCallBack callBack) {
        ThreadManager.getInstance().runOnIOThread(()-> {
            EMClient.getInstance().login(user.getUsername(), UserRepository.getInstance().getDefaultPsw(), new EMCallBack() {
                @Override
                public void onSuccess() {
                    callBack.onSuccess();
                }

                @Override
                public void onError(int code, String error) {
                    if(code == EMError.USER_NOT_FOUND) {
                        register(user, callBack);
                    }else {
                        callBack.onError(code, error);
                    }
                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        });
    }

    /**
     * 注册并登录
     * @param user
     * @param callBack
     */
    public void register(User user, EMCallBack callBack) {
        ThreadManager.getInstance().runOnIOThread(()-> {
            try {
                EMClient.getInstance().createAccount(user.getUsername(), UserRepository.getInstance().getDefaultPsw());
                //如果注册成功，则直接进行登录
                login(user, callBack);
            } catch (HyphenateException e) {
                e.printStackTrace();
                if(callBack != null) {
                    callBack.onError(e.getErrorCode(), e.getMessage());
                }
            }
        });
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

    //public void postStatistics(StatisticsType type, String roomId, String username) throws LiveException {
    //    JSONObject jobj = new JSONObject();
    //    try {
    //        jobj.put("type", type);
    //        jobj.put("count", username);
    //    } catch (JSONException e) {
    //        e.printStackTrace();
    //    }
    //    handleResponseCall(apiService.postStatistics(roomId, jsonToRequestBody(jobj.toString())));
    //}

    private <T> Response<T> handleResponseCall(Call<T> responseCall) throws LiveException{
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
