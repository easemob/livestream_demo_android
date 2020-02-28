package com.easemob.livedemo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.live.LiveAnchorActivity;
import com.easemob.livedemo.ui.live.viewmodels.CreateLiveViewModel;
import com.hyphenate.chat.EMClient;

import java.io.File;

public class CreateLiveRoomActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICK = 1;
    private static final int REQUEST_CODE_CUTTING = 2;

    @BindView(R.id.img_live_cover) ImageView coverView;
    @BindView(R.id.txt_cover_hint) TextView hintView;
    @BindView(R.id.edt_live_name) EditText liveNameView;
    @BindView(R.id.edt_live_desc) EditText liveDescView;

    private String coverPath;
    private File cacheFile;
    private CreateLiveViewModel viewmodel;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, CreateLiveRoomActivity.class);
        context.startActivity(starter);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live_room);
        ButterKnife.bind(this);
        getActionBarToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });

        String restServer = EMClient.getInstance().getOptions().getRestServer();
        Log.e("TAG", "restServer = "+restServer);

        viewmodel = new ViewModelProvider(this).get(CreateLiveViewModel.class);

    }


    @OnClick(R.id.layout_live_cover) void setLiveCover(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PICK);
    }

    @OnClick(R.id.txt_associate_room) void associateRoom(){
        startActivity(new Intent(this, AssociateLiveRoomActivity.class));
    }

    String name = null;
    String desc = null;
    @OnClick(R.id.btn_start_live) void startLive() {

        if (!TextUtils.isEmpty(liveNameView.getText())){
            name = liveNameView.getText().toString();
        }
        if (!TextUtils.isEmpty(liveDescView.getText())){
            desc = liveDescView.getText().toString();
        }

        viewmodel.createLiveRoom(name, desc, coverPath);

        viewmodel.getCreateObservable().observe(mContext, response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>(true) {
                @Override
                public void onSuccess(LiveRoom data) {
                    LiveAnchorActivity.actionStart(mContext, data);
                    finish();
                }

                @Override
                public void onLoading() {
                    super.onLoading();
                    showProgressDialog("发起直播...");
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    dismissProgressDialog();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    showToast("发起直播失败: " + message);
                }
            });
        });

    }

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
            Bitmap bitmap = BitmapFactory.decodeFile(coverPath);
            coverView.setImageBitmap(bitmap);
            hintView.setVisibility(View.INVISIBLE);
        }
    }
}
