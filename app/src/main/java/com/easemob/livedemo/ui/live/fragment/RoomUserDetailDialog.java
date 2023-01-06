package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.easemob.livedemo.utils.LanguageUtils;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMChatRoomManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.jakewharton.rxbinding4.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.callback.OnResourceParseCallback;
import com.easemob.livedemo.common.livedata.LiveDataBus;
import com.easemob.livedemo.data.model.AttentionBean;
import com.easemob.livedemo.data.repository.UserRepository;
import com.easemob.livedemo.ui.base.BaseLiveDialogFragment;
import com.easemob.livedemo.ui.live.viewmodels.LivingViewModel;
import com.easemob.livedemo.ui.live.viewmodels.UserManageViewModel;
import com.easemob.livedemo.ui.widget.ArrowItemView;
import com.easemob.livedemo.ui.widget.SwitchItemView;
import com.easemob.livedemo.utils.StatusBarCompat;
import com.easemob.livedemo.utils.Utils;
import io.reactivex.rxjava3.functions.Consumer;
import kotlin.Unit;

public class RoomUserDetailDialog extends BaseLiveDialogFragment implements SwitchItemView.OnCheckedChangeListener {
    private View layout;
    private EaseImageView userIcon;
    private TextView userNameTv;
    private ConstraintLayout sexLayout;
    private ImageView sexIcon;
    private TextView ageTv;
    private ImageView roleType;
    private SwitchItemView timeoutAll;
    private EaseImageView muteState;
    private ArrowItemView removeFromAllowedListItem;
    private ArrowItemView moveToAllowedListItem;
    private ArrowItemView removeAsModeratorItem;
    private ArrowItemView assignAsModeratorItem;
    private ArrowItemView timeoutItem;
    private ArrowItemView removeTimeoutItem;
    private ArrowItemView banItem;
    private ArrowItemView unbanItem;


    private UserManageViewModel viewModel;
    protected LivingViewModel livingViewModel;
    private String username;
    private String roomId;
    protected EMChatRoomManager mChatRoomManager;
    protected EMChatRoom mChatRoom;

