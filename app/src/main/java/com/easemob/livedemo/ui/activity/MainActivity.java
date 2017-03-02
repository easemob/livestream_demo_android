package com.easemob.livedemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easemob.livedemo.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class MainActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    getActionBarToolbar().setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, SearchActivity.class));
      }
    });
    // 添加显示第一个fragment
    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new LiveListFragment())
            .commit();

  }

  @OnClick(R.id.floatingActionButton) void createLiveRoom(){
    startActivity(new Intent(this, CreateLiveRoomActivity.class));
  }

  @OnClick(R.id.txt_logout) void logout(){
    EMClient.getInstance().logout(false, new EMCallBack() {
      @Override
      public void onSuccess() {
        finish();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
      }

      @Override
      public void onError(int i, String s) {

      }

      @Override
      public void onProgress(int i, String s) {

      }
    });
  }

}
