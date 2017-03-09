package com.easemob.livedemo.ui.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.easemob.livedemo.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 2016/7/25.
 */
public class RoomUserDetailsDialog extends DialogFragment {

  Unbinder unbinder;
  @BindView(R.id.tv_username) TextView usernameView;

  private String username;
  private String chatroomId;

  public static RoomUserDetailsDialog newInstance(String username, String chatroomId) {
    RoomUserDetailsDialog dialog = new RoomUserDetailsDialog();
    Bundle args = new Bundle();
    args.putString("username", username);
    args.putString("chatroomId", chatroomId);
    dialog.setArguments(args);
    return dialog;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_room_user_details, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (getArguments() != null) {
      username = getArguments().getString("username");
      chatroomId = getArguments().getString("chatroomId");
    }
    if (username != null) {
      usernameView.setText(username);
    }
    //mentionBtn.setText("@TA");
  }

  @OnClick(R.id.layout_live_no_talk) void noTalk(){
    if (chatroomId != null) {
      //EMClient.getInstance().chatroomManager().asyncMuteChatRoomMembers(chatroomId, getUserList(), 0, new EMValueCallBack<EMChatRoom>() {
      //  @Override public void onSuccess(EMChatRoom value) {
      //    showToast("禁言成功");
      //  }
      //
      //  @Override public void onError(int error, String errorMsg) {
      //    showToast("禁言失败");
      //  }
      //});
    }
  }


  @OnClick(R.id.layout_live_add_blacklist) void addToBlacklist(){
    if (chatroomId != null) {
      List<String> users = new ArrayList<>();
      //EMClient.getInstance().chatroomManager().asyncBlockChatroomMembers(chatroomId, getUserList(),
      //        new EMValueCallBack<EMChatRoom>() {
      //          @Override public void onSuccess(EMChatRoom value) {
      //            showToast("加入黑名单成功");
      //          }
      //
      //          @Override public void onError(int error, String errorMsg) {
      //            showToast("加入黑名单失败");
      //          }
      //        });
    }
  }
  @OnClick(R.id.layout_live_kick) void kickMember(){
    //EMClient.getInstance().chatroomManager().asyncRemoveChatRoomMembers(chatroomId, getUserList(),
    //        new EMValueCallBack<EMChatRoom>() {
    //          @Override public void onSuccess(EMChatRoom value) {
    //            showToast("踢出成功");
    //          }
    //
    //          @Override public void onError(int error, String errorMsg) {
    //            showToast("踢出失败");
    //          }
    //        });
  }
  @OnClick(R.id.btn_set_admin) void setToAdmin(){
    //EMClient.getInstance().chatroomManager().asyncAddChatRoomAdmin(chatroomId, a);
  }


  private List<String> getUserList(){
    List<String> users = new ArrayList<>();
    users.add(username);
    return users;
  }

  private void showToast(final String toast){
    getActivity().runOnUiThread(new Runnable() {
      @Override public void run() {
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
      }
    });
  }
  //@OnClick(R.id.btn_message) void onMessageBtnClick(){
  //  ChatFragment fragment = ChatFragment.newInstance(username, false);
  //  dismiss();
  //  getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.message_container, fragment).commit();
  //}

  //@OnClick(R.id.btn_mentions) void onMentionBtnClick(){
  //  if(dialogListener != null){
  //    dialogListener.onMentionClick(username);
  //  }
  //}
  //
  //@OnClick(R.id.btn_follow) void onFollowBtnClick(){
  //}

  private UserDetailsDialogListener dialogListener;

  public void setUserDetailsDialogListener(UserDetailsDialogListener dialogListener){
    this.dialogListener = dialogListener;
  }

  interface UserDetailsDialogListener{
    void onMentionClick(String username);
  }



  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // 使用不带theme的构造器，获得的dialog边框距离屏幕仍有几毫米的缝隙。
    // Dialog dialog = new Dialog(getActivity());
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // must be called before set content
    dialog.setContentView(R.layout.fragment_room_user_details);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.setCanceledOnTouchOutside(true);

    // 设置宽度为屏宽、靠近屏幕底部。
    Window window = dialog.getWindow();
    WindowManager.LayoutParams wlp = window.getAttributes();
    wlp.gravity = Gravity.BOTTOM;
    wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
    window.setAttributes(wlp);

    return dialog;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
