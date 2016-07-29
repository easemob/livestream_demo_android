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
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import java.util.List;

public class MainActivity extends BaseActivity {

  private int index;
  // 当前fragment的index
  private int currentTabIndex;
  private Fragment[] fragments;

  @BindView(R.id.unread_msg_number) TextView unreadLabel;
  private Button[] mTabs;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    initView();

    fragments = new Fragment[] { new LiveSquareFragment(), ConversationListFragment.newInstance(null, true), new MyProfileFragment()};
    // 添加显示第一个fragment
    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments[0])
            .commit();


  }

  @Override protected void onResume() {
    super.onResume();
    updateUnreadLabel();
    EMClient.getInstance().chatManager().addMessageListener(messageListener);
  }

  @Override protected void onStop() {
    super.onStop();
    EMClient.getInstance().chatManager().removeMessageListener(messageListener);
  }

  EMMessageListener messageListener = new EMMessageListener() {
    @Override public void onMessageReceived(List<EMMessage> list) {
      refreshUIWithMessage();
    }
    @Override public void onCmdMessageReceived(List<EMMessage> list) {
    }

    @Override public void onMessageReadAckReceived(List<EMMessage> list) {
    }

    @Override public void onMessageDeliveryAckReceived(List<EMMessage> list) {
    }

    @Override public void onMessageChanged(EMMessage emMessage, Object o) {
      refreshUIWithMessage();
    }
  };

  private void refreshUIWithMessage() {
    runOnUiThread(new Runnable() {
      public void run() {
        // refresh unread count
        updateUnreadLabel();
        if (currentTabIndex == 1) {
          // refresh conversation list
          if (fragments[1] != null) {
            ((ConversationListFragment)fragments[1]).refreshList();
          }
        }
      }
    });
  }

  /**
   * update unread message count
   */
  public void updateUnreadLabel() {
    int count = getUnreadMsgCountTotal();
    if (count > 0) {
      unreadLabel.setText(String.valueOf(count));
      unreadLabel.setVisibility(View.VISIBLE);
    } else {
      unreadLabel.setVisibility(View.INVISIBLE);
    }
  }

  /**
   * get unread message count
   *
   * @return
   */
  public int getUnreadMsgCountTotal() {
    int unreadMsgCountTotal = 0;
    int chatroomUnreadMsgCount = 0;
    unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
    for(EMConversation conversation:EMClient.getInstance().chatManager().getAllConversations().values()){
      if(conversation.getType() == EMConversation.EMConversationType.ChatRoom)
        chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
    }
    return unreadMsgCountTotal-chatroomUnreadMsgCount;
  }

  /**
   * 初始化组件
   */
  private void initView() {
    mTabs = new Button[3];
    mTabs[0] = (Button) findViewById(R.id.btn_square);
    mTabs[1] = (Button) findViewById(R.id.btn_conversation_list);
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
      case R.id.btn_square:
        index = 0;
        break;
      case R.id.btn_conversation_list:
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
