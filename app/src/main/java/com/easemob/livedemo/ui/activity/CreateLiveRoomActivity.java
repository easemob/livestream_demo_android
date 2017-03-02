package com.easemob.livedemo.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easemob.livedemo.R;
import java.io.FileNotFoundException;

public class CreateLiveRoomActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICK = 1;
    private static final int REQUEST_CODE_CUTTING = 2;

    @BindView(R.id.img_live_cover) ImageView coverView;
    @BindView(R.id.txt_cover_hint) TextView hintView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live_room);
        ButterKnife.bind(this);
        getActionBarToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.layout_live_cover) void setLiveCover(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PICK);
    }

    @OnClick(R.id.btn_start_live) void startLive(){
        startActivity(new Intent(this, LiveAnchorActivity.class));
        finish();
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
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 450);
        intent.putExtra("outputY", 450);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_CODE_CUTTING);
    }

    /**
     * save the picture data
     */
    private void setPicToView(Intent picdata) {
        Uri uri = picdata.getData();
        if(uri != null){
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                coverView.setImageBitmap(bitmap);
                hintView.setVisibility(View.INVISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
