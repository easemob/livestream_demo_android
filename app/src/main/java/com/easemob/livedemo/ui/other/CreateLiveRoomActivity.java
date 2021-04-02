package com.easemob.livedemo.ui.other;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.base.BaseActivity;
import com.easemob.livedemo.ui.fast.FastLiveHostActivity;
import com.easemob.livedemo.ui.live.LiveAnchorActivity;
import com.easemob.livedemo.ui.live.fragment.DemoListDialogFragment;
import com.easemob.livedemo.ui.live.viewmodels.CreateLiveViewModel;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.VersionUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateLiveRoomActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICK = 1;
    private static final int REQUEST_CODE_CUTTING = 2;
    private static final String[] calls = {"相机拍摄", "相册选择"};
    private static final int REQUEST_CODE_CAMERA = 100;

    @BindView(R.id.title_bar)
    EaseTitleBar titleBar;
    @BindView(R.id.img_live_cover)
    EaseImageView coverView;
    @BindView(R.id.txt_cover_hint)
    TextView hintView;
    @BindView(R.id.edt_live_name)
    EditText liveNameView;
    @BindView(R.id.edt_live_desc)
    EditText liveDescView;

    private String coverPath;
    private File cacheFile;
    private CreateLiveViewModel viewmodel;
    protected File cameraFile;
    private String name;
    private String desc;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, CreateLiveRoomActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live_room);
        ButterKnife.bind(this);
        setFitSystemForTheme(true);
        adjustTitle();
        titleBar.setOnRightClickListener(new EaseTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                onBackPressed();
            }
        });
        initViewModel();
    }

    private void initViewModel() {
        viewmodel = new ViewModelProvider(this).get(CreateLiveViewModel.class);
        viewmodel.getCreateObservable().observe(mContext, response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>(true) {
                @Override
                public void onSuccess(LiveRoom data) {
                    if(DemoHelper.isFastLiveType(data.getVideo_type())) {
                        FastLiveHostActivity.actionStart(mContext, data);
                    }else {
                        LiveAnchorActivity.actionStart(mContext, data);
                    }
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

    private void adjustTitle() {
        RelativeLayout rightLayout = titleBar.getRightLayout();
        ViewGroup.LayoutParams params = rightLayout.getLayoutParams();
        if (params instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) params).rightMargin = (int) EaseCommonUtils.dip2px(mContext, 5);
        }
    }


    @OnClick(R.id.img_live_cover)
    void setLiveCover() {
        showSelectDialog();
    }

    @OnClick(R.id.txt_associate_room)
    void associateRoom() {
        startActivity(new Intent(this, AssociateLiveRoomActivity.class));
    }

    @OnClick(R.id.btn_start_live)
    void startLive() {
        if (!TextUtils.isEmpty(liveNameView.getText())) {
            name = liveNameView.getText().toString();
        }
        if (!TextUtils.isEmpty(liveDescView.getText())) {
            desc = liveDescView.getText().toString();
        }
        if (TextUtils.isEmpty(name)) {
            showToast(getResources().getString(R.string.em_live_create_room_check_info));
            return;
        }
        viewmodel.createLiveRoom(name, desc, coverPath);
    }

    @OnClick(R.id.btn_start_fast_live)
    void startFastLive() {
        if (!TextUtils.isEmpty(liveNameView.getText())) {
            name = liveNameView.getText().toString();
        }
        if (!TextUtils.isEmpty(liveDescView.getText())) {
            desc = liveDescView.getText().toString();
        }
        if (TextUtils.isEmpty(name)) {
            showToast(getResources().getString(R.string.em_live_create_room_check_info));
            return;
        }
        viewmodel.createLiveRoom(name, desc, coverPath, LiveRoom.Type.agora_speed_live.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            case REQUEST_CODE_CAMERA:
                if (cameraFile != null && cameraFile.exists()) {
                    startPhotoZoom(getUriForFile(mContext, cameraFile));
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSelectDialog() {
        new DemoListDialogFragment.Builder(mContext)
                //.setTitle(R.string.em_single_call_type)
                .setData(calls)
                .setCancelColorRes(R.color.black)
                .setWindowAnimations(R.style.animate_dialog)
                .setOnItemClickListener(new DemoListDialogFragment.OnDialogItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        switch (position) {
                            case 0:
                                selectPicFromCamera();
                                break;
                            case 1:
                                selectImageFromLocal();
                                break;
                        }
                    }
                })
                .show();
    }

    private void selectImageFromLocal() {
        Intent intent = null;
        if (VersionUtils.isTargetQ(mContext)) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            if (Build.VERSION.SDK_INT < 19) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            } else {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }
        }
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK);
    }

    /**
     * select picture from camera
     */
    private void selectPicFromCamera() {
        if (!checkSdCardExist()) {
            return;
        }
        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(mContext, cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    private static Uri getUriForFile(Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 检查sd卡是否挂载
     *
     * @return
     */
    private boolean checkSdCardExist() {
        return EaseCommonUtils.isSdcardExist();
    }

    private void startPhotoZoom(Uri uri) {
        cacheFile = new File(getExternalCacheDir(), "cover_temp.jpg");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
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
        if (coverPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(coverPath);
            coverView.setImageBitmap(bitmap);
            hintView.setVisibility(View.INVISIBLE);
        }
    }
}