    public static RoomUserDetailDialog getNewInstance(String chatroomId, String username) {
        RoomUserDetailDialog dialog = new RoomUserDetailDialog();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("chatroomid", chatroomId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_dialog_user_detail;
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if (bundle != null) {
            username = bundle.getString("username");
            roomId = bundle.getString("chatroomid");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        layout = findViewById(R.id.layout);
        userIcon = findViewById(R.id.user_icon);
        userNameTv = findViewById(R.id.tv_username);
        sexLayout = findViewById(R.id.layout_sex);
        sexIcon = findViewById(R.id.sex_icon);
        ageTv = findViewById(R.id.age_tv);
        roleType = findViewById(R.id.role_type);
        timeoutAll = findViewById(R.id.item_timeout_all);
        muteState = findViewById(R.id.mute_state);
        removeFromAllowedListItem = findViewById(R.id.item_remove_from_allowed_list);
        moveToAllowedListItem = findViewById(R.id.item_move_to_allowed_list);
        removeAsModeratorItem = findViewById(R.id.item_remove_as_moderator);
        assignAsModeratorItem = findViewById(R.id.item_assign_as_moderator);
        timeoutItem = findViewById(R.id.item_timeout);
        removeTimeoutItem = findViewById(R.id.item_remove_timeout);
        banItem = findViewById(R.id.item_ban);
        unbanItem = findViewById(R.id.item_unban);

        EaseUser user = UserRepository.getInstance().getUserInfo(username);

        EaseUserUtils.setUserNick(username, userNameTv);
        EaseUserUtils.setUserAvatar(mContext, username, userIcon);
        int gender = user.getGender();
        if (1 == gender) {
            sexLayout.setBackgroundResource(R.drawable.sex_male_bg_shape);
            sexIcon.setImageResource(R.drawable.sex_male_icon);
        } else if (2 == gender) {
            sexLayout.setBackgroundResource(R.drawable.sex_female_bg_shape);
            sexIcon.setImageResource(R.drawable.sex_female_icon);
        } else if (3 == gender) {
            sexLayout.setBackgroundResource(R.drawable.sex_other_bg_shape);
            sexIcon.setImageResource(R.drawable.sex_other_icon);
        } else {
            sexLayout.setBackgroundResource(R.drawable.sex_secret_bg_shape);
            sexIcon.setImageResource(R.drawable.sex_secret_icon);
        }
        String birth = user.getBirth();
        if (!TextUtils.isEmpty(birth)) {
            ageTv.setText(String.valueOf(Math.max(Utils.getAgeByBirthday(user.getBirth()), 0)));
        }

        layout.post(new Runnable() {
            @Override
            public void run() {
                int nicknameMaxWidth = layout.getWidth() - layout.getPaddingStart() - layout.getPaddingEnd() -
                        ((FrameLayout.LayoutParams) layout.getLayoutParams()).getMarginStart() - ((FrameLayout.LayoutParams) layout.getLayoutParams()).getMarginEnd() -
                        sexLayout.getWidth() - sexLayout.getPaddingStart() - sexLayout.getPaddingEnd() -
                        ((ConstraintLayout.LayoutParams) sexLayout.getLayoutParams()).getMarginStart() - ((ConstraintLayout.LayoutParams) sexLayout.getLayoutParams()).getMarginEnd() -
                        ((ConstraintLayout.LayoutParams) userNameTv.getLayoutParams()).getMarginStart() - ((ConstraintLayout.LayoutParams) userNameTv.getLayoutParams()).getMarginEnd();

                userNameTv.setMaxWidth(nicknameMaxWidth);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            final float screenHeight = EaseCommonUtils.getScreenInfo(mContext)[1];
            final int navBarHeight = StatusBarCompat.getNavigationBarHeight(mContext);
            lp.height = (int) screenHeight * 3 / 5 + navBarHeight;
            lp.gravity = Gravity.BOTTOM;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            dialogWindow.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateView(boolean initView) {
        if (null == mChatRoom) {
            return;
        }
        boolean isZh = LanguageUtils.isZhLanguage(mContext);
        if (username.equals(mChatRoom.getOwner())) {
            roleType.setVisibility(View.VISIBLE);
            roleType.setImageResource(isZh ? R.drawable.live_streamer_zh : R.drawable.live_streamer);
        } else if (mChatRoom.getAdminList().contains(username)) {
            roleType.setVisibility(View.VISIBLE);
            roleType.setImageResource(isZh ? R.drawable.live_moderator_zh : R.drawable.live_moderator);
        } else {
            roleType.setVisibility(View.GONE);
        }

        if (!mChatRoom.getMemberList().contains(username) && !mChatRoom.getAdminList().contains(username) && !username.equals(mChatRoom.getOwner()) && !mChatRoom.getBlacklist().contains(username)) {
            return;
        }

        if (mChatRoom.getMuteList().containsKey(username)) {
            muteState.setVisibility(View.VISIBLE);
        } else {
            muteState.setVisibility(View.GONE);
        }

        if (mChatRoom.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
            if (mChatRoom.getOwner().equals(username)) {
                timeoutAll.setVisibility(View.VISIBLE);
                //timeoutAll.getSwitch().setEnabled(mChatRoom.getMemberList().size() != 0 || mChatRoom.getAdminList().size() != 0);
                if (initView) {
                    timeoutAll.setOnCheckedChangeListener(null);
                    timeoutAll.getSwitch().setChecked(mChatRoom.isAllMemberMuted());
                    timeoutAll.setOnCheckedChangeListener(this);
                }
            } else {
                if (mChatRoom.getWhitelist().contains(username)) {
                    removeFromAllowedListItem.setVisibility(View.VISIBLE);
                    moveToAllowedListItem.setVisibility(View.GONE);
                } else {
                    removeFromAllowedListItem.setVisibility(View.GONE);
                    moveToAllowedListItem.setVisibility(View.VISIBLE);
                }

                if (mChatRoom.getMuteList().containsKey(username)) {
                    removeTimeoutItem.setVisibility(View.VISIBLE);
                    timeoutItem.setVisibility(View.GONE);
                } else {
                    removeTimeoutItem.setVisibility(View.GONE);
                    timeoutItem.setVisibility(View.VISIBLE);
                }

                if (mChatRoom.getBlacklist().contains(username)) {
                    moveToAllowedListItem.setVisibility(View.GONE);
                    removeFromAllowedListItem.setVisibility(View.GONE);
                    assignAsModeratorItem.setVisibility(View.GONE);
                    removeAsModeratorItem.setVisibility(View.GONE);
                    timeoutItem.setVisibility(View.GONE);
                    removeTimeoutItem.setVisibility(View.GONE);
                    banItem.setVisibility(View.GONE);
                    unbanItem.setVisibility(View.VISIBLE);
                } else {
                    banItem.setVisibility(View.VISIBLE);
                    unbanItem.setVisibility(View.GONE);
                }

                if (mChatRoom.getAdminList().contains(username)) {
                    removeAsModeratorItem.setVisibility(View.VISIBLE);
                    assignAsModeratorItem.setVisibility(View.GONE);
                    if (mChatRoom.getMuteList().containsKey(username)) {
                        timeoutItem.setVisibility(View.GONE);
                        removeFromAllowedListItem.setVisibility(View.GONE);
                        moveToAllowedListItem.setVisibility(View.GONE);
                    } else {
                        if (mChatRoom.getWhitelist().contains(username)) {
                            timeoutItem.setVisibility(View.GONE);
                            removeTimeoutItem.setVisibility(View.GONE);
                            banItem.setVisibility(View.GONE);
                            unbanItem.setVisibility(View.GONE);
                        }
                    }
                } else {
                    removeAsModeratorItem.setVisibility(View.GONE);
                    assignAsModeratorItem.setVisibility(View.VISIBLE);
                }
                if (mChatRoom.getWhitelist().contains(username)) {
                    timeoutItem.setVisibility(View.GONE);
                    removeTimeoutItem.setVisibility(View.GONE);
                    banItem.setVisibility(View.GONE);
                    unbanItem.setVisibility(View.GONE);
                }
            }
        } else if (mChatRoom.getAdminList().contains(EMClient.getInstance().getCurrentUser())) {
            timeoutAll.setVisibility(View.GONE);
            if (mChatRoom.getOwner().equals(username) || EMClient.getInstance().getCurrentUser().equals(username)) {
                moveToAllowedListItem.setVisibility(View.GONE);
                removeFromAllowedListItem.setVisibility(View.GONE);
                assignAsModeratorItem.setVisibility(View.GONE);
                removeAsModeratorItem.setVisibility(View.GONE);
                timeoutItem.setVisibility(View.GONE);
                removeTimeoutItem.setVisibility(View.GONE);
                banItem.setVisibility(View.GONE);
                unbanItem.setVisibility(View.GONE);
            } else {
                if (mChatRoom.getWhitelist().contains(username)) {
                    removeFromAllowedListItem.setVisibility(View.VISIBLE);
                    moveToAllowedListItem.setVisibility(View.GONE);
                } else {
                    removeFromAllowedListItem.setVisibility(View.GONE);
                    moveToAllowedListItem.setVisibility(View.VISIBLE);
                }

                if (mChatRoom.getMuteList().containsKey(username)) {
                    removeTimeoutItem.setVisibility(View.VISIBLE);
                    timeoutItem.setVisibility(View.GONE);
                } else {
                    removeTimeoutItem.setVisibility(View.GONE);
                    timeoutItem.setVisibility(View.VISIBLE);
                }

                removeAsModeratorItem.setVisibility(View.GONE);
                assignAsModeratorItem.setVisibility(View.GONE);

                if (mChatRoom.getBlacklist().contains(username)) {
                    moveToAllowedListItem.setVisibility(View.GONE);
                    removeFromAllowedListItem.setVisibility(View.GONE);
                    assignAsModeratorItem.setVisibility(View.GONE);
                    removeAsModeratorItem.setVisibility(View.GONE);
                    timeoutItem.setVisibility(View.GONE);
                    removeTimeoutItem.setVisibility(View.GONE);
                    banItem.setVisibility(View.GONE);
                    unbanItem.setVisibility(View.VISIBLE);
                } else {
                    banItem.setVisibility(View.VISIBLE);
                    unbanItem.setVisibility(View.GONE);
                }

                if (mChatRoom.getMuteList().containsKey(username)) {
                    timeoutItem.setVisibility(View.GONE);
                    removeFromAllowedListItem.setVisibility(View.GONE);
                    moveToAllowedListItem.setVisibility(View.GONE);
                } else {
                    if (mChatRoom.getWhitelist().contains(username)) {
                        timeoutItem.setVisibility(View.GONE);
                        removeTimeoutItem.setVisibility(View.GONE);
                        banItem.setVisibility(View.GONE);
                        unbanItem.setVisibility(View.GONE);
                    }
                }

                if (mChatRoom.getWhitelist().contains(username)) {
                    timeoutItem.setVisibility(View.GONE);
                    removeTimeoutItem.setVisibility(View.GONE);
                    banItem.setVisibility(View.GONE);
                    unbanItem.setVisibility(View.GONE);
                }
            }
        } else {
            timeoutAll.setVisibility(View.GONE);
            moveToAllowedListItem.setVisibility(View.GONE);
            removeFromAllowedListItem.setVisibility(View.GONE);
            assignAsModeratorItem.setVisibility(View.GONE);
            removeAsModeratorItem.setVisibility(View.GONE);
            timeoutItem.setVisibility(View.GONE);
            removeTimeoutItem.setVisibility(View.GONE);
            banItem.setVisibility(View.GONE);
            unbanItem.setVisibility(View.GONE);
        }
    }

    @Override
    public void initViewModel() {
        super.initViewModel();
        viewModel = new ViewModelProvider(this).get(UserManageViewModel.class);
        livingViewModel = new ViewModelProvider(mContext).get(LivingViewModel.class);

        viewModel.getChatRoomObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(EMChatRoom data) {
                    if (null != RoomUserDetailDialog.this.getActivity()) {
                        RoomUserDetailDialog.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChatRoom = data;
                                updateView(false);
                            }
                        });
                    }
                }
            });
        });

        LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER, Boolean.class).observe(getViewLifecycleOwner(), event -> {
            if (event != null && event) {
                updateChatRoom(false);
            }
        });

        LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER_STATE, Boolean.class).observe(getViewLifecycleOwner(), event -> {
            if (event != null && event) {
                updateChatRoom(false);
            }
        });
    }


    @Override
    public void initListener() {
        super.initListener();
        List<String> list = new ArrayList<>(1);
        list.add(username);

        RxView.clicks(moveToAllowedListItem).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {
                        showToast("move to allowed list");
                        viewModel.addToChatRoomWhiteList(roomId, list);
                    }
                });

        RxView.clicks(removeFromAllowedListItem).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {
                        showToast("remove from allowed list");
                        viewModel.removeFromChatRoomWhiteList(roomId, list);
                    }
                });


        RxView.clicks(assignAsModeratorItem).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {
                        showToast("assign as moderator");
                        viewModel.addChatRoomAdmin(roomId, username);
                        viewModel.fetchChatRoom(roomId);
                    }
                });

        RxView.clicks(removeAsModeratorItem).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {
                        showToast("remove as moderator");
                        viewModel.removeChatRoomAdmin(roomId, username);
                    }
                });

        RxView.clicks(timeoutItem).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {
                        new MaterialDialog.Builder(mContext)
                                .title(mContext.getString(R.string.dialog_list_timeout_title, username))
                                .positiveText(R.string.timeout)
                                .positiveColor(getResources().getColor(R.color.button_color))
                                .negativeText(R.string.cancel)
                                .negativeColor(getResources().getColor(R.color.button_color))
                                .items(R.array.timeout_labels)
                                .itemsColor(getResources().getColor(R.color.dialog_list_item_color))
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        if (-1 != which) {
                                            int[] timeoutValues = mContext.getResources().getIntArray(R.array.timeout_values);
                                            final int timeoutTime = timeoutValues[which];

                                            new MaterialDialog.Builder(mContext)
                                                    .content(mContext.getString(R.string.dialog_timeout_content, username))
                                                    .contentColor(getResources().getColor(R.color.black))
                                                    .positiveText(R.string.timeout)
                                                    .positiveColor(getResources().getColor(R.color.button_color))
                                                    .negativeText(R.string.cancel)
                                                    .negativeColor(getResources().getColor(R.color.button_color))
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            viewModel.muteChatRoomMembers(roomId, list, timeoutTime);
                                                            AttentionBean attention = new AttentionBean();
                                                            attention.setShowTime(10);
                                                            attention.setAlert(true);
                                                            attention.setShowContent(mContext.getString(R.string.room_manager_timeout_attention_tip, username, text));
                                                            LiveDataBus.get().with(DemoConstants.REFRESH_ATTENTION).postValue(attention);
                                                        }
                                                    })
                                                    .show();
                                        }
                                        return true;
                                    }
                                })
                                .show();
                    }
                });


        RxView.clicks(removeTimeoutItem).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {
                        showToast("remove timeout");
                        viewModel.unMuteChatRoomMembers(roomId, list);
                    }
                });

        RxView.clicks(banItem).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {

                        new MaterialDialog.Builder(mContext)
                                .content(mContext.getString(R.string.room_manager_ban_tip, username))
                                .contentColor(getResources().getColor(R.color.black))
                                .positiveText(R.string.ok)
                                .positiveColor(getResources().getColor(R.color.button_color))
                                .negativeText(R.string.cancel)
                                .negativeColor(getResources().getColor(R.color.button_color))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        viewModel.banChatRoomMembers(roomId, list);
                                        AttentionBean attention = new AttentionBean();
                                        attention.setShowTime(10);
                                        attention.setAlert(false);
                                        attention.setShowContent(mContext.getString(R.string.room_manager_ban_attention_tip, username));
                                        LiveDataBus.get().with(DemoConstants.REFRESH_ATTENTION).postValue(attention);
                                    }
                                })
                                .show();
                    }
                });


        RxView.clicks(unbanItem).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {
                        showToast("unban");
                        viewModel.unbanChatRoomMembers(roomId, list);
                    }
                });
    }

    @Override
    public void initData() {
        super.initData();
        mChatRoomManager = EMClient.getInstance().chatroomManager();
        updateChatRoom(true);
    }

    private void updateChatRoom(boolean initView) {
        mChatRoom = mChatRoomManager.getChatRoom(roomId);
        updateView(initView);
    }

    private void showToast(String msg) {
        Toast.makeText(mContext.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.item_timeout_all) {
            AttentionBean attention = new AttentionBean();
            if (isChecked) {
                viewModel.muteAllMembers(roomId);
                //viewModel.muteChatRoomMembers(roomId, mChatRoom.getMemberList(), Integer.MAX_VALUE);
                attention.setShowTime(-1);
                attention.setAlert(true);
                attention.setShowContent(RoomUserDetailDialog.this.getResources().getString(R.string.attention_timeout_all));
            } else {
                viewModel.unMuteAllMembers(roomId);
                //viewModel.unMuteChatRoomMembers(roomId, mChatRoom.getMemberList());
                attention.setShowTime(10);
                attention.setAlert(false);
                attention.setShowContent(RoomUserDetailDialog.this.getResources().getString(R.string.attention_remove_timeout_all));
            }
            LiveDataBus.get().with(DemoConstants.REFRESH_ATTENTION).postValue(attention);
            LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER_STATE).postValue(true);
        }
    }
}
