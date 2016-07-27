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
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.easemob.livedemo.R;

/**
 * Created by wei on 2016/7/25.
 */
public class RoomUserDetailsDialog extends DialogFragment {

  Unbinder unbinder;
  @BindView(R.id.tv_username) TextView usernameView;
  @BindView(R.id.btn_mentions) Button mentionBtn;

  private String username;

  public static RoomUserDetailsDialog newInstance(String username) {
    RoomUserDetailsDialog dialog = new RoomUserDetailsDialog();
    Bundle args = new Bundle();
    args.putString("username", username);
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
    }
    if (username != null) {
      usernameView.setText(username);
    }
    mentionBtn.setText("@TA");
  }

  @OnClick(R.id.btn_message) void onMessageBtnClick(){
    ChatFragment fragment = ChatFragment.newInstance(username, false);
    dismiss();
    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.message_container, fragment).commit();
  }

  @OnClick(R.id.btn_mentions) void onMentionBtnClick(){
    if(dialogListener != null){
      dialogListener.onMentionClick(username);
    }
  }

  @OnClick(R.id.btn_follow) void onFollowBtnClick(){
  }

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
    Dialog dialog = new Dialog(getActivity(), R.style.room_user_details_dialog);
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
