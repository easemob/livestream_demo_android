package com.easemob.livedemo.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.restapi.LiveManager;
import com.easemob.livedemo.data.restapi.LiveException;
import com.hyphenate.chat.EMClient;
import com.hyphenate.cloud.EMCloudOperationCallback;
import com.hyphenate.cloud.HttpFileManager;
import com.hyphenate.exceptions.HyphenateException;
import com.weigan.loopview.LoopView;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class AssociateLiveRoomActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICK = 1;
    private static final int REQUEST_CODE_CUTTING = 2;

    @BindView(R.id.loop_view) LoopView loopView;
    @BindView(R.id.layout_select_live_id) LinearLayout selectLiveIdLayout;
    @BindView(R.id.txt_live_id_value) TextView liveIdView;
    @BindView(R.id.btn_start_live) Button startButton;
    @BindView(R.id.layout_input_text) LinearLayout inputLayout;
    @BindView(R.id.edt_live_name) EditText liveNameView;
    @BindView(R.id.edt_live_desc) EditText liveDescView;
    @BindView(R.id.img_live_cover) ImageView liveCoverView;
    @BindView(R.id.txt_cover_hint) TextView hintView;


    private List<String> liveIds;

    private String coverPath;
    private File cacheFile;

    private String selectedLiveId;

    private LiveRoom currentLiveRoom;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associate_live_room);
        ButterKnife.bind(this);

        loopView.setTextSize(18);

        executeTask(new ThreadPoolManager.Task<List<String>>() {
            @Override public List<String> onRequest() throws HyphenateException {
                return LiveManager.getInstance().getAssociatedRooms(EMClient.getInstance().getCurrentUser());
            }

            @Override public void onSuccess(List<String> list) {
                liveIds = list;
                if(liveIds.size() > 0) {
                    loopView.setNotLoop();
                    loopView.setItems(liveIds);
                    loopView.setCurrentPosition(Math.round(liveIds.size() / 2));
                }
            }

            @Override public void onError(HyphenateException exception) {

            }
        });
    }

    @OnClick(R.id.txt_cancel) void cancel() {
        if(isSelectLayoutShowed) {
            dismissSelectLayout();
            isSelectLayoutShowed = false;
        }
    }

    @OnClick(R.id.txt_save) void save() {
        if(isSelectLayoutShowed) {
            dismissSelectLayout();
            isSelectLayoutShowed = false;
            if(liveIds == null || liveIds.size() == 0) {
                return;
            }
            selectedLiveId = liveIds.get(loopView.getSelectedItem());

            showProgressDialog("获取直播间信息...");
            executeTask(new ThreadPoolManager.Task<LiveRoom>() {
                @Override public LiveRoom onRequest() throws HyphenateException {
                    return LiveManager.getInstance().getLiveRoomDetails(selectedLiveId);
                }

                @Override public void onSuccess(LiveRoom liveRoom) {
                    currentLiveRoom = liveRoom;
                    liveIdView.setText(selectedLiveId);
                    inputLayout.setVisibility(View.VISIBLE);
                    startButton.setEnabled(true);
                    liveNameView.setText(liveRoom.getName());
                    liveDescView.setText(liveRoom.getDescription());
                    if(liveRoom.getCover() != null){
                        Glide.with(AssociateLiveRoomActivity.this)
                                .load(liveRoom.getCover())
                                .into(liveCoverView);
                    }
                    dismissProgressDialog();
                }

                @Override public void onError(HyphenateException exception) {
                    dismissProgressDialog();
                    showToast("获取直播间信息失败！");
                }
            });
        }
    }

    @OnClick(R.id.layout_live_id) void liveIdClicked(){
        if(!isSelectLayoutShowed) {
            showSelectLayout();
            isSelectLayoutShowed = true;
        }
    }

    @OnClick(R.id.layout_live_cover) void setLiveCover(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PICK);
    }

    String name = null;
    String desc = null;
    @OnClick(R.id.btn_start_live) void startLive(){
        if (!TextUtils.isEmpty(liveNameView.getText())){
            name = liveNameView.getText().toString();
        }
        if (!TextUtils.isEmpty(liveDescView.getText())){
            desc = liveDescView.getText().toString();
        }
        showProgressDialog("发起直播...");

        //EMHttpClient.getInstance().uploadFile();

        executeTask(new ThreadPoolManager.Task<LiveRoom>() {
            HyphenateException exception;
            String coverUrl;
            @Override public LiveRoom onRequest() throws HyphenateException {
                if(coverPath != null){

                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + EMClient.getInstance().getAccessToken());
                    new HttpFileManager().uploadFile(coverPath, "", "", "", headers, new EMCloudOperationCallback() {
                        @Override public void onSuccess(String result) {
                            try {
                                JSONObject jsonObj = new JSONObject(result);
                                JSONObject entitys = jsonObj.getJSONArray("entities").getJSONObject(0);
                                String uuid = entitys.getString("uuid");
                                String url = jsonObj.getString("uri");
                                coverUrl = url + "/" + uuid;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override public void onError(String msg) {
                            exception = new HyphenateException(msg);
                        }

                        @Override public void onProgress(int progress) {

                        }
                    });
                }
                if(exception != null){
                    throw exception;
                }

                LiveRoom room =  LiveManager.getInstance().createLiveRoom(name, desc, coverUrl, selectedLiveId);
                //现在服务器没有更新封面，手动调用更新
                try {
                    LiveManager.getInstance().updateLiveRoomCover(selectedLiveId, coverUrl);
                } catch (LiveException e) {
                }
                return room;
            }

            @Override public void onSuccess(LiveRoom liveRoom) {
                dismissProgressDialog();
                startActivity(new Intent(AssociateLiveRoomActivity.this, LiveAnchorActivity.class)
                        .putExtra("liveroom", liveRoom));
                finish();
            }
            @Override public void onError(HyphenateException exception) {
                exception.printStackTrace();
                dismissProgressDialog();
                // ugly
                if(exception.getMessage().contains("current live room is ongoing") &&
                        currentLiveRoom.getAnchorId().equals(EMClient.getInstance().getCurrentUser())){
                    startActivity(new Intent(AssociateLiveRoomActivity.this, LiveAnchorActivity.class)
                            .putExtra("liveroom", currentLiveRoom));
                    finish();
                }else {
                    showLongToast("发起直播失败: " + exception.getMessage());
                }
            }
        });
    }


    boolean isSelectLayoutShowed = false;

    private void dismissSelectLayout() {
        selectLiveIdLayout.animate()
                .translationYBy(selectLiveIdLayout.getHeight())
                .setDuration(500)
                .start();
    }

    private void showSelectLayout() {
        selectLiveIdLayout.setVisibility(View.VISIBLE);
        selectLiveIdLayout.setTranslationY(selectLiveIdLayout.getHeight());
        selectLiveIdLayout.animate()
                .translationYBy(-selectLiveIdLayout.getHeight())
                .setDuration(500)
                .start();

    }


    //TODO 和CreateLiveRoomActivity中相同/类似的功能，可以抽取一下
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUEST_CODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void startPhotoZoom(Uri uri) {
        cacheFile = new File(getExternalCacheDir(), "cover_temp.jpg");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 450);
        intent.putExtra("outputY", 450);
        intent.putExtra("output", Uri.fromFile(cacheFile));
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_CODE_CUTTING);
    }

    /**
     * save the picture data
     */
    private void setPicToView(Intent picdata) {
        //Uri uri = picdata.getData();
        coverPath = cacheFile.getAbsolutePath();
        if(coverPath != null){
            //Bitmap bitmap = BitmapFactory.decodeFile(coverPath);
                Glide.with(AssociateLiveRoomActivity.this)
                        .load(coverPath)
                        .into(liveCoverView);
            //liveCoverView.setImageBitmap(bitmap);
            hintView.setVisibility(View.INVISIBLE);
        }
    }


}
