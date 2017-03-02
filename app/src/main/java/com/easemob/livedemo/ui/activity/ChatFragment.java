package com.easemob.livedemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.easemob.livedemo.R;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import java.util.List;

/**
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment implements EMMessageListener {

  private static final String ARG_USERNAME = "username";
  private static final String ARG_IS_NORMAL = "isNormalStyle";

  boolean isNormalStyle;
  Unbinder unbinder;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.username) TextView usernameView;
  @BindView(R.id.message_list) EaseChatMessageList messageListView;
  @BindView(R.id.edit_text) EditText editText;

  private String toChatUsername;

  public ChatFragment(){}

  public static ChatFragment newInstance(String toChatUsername, boolean isNormalStyle){
    ChatFragment fragment = new ChatFragment();
    Bundle args = new Bundle();
    args.putString(ARG_USERNAME, toChatUsername);
    args.putBoolean(ARG_IS_NORMAL,isNormalStyle);
    fragment.setArguments(args);
    return fragment;
  }


  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_chat, container, false);
    unbinder = ButterKnife.bind(this,view);
    return view;
  }


  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if(getArguments() != null){
      toChatUsername = getArguments().getString(ARG_USERNAME);
      isNormalStyle = getArguments().getBoolean(ARG_IS_NORMAL, false);
      usernameView.setText(toChatUsername);
    }
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(!isNormalStyle)
          getActivity().getSupportFragmentManager().popBackStack();
        else
          getActivity().finish();
      }
    });

    if(isNormalStyle){
      getView().findViewById(R.id.close).setVisibility(View.INVISIBLE);
      toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
      toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
      usernameView.setTextColor(getResources().getColor(R.color.common_white));
    }
    messageListView.init(toChatUsername, EaseConstant.CHATTYPE_SINGLE, null);

    // 获取当前conversation对象
    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername);
    conversation.markAllMessagesAsRead();


  }

  @Override
  public void onResume() {
    super.onResume();
    messageListView.refresh();
    EaseUI.getInstance().pushActivity(getActivity());
    // register the event listener when enter the foreground
    EMClient.getInstance().chatManager().addMessageListener(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    // unregister this event listener when this activity enters the
    // background
    EMClient.getInstance().chatManager().removeMessageListener(this);

    // remove activity from foreground activity list
    EaseUI.getInstance().popActivity(getActivity());
  }


  @OnClick(R.id.btn_send) void sendMessage(){
    if(TextUtils.isEmpty(editText.getText())){
      Toast.makeText(getActivity(), "消息内容不能为空！", Toast.LENGTH_SHORT).show();
      return;
    }
    EMMessage message = EMMessage.createTxtSendMessage(editText.getText().toString(), toChatUsername);
    sendMessage(message);
  }

  private void sendMessage(EMMessage message) {
    editText.setText("");
    //send message
    EMClient.getInstance().chatManager().sendMessage(message);
    //refresh ui
    messageListView.refreshSelectLast();
  }

  @OnClick(R.id.close) void close(){
    getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    if(getActivity() != null && (getActivity() instanceof LiveBaseActivity)){
      ((LiveBaseActivity) getActivity()).updateUnreadMsgView();
    }
  }

  private ChatFragmentListener listener;

  public void setChatFragmentListener(ChatFragmentListener listener){
    this.listener = listener;
  }

  interface ChatFragmentListener{
    void onDestory();
  }

  @Override public void onMessageReceived(List<EMMessage> messages) {
    messageListView.refreshSelectLast();
    EaseUI.getInstance().getNotifier().vibrateAndPlayTone(messages.get(messages.size()-1));
  }

  @Override public void onCmdMessageReceived(List<EMMessage> list) {

  }

  @Override public void onMessageRead(List<EMMessage> messages) {
    messageListView.refresh();
  }

  @Override public void onMessageDelivered(List<EMMessage> messages) {
    messageListView.refresh();
  }


  @Override
  public void onMessageChanged(EMMessage emMessage, Object change) {
    messageListView.refresh();
  }
}
