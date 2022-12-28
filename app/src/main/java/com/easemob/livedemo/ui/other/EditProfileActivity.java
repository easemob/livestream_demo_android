package com.easemob.livedemo.ui.other;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.callback.OnResourceParseCallback;
import com.easemob.livedemo.common.livedata.LiveDataBus;
import com.easemob.livedemo.common.repository.Resource;
import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.data.repository.UserRepository;
import com.easemob.livedemo.databinding.ActivityEditProfileBinding;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.easemob.livedemo.ui.live.fragment.ListDialogFragment;
import com.easemob.livedemo.ui.live.viewmodels.UserInfoViewModel;
import com.easemob.livedemo.utils.Utils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.VersionUtils;

import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class EditProfileActivity extends BaseLiveActivity {
    private final static String TAG = EditProfileActivity.class.getSimpleName();
    private ActivityEditProfileBinding mBinding;
    private String[] mGenderArray;
    private final static int MAX_USERNAME_LENGTH = 24;
    private static final String[] calls = {"Take Photo", "Upload Photo"};
    private static final int REQUEST_CODE_PICK = 1;
    private static final int REQUEST_CODE_CUTTING = 2;
    private static final int REQUEST_CODE_CAMERA = 100;

    private String mAvatarPath;

    protected File mCameraFile;
    private Uri mCacheUri;

    private UserInfoViewModel mViewModel;
    private ListDialogFragment.Builder mChangeAvatarDialogBuilder;
    private ListDialogFragment mChangeAvatarDialog;

    private EaseUser mUser;
    private MaterialDatePicker<Long> materialDatePicker;

    @Override
    protected View getContentView() {
        mBinding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void initView() {
        super.initView();
        mUser = UserRepository.getInstance().getUserInfo(DemoHelper.getAgoraId());
        initDatePicker();
        EaseUserUtils.setUserAvatar(mContext, DemoHelper.getAgoraId(), mBinding.userIcon);
        EaseUserUtils.setUserNick(DemoHelper.getAgoraId(), mBinding.itemUsername.getTvContent());
        mBinding.titlebarTitle.setTypeface(Utils.getRobotoBlackTypeface(this.getApplicationContext()));

        mBinding.userIcon.setAlpha(0.6f);

        mGenderArray = getResources().getStringArray(R.array.gender_types);
        updateGender(mUser.getGender());
        updateBirthday(mUser.getBirth());
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBinding.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mBinding.titlebarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBinding.itemUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog tipsDialog = new MaterialDialog.Builder(mContext)
                        .theme(Theme.DARK)
                        .backgroundColor(mContext.getResources().getColor(R.color.change_username_bg))
                        .title(mContext.getResources().getString(R.string.setting_username_title))
                        .inputRange(1, MAX_USERNAME_LENGTH, mContext.getResources().getColor(R.color.color_black_333333))
                        .input("", mUser.getNickname(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            }
                        })
                        .cancelable(false)
                        .positiveText(R.string.confirm)
                        .positiveColor(getResources().getColor(R.color.button_color))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.button_color))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (null != dialog.getInputEditText() && !TextUtils.isEmpty(dialog.getInputEditText().getText().toString())) {
                                    setNickname(dialog.getInputEditText().getText().toString());
                                    dialog.cancel();
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .build();
                if (tipsDialog.getInputEditText() != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        tipsDialog.getInputEditText().setTextCursorDrawable(R.drawable.search_cursor);
                    }
                    tipsDialog.getInputEditText().setPadding(10, 0, 10, 0);

                    tipsDialog.getInputEditText().setBackgroundColor(getResources().getColor(R.color.color_black_333333));
                }
                tipsDialog.show();
            }
        });

        mBinding.itemGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ListDialogFragment.Builder(mContext)
                        .setTitle(R.string.setting_gender_title)
                        .setLayoutBgResId(R.color.change_avatar_bg)
                        .setDividerViewBgResId(R.color.change_avatar_divider_bg)
                        .setGravity(Gravity.START)
                        .setData(mGenderArray)
                        .setCancelColorRes(R.color.black)
                        .setWindowAnimations(R.style.animate_dialog)
                        .setOnItemClickListener(new ListDialogFragment.OnDialogItemClickListener() {
                            @Override
                            public void OnItemClick(View view, int position) {
                                setGender(position + 1);
                            }
                        }).show();
            }
        });

        mBinding.itemBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!materialDatePicker.isAdded()) {
                    materialDatePicker.show(getSupportFragmentManager(),
                            "MATERIAL_DATE_PICKER");
                }
            }
        });

        mBinding.changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mChangeAvatarDialog) {
                    mChangeAvatarDialogBuilder = new ListDialogFragment.Builder(mContext)
                            .setTitle(R.string.create_live_change_avatar)
                            .setLayoutBgResId(R.color.change_avatar_bg)
                            .setGravity(Gravity.START)
                            .setData(calls)
                            .setCancelColorRes(R.color.black)
                            .setWindowAnimations(R.style.animate_dialog)
                            .setDividerViewBgResId(R.color.change_avatar_divider_bg)
                            .setOnItemClickListener(new ListDialogFragment.OnDialogItemClickListener() {
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
                            });
                    mChangeAvatarDialog = mChangeAvatarDialogBuilder.show();
                } else {
                    if (null != mChangeAvatarDialog.getDialog() && mChangeAvatarDialog.getDialog().isShowing()) {
                        mChangeAvatarDialog.dismiss();
                    }
                    mChangeAvatarDialog = mChangeAvatarDialogBuilder.show();
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        mViewModel = new ViewModelProvider(this).get(UserInfoViewModel.class);


        mViewModel.getUploadAvatarObservable().observe(mContext, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> stringResource) {
                EditProfileActivity.this.parseResource(stringResource, new OnResourceParseCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.AVATAR_URL, data, new EMValueCallBack<String>() {
                            @Override
                            public void onSuccess(String value) {
                                EditProfileActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mUser.setAvatar(data);
                                        UserRepository.getInstance().saveUserInfoToDb(mUser);
                                        EaseUserUtils.setUserAvatar(mContext, DemoHelper.getAgoraId(), mBinding.userIcon);
                                        LiveDataBus.get().with(DemoConstants.AVATAR_CHANGE).postValue(true);
                                    }
                                });
                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    }
                });
            }
        });
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
            mAvatarPath = new File(new URI(mCacheUri.toString())).getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(mAvatarPath);
            if (null == bitmap) {
                mCacheUri = null;
                mAvatarPath = "";
            } else {
                mBinding.userIcon.setImageBitmap(bitmap);
                setUserAvatar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mCacheUri = null;
            mAvatarPath = "";
        }
    }

    private void setUserAvatar() {
        mViewModel.uploadAvatar(mAvatarPath);
    }


    private void setNickname(final String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            return;
        }
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.NICKNAME, nickname, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                EditProfileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mUser.setNickname(nickname);
                        UserRepository.getInstance().saveUserInfoToDb(mUser);
                        EaseUserUtils.setUserNick(DemoHelper.getAgoraId(), mBinding.itemUsername.getTvContent());
                        LiveDataBus.get().with(DemoConstants.NICKNAME_CHANGE).postValue(nickname);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void updateGender(int gender) {
        if (gender < 1 || gender > mGenderArray.length) {
            Log.e(TAG, "gender value is incorrect,gender=" + gender);
            mBinding.itemGender.setContent(mGenderArray[mGenderArray.length - 1]);
            return;
        }
        mBinding.itemGender.setContent(mGenderArray[gender - 1]);
    }


    private void setGender(final int gender) {
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.GENDER, String.valueOf(gender), new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                EditProfileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mUser.setGender(gender);
                        UserRepository.getInstance().saveUserInfoToDb(mUser);
                        updateGender(gender);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void updateBirthday(String birthday) {
        if (!TextUtils.isEmpty(birthday)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = null;
            try {
                date = format.parse(birthday);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null == date) {
                date = new Date();
            }

            format = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
            mBinding.itemBirthday.setContent(format.format(date));
        } else {
            mBinding.itemBirthday.setContent(this.getResources().getString(R.string.setting_unknown));
        }
    }

    private void setBirthday(int year, int month, int day) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        final String birthday = year + "-" + decimalFormat.format(month) + "-" + decimalFormat.format(day);
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.BIRTH, birthday, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                EditProfileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mUser.setBirth(birthday);
                        UserRepository.getInstance().saveUserInfoToDb(mUser);
                        updateBirthday(birthday);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    private void initDatePicker() {
        MaterialDatePicker.Builder<Long>
                materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        materialDateBuilder.setTheme(R.style.ThemeOverlay_App_MaterialCalendar);
        CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder();
        materialDateBuilder.setTitleText("SELECT YOUR BIRTHDAY");
        materialDateBuilder.setCalendarConstraints(calendarConstraints.build());
        long now = System.currentTimeMillis();
        materialDateBuilder.setCalendarConstraints(new CalendarConstraints.Builder().setEnd(now).build());
        materialDatePicker = materialDateBuilder.build();

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            setBirthday(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        });
    }

}