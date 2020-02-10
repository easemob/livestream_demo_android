package com.easemob.livedemo.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.AboutMeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class MainActivity extends BaseLiveActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navView;
    private EaseTitleBar mTitleBar;
    private Fragment mCurrentFragment;
    private Fragment mHomeFragment, mLiveListFragment, mAboutMeFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        navView = findViewById(R.id.nav_view);
        mTitleBar = findViewById(R.id.title_bar_main);
        navView.setItemIconTintList(null);
    }

    @Override
    protected void initListener() {
        super.initListener();
        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        switchToHome();
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processConflictIntent(intent);
    }

    private void processConflictIntent(Intent intent) {
        if(intent.getBooleanExtra("conflict", false)) {
            EMClient.getInstance().logout(false, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.prompt)
                    .setMessage("账户已在别处登录！")
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            finish();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
            builder.show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mTitleBar.setVisibility(View.VISIBLE);
        switch (menuItem.getItemId()) {
            case R.id.em_main_nav_home :
                switchToHome();
                mTitleBar.setTitle(getResources().getString(R.string.em_main_title_home));
                return true;
            case R.id.em_main_nav_live_list :
                switchToLiveList();
                mTitleBar.setTitle(getResources().getString(R.string.em_main_title_live));
                return true;
            case R.id.em_main_nav_me :
                switchToAboutMe();
                mTitleBar.setVisibility(View.GONE);
                return true;
        }
        return false;
    }

    private void switchToHome() {
        if(mHomeFragment == null) {
            mHomeFragment = new LivingListFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString("status", "ongoing");
        mHomeFragment.setArguments(bundle);
        replace(mHomeFragment);
    }

    private void switchToLiveList() {
        if(mLiveListFragment == null) {
            mLiveListFragment = new LiveListFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString("status", "all");
        mLiveListFragment.setArguments(bundle);
        replace(mLiveListFragment);
    }

    private void switchToAboutMe() {
        if(mAboutMeFragment == null) {
            mAboutMeFragment = new AboutMeFragment();
        }
        replace(mAboutMeFragment);
    }

    private void replace(Fragment fragment) {
        if(mCurrentFragment != fragment) {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if(mCurrentFragment != null) {
                t.hide(mCurrentFragment);
            }
            mCurrentFragment = fragment;
            if(!fragment.isAdded()) {
                t.add(R.id.fl_main_fragment, fragment).show(fragment).commit();
            }else {
                t.show(fragment).commit();
            }
        }
    }
}
