package com.easemob.livedemo.ui.other;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Size;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.callback.OnResourceParseCallback;
import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.common.utils.EmojiFilter;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.databinding.ActivityCreateLiveRoomBinding;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.easemob.livedemo.ui.cdn.CdnLiveHostActivity;
import com.easemob.livedemo.ui.live.fragment.ListDialogFragment;
import com.easemob.livedemo.ui.live.viewmodels.CreateLiveViewModel;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.VersionUtils;

import java.io.File;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateLiveRoomActivity extends BaseLiveActivity {

    private static final int REQUEST_CODE_PICK = 1;
    private static final int REQUEST_CODE_CUTTING = 2;
    private static final String[] calls = {"Take Photo", "Upload Photo"};
    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int LIVE_NAME_MAX_LENGTH = 50;

    private ActivityCreateLiveRoomBinding mBinding;
    private CreateLiveViewModel mViewModel;

    private String mCoverPath;

    private final int REQUEST_CODE_PERMISSIONS = 10; //arbitrary number, can be changed accordingly
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};

    protected File mCameraFile;
    private Uri mCacheUri;
    private CameraX.LensFacing mFacingType;


    public static void actionStart(Context context) {
        Intent starter = new Intent(context, CreateLiveRoomActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        mBinding = ActivityCreateLiveRoomBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //reset
        mBinding.bottomLayout.scrollTo(0, 0);
    }

    @Override
    protected void initView() {
        super.initView();

        controlKeyboardLayout(mBinding.container, mBinding.bottomLayout);

        mBinding.changeTv.setTypeface(Utils.getRobotoRegularTypeface(mContext));
        mBinding.closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(v);
                finish();
            }
        });

        EaseUserUtils.setUserAvatar(mContext, EMClient.getInstance().getCurrentUser(), mBinding.coverImage);

        setEditTextEnable(false, mBinding.liveName);


        InputFilter emojiFilter = new InputFilter() {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher emojiMatcher = emoji.matcher(source);
                if (emojiMatcher.find()) {
                    return "";
                }
                return filterCharToNormal(source.toString());
            }
        };

        mBinding.liveName.setFilters(new InputFilter[]{emojiFilter, new EmojiFilter(), new InputFilter.LengthFilter(LIVE_NAME_MAX_LENGTH)});
        mBinding.liveName.setTypeface(Utils.getRobotoRegularTypeface(mContext));

        mBinding.cameraView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                updateTransform();
            }
        });

        mBinding.cameraView.post(new Runnable() {
            @Override
            public void run() {
                if (allPermissionsGranted()) {
                    startCamera(mFacingType); //start camera if permission has been granted by user
                } else {
                    ActivityCompat.requestPermissions(CreateLiveRoomActivity.this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                }
            }
        });
        mBinding.goLive.setTypeface(Utils.getRobotoBlackTypeface(mContext));
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBinding.liveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.liveName.isFocused()) {
                    showEditLiveName();
                }
            }
        });
        mBinding.editLiveNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.liveName.isFocused()) {
                    showEditLiveName();
                }
            }
        });

        mBinding.liveName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_SEND) {
                    setEditTextEnable(false, mBinding.liveName);
                    mBinding.liveNameNumbersTip.setVisibility(View.GONE);
                }
                return false;
            }
        });

        mBinding.liveName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBinding.liveNameNumbersTip.setText(s.toString().trim().length() + "/" + LIVE_NAME_MAX_LENGTH);
            }
        });

        mBinding.coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.liveName.isFocused()) {
                    setEditTextEnable(false, mBinding.liveName);
                    mBinding.liveNameNumbersTip.setVisibility(View.GONE);
                }
                selectImageFromLocal();
                //showSelectDialog();
            }
        });

        mBinding.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditTextEnable(false, mBinding.liveName);
                mBinding.liveNameNumbersTip.setVisibility(View.GONE);
            }
        });

        mBinding.goLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLive();
            }
        });

        mBinding.flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraX.LensFacing.FRONT == mFacingType) {
                    startCamera(CameraX.LensFacing.BACK);
                } else {
                    startCamera(CameraX.LensFacing.FRONT);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mFacingType = CameraX.LensFacing.FRONT;
        initViewModel();
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this).get(CreateLiveViewModel.class);
        mViewModel.getCreateObservable().observe(mContext, response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>(true) {
                @Override
                public void onSuccess(LiveRoom data) {
                    if (DemoHelper.isCdnLiveType(data.getVideo_type())) {
                        stopCamera();
                        CdnLiveHostActivity.actionStart(mContext, data);
                    }
                    finish();
                }

                @Override
                public void onLoading() {
                    super.onLoading();
                    CreateLiveRoomActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBinding.goLive.setText("");
                            mBinding.loading.bringToFront();
                            mBinding.loading.invalidate();
                            Animation rotateAnimation = AnimationUtils.loadAnimation(CreateLiveRoomActivity.this, R.anim.go_live_loading_anim);
                            LinearInterpolator lin = new LinearInterpolator();
                            rotateAnimation.setInterpolator(lin);
                            mBinding.loading.startAnimation(rotateAnimation);
                            mBinding.loading.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    dismissProgressDialog();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    showToast("go live fail: " + message);
                }
            });
        });
    }

    private void showEditLiveName() {
        setEditTextEnable(true, mBinding.liveName);
        if (null != mBinding.liveName.getText()) {
            mBinding.liveName.setSelection(mBinding.liveName.getText().length());
        }
        mBinding.liveNameNumbersTip.setVisibility(View.VISIBLE);
        mBinding.liveNameNumbersTip.setText(mBinding.liveName.getText().toString().trim().length() + "/" + LIVE_NAME_MAX_LENGTH);

    }

    void startLive() {
        String name = "";
        if (mBinding.liveName.getText() != null) {
            name = mBinding.liveName.getText().toString();
        }
        if (TextUtils.isEmpty(name)) {
            showToast(getResources().getString(R.string.create_live_room_check_info));
            return;
        }
        mViewModel.createLiveRoom(name, "", mCoverPath, LiveRoom.Type.agora_cdn_live.name());
        Utils.hideKeyboard(mBinding.liveName);
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
                setPicToView();
                break;
            case REQUEST_CODE_CAMERA:
                if (mCameraFile != null && mCameraFile.exists()) {
                    startPhotoZoom(getUriForFile(mContext, mCameraFile));
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSelectDialog() {
        new ListDialogFragment.Builder(mContext)
                .setTitle(R.string.create_live_change_cover)
                .setGravity(Gravity.START)
                .setLayoutBgResId(R.color.white)
                .setDividerViewBgResId(R.color.change_cover_divider_bg)
                .setData(calls)
                .setCancelColorRes(R.color.black)
                .setTitleColorRes(R.color.black)
                .setContentColorRes(R.color.change_cover_content_color)
                .setWindowAnimations(R.style.animate_dialog)
                .setGravity(Gravity.LEFT)
                .setOnItemClickListener(new ListDialogFragment.OnDialogItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        switch (position) {
                            /*case 0:
                                selectPicFromCamera();
                                break;*/
                            case 0:
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
        mCameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");

        mCameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(mContext, mCameraFile)),
                REQUEST_CODE_CAMERA);
    }

    private static Uri getUriForFile(Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    private boolean checkSdCardExist() {
        return EaseCommonUtils.isSdcardExist();
    }

    private void startPhotoZoom(Uri uri) {
        mCacheUri = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 450);
        intent.putExtra("outputY", 450);
        intent.putExtra("output", mCacheUri);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_CODE_CUTTING);
    }

    /**
     * save the picture data
     */
    private void setPicToView() {
        try {
            mCoverPath = new File(new URI(mCacheUri.toString())).getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(mCoverPath);
            if (null == bitmap) {
                mCoverPath = "";
                mCacheUri = null;
            } else {
                mBinding.coverImage.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mCoverPath = "";
            mCacheUri = null;
        }
    }

    private void setEditTextEnable(boolean enable, EditText editText) {
        editText.setFocusable(enable);
        editText.setFocusableInTouchMode(enable);
        editText.setInputType(enable ? InputType.TYPE_TEXT_FLAG_MULTI_LINE : InputType.TYPE_NULL);
        editText.setHorizontallyScrolling(false);
        editText.setMaxLines(Integer.MAX_VALUE);
        if (enable) {
            editText.requestFocus();
            Utils.showKeyboard(editText);
        } else {
            editText.clearFocus();
            Utils.hideKeyboard(editText);
        }
    }

    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = mBinding.cameraView.getMeasuredWidth();
        float h = mBinding.cameraView.getMeasuredHeight();

        float centreX = w / 2f; //calc centre of the viewfinder
        float centreY = h / 2f;

        int rotationDgr;
        int rotation = (int) mBinding.cameraView.getRotation(); //cast to int bc switches don't like floats

        switch (rotation) { //correct output to account for display rotation
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float) rotationDgr, centreX, centreY);
        mBinding.cameraView.setTransform(mx);
    }


    private void startCamera(CameraX.LensFacing facing) {
        mFacingType = facing;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            stopCamera();

            PreviewConfig previewConfig = new PreviewConfig.Builder()
                    .setLensFacing(facing)
                    //.setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    //.setTargetResolution(new Size((int) EaseCommonUtils.getScreenInfo(mContext)[0], (int) EaseCommonUtils.getScreenInfo(mContext)[1]))
                    .setTargetResolution(new Size(mBinding.cameraView.getMeasuredWidth(), mBinding.cameraView.getMeasuredHeight()))
                    .build();


            Preview preview = new Preview(previewConfig);
            preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
                @Override
                public void onUpdated(Preview.PreviewOutput output) {
                    ViewGroup parent = (ViewGroup) mBinding.cameraView.getParent();
                    parent.removeView(mBinding.cameraView);
                    parent.addView(mBinding.cameraView, 0);

                    mBinding.cameraView.setSurfaceTexture(output.getSurfaceTexture());
                    updateTransform();
                }
            });

            CameraX.bindToLifecycle(this, preview);
        } else {
            //deal other version
        }
    }

    private void stopCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraX.unbindAll();
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //start camera when permissions have been granted otherwise exit app
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(mFacingType);
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        //check if req permissions have been granted
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static String filterCharToNormal(String oldString) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = oldString.length();
        for (int i = 0; i < length; i++) {
            char codePoint = oldString.charAt(i);
            if (((codePoint >= 0x4e00) && (codePoint <= 0x9fa5)) ||  //chinese
                    ((codePoint >= 0x20) && (codePoint <= 0x7E))) {
                stringBuilder.append(codePoint);
            }
        }
        return stringBuilder.toString();
    }

    private void controlKeyboardLayout(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        root.getWindowVisibleDisplayFrame(rect);
                        int rootInvisibleHeight = root.getRootView()
                                .getHeight() - rect.bottom;
                        if (rootInvisibleHeight > 100) {
                            int[] location = new int[2];
                            scrollToView.getLocationInWindow(location);
                            int scrollHeight = (location[1] + scrollToView
                                    .getHeight()) - rect.bottom;
                            scrollToView.scrollTo(0, scrollHeight);
                        } else {
                            scrollToView.scrollTo(0, 0);
                        }
                    }
                });
    }
}
