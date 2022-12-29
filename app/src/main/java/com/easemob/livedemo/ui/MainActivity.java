package com.easemob.livedemo.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.livedata.LiveDataBus;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.databinding.ActivityMainBinding;
import com.easemob.livedemo.runtimepermissions.PermissionsManager;
import com.easemob.livedemo.runtimepermissions.PermissionsResultAction;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.easemob.livedemo.ui.cdn.fragment.CdnLivingListFragment;
import com.easemob.livedemo.ui.other.CreateLiveRoomActivity;
import com.easemob.livedemo.ui.other.EditProfileActivity;
import com.easemob.livedemo.ui.other.SearchActivity;
import com.easemob.livedemo.ui.other.fragment.AboutMeFragment;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends BaseLiveActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding mBinding;
    private Fragment mCurrentFragment;
    private int position;

    @Override
    protected View getContentView() {
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }


    @Override
    protected void initView() {
        super.initView();
        Utils.hideKeyboard(mBinding.title);
        mBinding.title.setTypeface(Utils.getRobotoBlackTypeface(this.getApplicationContext()));
        EaseUserUtils.setUserAvatar(mContext, EMClient.getInstance().getCurrentUser(), mBinding.ivHomeSet);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBinding.llHomeHome.setOnClickListener(this);
        mBinding.llHomeSet.setOnClickListener(this);
        mBinding.rlHomeLive.setOnClickListener(this);

        mBinding.titlebarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentFragment instanceof CdnLivingListFragment) {
                    List<LiveRoom> liveRooms = ((CdnLivingListFragment) mCurrentFragment).getLiveRooms();
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra("liverooms", (Serializable) liveRooms);
                    startActivity(intent);
                } else if (mCurrentFragment instanceof AboutMeFragment) {
                    Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        skipToTarget(position);
        Log.e(TAG, "user = " + EMClient.getInstance().getCurrentUser());
        requestPermissions();

        LiveDataBus.get().with(DemoConstants.AVATAR_CHANGE, Boolean.class)
                .observe(mContext, response -> {
                    EaseUserUtils.setUserAvatar(mContext, EMClient.getInstance().getCurrentUser(), mBinding.ivHomeSet);
                });
    }

    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                //Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processConflictIntent(intent);
    }

    private void processConflictIntent(Intent intent) {
        if (intent.getBooleanExtra("conflict", false)) {
            EMClient.getInstance().logout(false, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.prompt)
                    .setMessage(R.string.home_logged_tip)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
            builder.show();
        }
    }

    private void switchToHome() {
        mBinding.ivHomeHome.setImageResource(R.drawable.live_home_selected);
        mBinding.ivHomeSet.setAlpha(0.8f);
        Fragment homeFragment = getSupportFragmentManager().findFragmentByTag("home");
        if (homeFragment == null) {
            CdnLivingListFragment fragment = new CdnLivingListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("status", "ongoing");
            fragment.setArguments(bundle);
            homeFragment = fragment;
        }
        replace(homeFragment, "home");
    }

    private void switchToAboutMe() {
        mBinding.ivHomeHome.setImageResource(R.drawable.live_home_unselected);
        mBinding.ivHomeSet.setAlpha(1.0f);
        Fragment aboutMeFragment = getSupportFragmentManager().findFragmentByTag("about_me");
        if (aboutMeFragment == null) {
            aboutMeFragment = new AboutMeFragment();
        }
        replace(aboutMeFragment, "about_me");
    }

    private void startAnimation(float fromX, float toX, float fromY, float toY) {
        ScaleAnimation animation = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        mBinding.rlHomeLive.startAnimation(animation);
    }

    private void replace(Fragment fragment, String tag) {
        if (mCurrentFragment != fragment) {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if (mCurrentFragment != null) {
                t.hide(mCurrentFragment);
            }
            mCurrentFragment = fragment;
            if (!fragment.isAdded()) {
                t.add(R.id.fl_main_fragment, fragment, tag).show(fragment).commit();
            } else {
                t.show(fragment).commit();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home_home:
                skipToTarget(0);
                break;
            case R.id.ll_home_set:
                skipToTarget(1);
                break;
            case R.id.rl_home_live:
                CreateLiveRoomActivity.actionStart(mContext);
                break;
        }
    }

    private void skipToTarget(int position) {
        this.position = position;
        switch (position) {
            case 0:
                switchToHome();
                mBinding.title.setText(getResources().getString(R.string.home_title));
                mBinding.titlebarIcon.setImageResource(R.drawable.home_search);
                break;
            case 1:
                switchToAboutMe();
                mBinding.title.setText(getResources().getString(R.string.profile_title));
                mBinding.titlebarIcon.setImageResource(R.drawable.profile_edit);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", position);
    }
}
