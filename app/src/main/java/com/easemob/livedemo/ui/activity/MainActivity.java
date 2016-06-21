package com.easemob.livedemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.livedemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

  private int index;
  // 当前fragment的index
  private int currentTabIndex;
  private Fragment[] fragments;

  @BindView(R.id.unread_msg_number) TextView unreadLabel;
  @BindView(R.id.unread_address_number) TextView unreadAddressLable;
  @BindView(R.id.btn_publish) Button publishBtn;
  private Button[] mTabs;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    initView();

    fragments = new Fragment[] { new LiveSquareFragment(), new MessageListFragment(), new MyProfileFragment()};
    // 添加显示第一个fragment
    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments[0])
            .commit();
  }

  /**
   * 初始化组件
   */
  private void initView() {
    mTabs = new Button[3];
    mTabs[0] = (Button) findViewById(R.id.btn_conversation);
    mTabs[1] = (Button) findViewById(R.id.btn_address_list);
    mTabs[2] = (Button) findViewById(R.id.btn_setting);
    // 把第一个tab设为选中状态
    mTabs[0].setSelected(true);
  }

  /**
   * button点击事件
   *
   * @param view
   */
  public void onTabClicked(View view) {
    switch (view.getId()) {
      case R.id.btn_conversation:
        index = 0;
        break;
      case R.id.btn_address_list:
        index = 1;
        break;
      case R.id.btn_setting:
        index = 2;
        break;
      case R.id.btn_publish:
        startActivity(new Intent(this, StartLiveActivity.class));
        break;
    }
    if (currentTabIndex != index) {
      FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
      trx.hide(fragments[currentTabIndex]);
      if (!fragments[index].isAdded()) {
        trx.add(R.id.fragment_container, fragments[index]);
      }
      trx.show(fragments[index]).commit();
    }
    mTabs[currentTabIndex].setSelected(false);
    // 把当前tab设为选中状态
    mTabs[index].setSelected(true);
    currentTabIndex = index;
  }
}
