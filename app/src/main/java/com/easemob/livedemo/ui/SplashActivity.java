package com.easemob.livedemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.common.utils.PreferenceManager;
import com.easemob.livedemo.data.repository.UserRepository;
import com.easemob.livedemo.databinding.ActivitySplashBinding;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.hyphenate.chat.EMClient;


public class SplashActivity extends BaseLiveActivity {
    private final static String TAG = "lives";
    private static final int SKIP_MAIN = 0;
    private static final int SKIP_LOGIN = 1;
    private ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View getContentView() {
        mBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
        PreferenceManager.init(mContext);
        DemoHelper.init();
        UserRepository.getInstance().init(mContext);

        skipToTarget();
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case SKIP_MAIN:
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    break;
                case SKIP_LOGIN:
                    LoginActivity.actionStart(mContext);
                    finish();
                    break;
            }

            return false;
        }
    });

    private void skipToTarget() {
        if (EMClient.getInstance().isLoggedInBefore()) {
            mHandler.sendEmptyMessageDelayed(SKIP_MAIN, 1000 * 3);//3s
        } else {
            login();
        }
    }

    private void login() {
        mHandler.sendEmptyMessageDelayed(SKIP_LOGIN, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

}
