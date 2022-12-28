package com.easemob.livedemo.ui.live.fragment;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easemob.livedemo.ui.live.ChatRoomPresenter;
import com.easemob.livedemo.ui.live.adapter.MemberAvatarAdapter;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMUserInfo;
import com.easemob.chatroom.EaseChatRoomMessagesView;
import com.easemob.chatroom.OnLiveMessageListener;
import com.easemob.chatroom.OnSendLiveMessageCallBack;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.callback.OnResourceParseCallback;
import com.easemob.livedemo.common.inf.OnUpdateUserInfoListener;
import com.easemob.livedemo.common.livedata.LiveDataBus;
import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.common.utils.DemoMsgHelper;
import com.easemob.livedemo.common.utils.ThreadManager;
import com.easemob.livedemo.data.model.AttentionBean;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.model.User;
import com.easemob.livedemo.data.repository.UserRepository;
import com.easemob.livedemo.ui.base.BaseLiveFragment;
import com.easemob.livedemo.ui.live.viewmodels.LivingViewModel;
import com.easemob.livedemo.ui.live.viewmodels.UserManageViewModel;
import com.easemob.livedemo.ui.widget.PeriscopeLayout;
import com.easemob.livedemo.ui.widget.ShowGiveGiftView;
import com.easemob.livedemo.ui.widget.SingleBarrageView;
import com.easemob.livedemo.utils.NumberUtils;
import com.easemob.livedemo.utils.Utils;

public abstract class LiveBaseFragment extends BaseLiveFragment implements View.OnClickListener, View.OnTouchListener, ChatRoomPresenter.OnChatRoomListener, OnLiveMessageListener {
    protected static final String TAG = "lives";
    private static final int MAX_SIZE = 10;
    protected static final int CYCLE_REFRESH = 100;
    protected static final int CYCLE_REFRESH_TIME = 30000;
    protected static final int ATTENTION_REFRESH = 101;
    @BindView(R.id.layout)
    View layout;
    @BindView(R.id.iv_icon)
    EaseImageView ivIcon;
    @BindView(R.id.message_view)
    EaseChatRoomMessagesView messageView;
    @BindView(R.id.periscope_layout)
    PeriscopeLayout periscopeLayout;
    @BindView(R.id.bottom_bar)
    View bottomBar;
    @BindView(R.id.show_gift_view)
    ShowGiveGiftView barrageLayout;
    @BindView(R.id.horizontal_recycle_view)
    RecyclerView horizontalRecyclerView;
    //@BindView(R.id.new_messages_warn) ImageView newMsgNotifyImage;

    @BindView(R.id.user_manager_image)
    ImageView userManagerView;
    @BindView(R.id.switch_camera_image)
    ImageView switchCameraView;
    @BindView(R.id.like_image)
    ImageView likeImageView;
    @BindView(R.id.tv_username)
    TextView usernameView;
    @BindView(R.id.tv_member_num)
    TextView tvMemberNum;
    @BindView(R.id.tv_attention)
    TextView tvAttention;
    @BindView(R.id.toolbar)
    ViewGroup toolbar;
    @BindView(R.id.live_receive_gift)
    ImageView liveReceiveGift;
    @BindView(R.id.barrageView)
    SingleBarrageView barrageView;
    @BindView(R.id.layout_sex)
    View sexLayout;
    @BindView(R.id.sex_icon)
    ImageView sexIcon;
    @BindView(R.id.age_tv)
    TextView ageTv;
    @BindView(R.id.group_toolbar_info)
    Group toolbarGroupView;
    @BindView(R.id.close_iv)
    ImageView closeIv;
    @BindView(R.id.comment_image)
    ImageView commentIv;
    @BindView(R.id.img_bt_close)
    ImageView btEnd;
    @BindView(R.id.layout_attention)
    View layoutAttention;
    @BindView(R.id.layout_member_num)
    ConstraintLayout layoutMemberNum;


    protected LiveRoom liveRoom;
    protected EMChatRoom chatroom;
    protected String chatroomId = "";
    protected String liveId = "";
    protected String anchorId;
    protected int watchedCount;
    protected LinkedList<String> memberList = new LinkedList<>();
    private LinearLayoutManager layoutManager;
    private MemberAvatarAdapter avatarAdapter;
    protected ChatRoomPresenter presenter;
    protected LivingViewModel viewModel;
    private UserManageViewModel userManageViewModel;
    private long joinTime;
    private EMConversation mConversation;


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handleHandlerMessage(msg);
        }
    };
    private boolean isStartCycleRefresh;

    private List<String> mMemberIconList;
    private int mMessageListViewVisibility;

    protected EaseUser mLiveStreamerUser;


    @Override
    protected void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if (bundle != null) {
            liveRoom = (LiveRoom) bundle.getSerializable("liveroom");
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        Log.i(TAG, "live room=" + liveRoom);
        liveId = liveRoom.getId();
        chatroomId = liveRoom.getId();

        anchorId = liveRoom.getOwner();
        DemoMsgHelper.getInstance().init(chatroomId);
        mConversation = EMClient.getInstance().chatManager().getConversation(chatroomId, EMConversation.EMConversationType.ChatRoom, true);

        presenter = new ChatRoomPresenter(mContext, chatroomId);
        chatroom = EMClient.getInstance().chatroomManager().getChatRoom(chatroomId);

        initLiveStreamerUser();

        watchedCount = liveRoom.getAudienceNum();
        memberList = liveRoom.getMemberList();

        initWatchedMemberView();
    }

    private void initWatchedMemberView() {
        layoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(layoutManager);
        avatarAdapter = new MemberAvatarAdapter();
        avatarAdapter.hideEmptyView(true);
        horizontalRecyclerView.setAdapter(avatarAdapter);
        horizontalRecyclerView.addItemDecoration(new MemberIconSpacesItemDecoration((int) EaseCommonUtils.dip2px(mContext, -10)));

        avatarAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                skipToUserListDialog();
            }
        });
    }

    private void updateWatchedMemberView(boolean needUpdateUserInfo) {
        if (watchedCount < memberList.size()) {
            watchedCount = memberList.size();
        }
        if (null != tvMemberNum) {
            tvMemberNum.setText(NumberUtils.amountConversion(watchedCount));
        }
        if (null != memberList && memberList.size() > 0 && needUpdateUserInfo) {
            UserRepository.getInstance().fetchUserInfo(new ArrayList<>(memberList), new OnUpdateUserInfoListener() {
                @Override
                public void onSuccess(Map<String, EMUserInfo> userInfoMap) {
                    EMLog.i(TAG, "update member list user info success");
                    ThreadManager.getInstance().runOnMainThread(() -> {
                        updateWatchedMemberIcon();
                    });
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });
        } else {
            updateWatchedMemberIcon();
        }
    }

    protected abstract void initLiveStreamerUser();

    protected void initLiveStreamView() {
        if (null == mLiveStreamerUser) {
            return;
        }

        EaseUserUtils.setUserAvatar(mContext, mLiveStreamerUser.getUsername(), ivIcon);
        EaseUserUtils.setUserNick(mLiveStreamerUser.getUsername(), usernameView);

        int gender = mLiveStreamerUser.getGender();
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
        String birth = mLiveStreamerUser.getBirth();
        if (!TextUtils.isEmpty(birth)) {
            ageTv.setText(String.valueOf(Math.max(Utils.getAgeByBirthday(mLiveStreamerUser.getBirth()), 0)));
        }

        if (null != presenter) {
            presenter.setOwnerNickname(mLiveStreamerUser.getNickname());
        }
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        viewModel = new ViewModelProvider(mContext).get(LivingViewModel.class);
        userManageViewModel = new ViewModelProvider(this).get(UserManageViewModel.class);
        LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER_COUNT, Boolean.class).observe(getViewLifecycleOwner(), event -> {
            if (event != null && event) {
                viewModel.getRoomMemberNumber(chatroomId);
            }
        });
        viewModel.getMemberNumberObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {
                    liveRoom = data;
                    handler.removeMessages(CYCLE_REFRESH);
                    handler.sendEmptyMessageDelayed(CYCLE_REFRESH, CYCLE_REFRESH_TIME);
                    LiveDataBus.get().with(DemoConstants.LIVING_STATUS).postValue(data.getStatus());
                    onRoomMemberChange(data);
                    checkLiveStatus(data);
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    handler.removeMessages(CYCLE_REFRESH);
                    handler.sendEmptyMessageDelayed(CYCLE_REFRESH, CYCLE_REFRESH_TIME);
                }
            });
        });
    }


    protected void checkLiveStatus(LiveRoom data) {

    }

    @Override
    protected void initListener() {
        super.initListener();
        layout.setOnTouchListener(this);
        layoutMemberNum.setOnClickListener(this);
        liveReceiveGift.setOnClickListener(this);
        presenter.setOnChatRoomListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        mMessageListViewVisibility = View.VISIBLE;
        joinTime = System.currentTimeMillis();
        barrageView.initBarrage();
        mMemberIconList = new ArrayList<>(2);

        LiveDataBus.get().with(DemoConstants.SHOW_USER_DETAIL, String.class)
                .observe(getViewLifecycleOwner(), response -> {
                    if (!TextUtils.isEmpty(response)) {
                        showUserDetailsDialog(response);
                    }
                });
        LiveDataBus.get().with(DemoConstants.REFRESH_ATTENTION, AttentionBean.class)
                .observe(getViewLifecycleOwner(), response -> {
                    if (null == response) {
                        layoutAttention.setVisibility(View.GONE);
                    } else {
                        handler.removeMessages(ATTENTION_REFRESH);
                        if (TextUtils.isEmpty(response.getShowContent())) {
                            layoutAttention.setVisibility(View.GONE);
                        } else {
                            layoutAttention.setVisibility(View.VISIBLE);
                            if (response.isAlert()) {
                                layoutAttention.setBackgroundResource(R.drawable.live_attention_alert_bg_shape);
                            } else {
                                layoutAttention.setBackgroundResource(R.drawable.live_attention_bg_shape);
                            }
                            tvAttention.setText(response.getShowContent());
                            if (-1 != response.getShowTime()) {
                                handler.sendEmptyMessageDelayed(ATTENTION_REFRESH, response.getShowTime() * 1000);
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_member_num:
                skipToUserListDialog();
                break;
            case R.id.live_receive_gift:
                onGiftClick();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isStartCycleRefresh) {
            startCycleRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isStartCycleRefresh) {
            stopCycleRefresh();
        }
    }

    protected void startCycleRefresh() {
        if (handler != null) {
            handler.removeMessages(CYCLE_REFRESH);
            handler.sendEmptyMessageDelayed(CYCLE_REFRESH, CYCLE_REFRESH_TIME);
        }
    }

    protected void stopCycleRefresh() {
        if (handler != null) {
            handler.removeMessages(CYCLE_REFRESH);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mContext != null && mContext.isFinishing()) {
            handler.removeCallbacksAndMessages(null);
            isStartCycleRefresh = false;
        }
    }

    public void handleHandlerMessage(Message msg) {
        switch (msg.what) {
            case CYCLE_REFRESH:
                if (!TextUtils.isEmpty(chatroomId)) {
                    isStartCycleRefresh = true;
                    viewModel.getRoomMemberNumber(chatroomId);
                }
                break;
            case ATTENTION_REFRESH:
                layoutAttention.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick(R.id.comment_image)
    void onCommentImageClick() {
        showMessageListView();
    }

    private void showMessageListView() {
        if (mMessageListViewVisibility == View.VISIBLE) {
            messageView.setVisibility(View.GONE);
            commentIv.setImageResource(R.drawable.message);
        } else {
            messageView.setVisibility(View.VISIBLE);
            commentIv.setImageResource(R.drawable.message_slash);
        }
        mMessageListViewVisibility = messageView.getVisibility();
    }


    protected void onGiftClick() {
    }

    @OnClick(R.id.layout_anchor)
    protected void anchorClick() {
        showUserDetailsDialog(chatroom.getOwner());
    }

    protected void skipToUserListDialog() {
    }

    protected void showUserList() {
        LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER_COUNT).postValue(true);
        RoomUserManagementDialog dialog = (RoomUserManagementDialog) getChildFragmentManager().findFragmentByTag("RoomUserManagementDialog");
        if (dialog == null) {
            dialog = new RoomUserManagementDialog(chatroomId);
        }
        if (dialog.isAdded()) {
            return;
        }
        dialog.show(getChildFragmentManager(), "RoomUserManagementDialog");
    }

    protected void showPraise(final int count) {
        ThreadManager.getInstance().runOnMainThread(() -> {
            for (int i = 0; i < count; i++) {
                if (!mContext.isFinishing())
                    periscopeLayout.addHeart();
            }
        });
    }

    /**
     * add chat room change listener
     */
    protected void addChatRoomChangeListener() {
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(presenter);
    }

    private synchronized void onRoomMemberChange(LiveRoom room) {
        if (watchedCount != room.getAudienceNum()) {
            watchedCount = room.getAudienceNum();
            memberList = room.getMemberList();
            ThreadManager.getInstance().runOnMainThread(() -> {
                updateWatchedMemberView(true);
            });
        }

        //update chat room
        chatroom = EMClient.getInstance().chatroomManager().getChatRoom(chatroomId);
    }

    private synchronized void onRoomMemberAdded(String name) {
        EMLog.i(TAG, "onRoomMemberAdded name=" + name);
        if (null != memberList && !memberList.contains(name)) {
            watchedCount++;
            memberList.add(name);
            presenter.showMemberChangeEvent(name, mContext.getResources().getString(R.string.live_msg_member_add));

            ThreadManager.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (name.equals(chatroom.getOwner())) {
                        LiveDataBus.get().with(DemoConstants.EVENT_ANCHOR_JOIN).setValue(true);
                    }
                    updateWatchedMemberView(false);
                    if (null != messageView) {
                        messageView.refresh();
                    }
                }
            });
        }
    }

    private synchronized void onRoomMemberExited(final String name) {
        EMLog.e(TAG, "onRoomMemberExited " + name + " exited");
        if (null != memberList && memberList.contains(name)) {
            watchedCount--;
            memberList.remove(name);
            ThreadManager.getInstance().runOnMainThread(() -> {
                updateWatchedMemberView(false);
            });
        } else {
            if (name.equals(chatroom.getOwner())) {
                ThreadManager.getInstance().runOnMainThread(() -> {
                    LiveDataBus.get().with(DemoConstants.EVENT_ANCHOR_FINISH_LIVE).setValue(true);
                    LiveDataBus.get().with(DemoConstants.FRESH_LIVE_LIST).setValue(true);

                });
            }
        }
    }

    protected void updateWatchedMemberIcon() {
        mMemberIconList.clear();
        if (memberList.size() > 2) {
            mMemberIconList.add(memberList.get(memberList.size() - 1));
            mMemberIconList.add(memberList.get(memberList.size() - 2));
        } else {
            mMemberIconList.addAll(memberList);
        }
        avatarAdapter.setData(mMemberIconList);
    }

    protected void onMessageListInit() {
        if (null == messageView) {
            return;
        }
        messageView.init(chatroomId);
        messageView.setMessageViewListener(new EaseChatRoomMessagesView.MessageViewListener() {
            @Override
            public void onSendTextMessageError(int code, String msg) {
                mContext.showToast("send text message fail:" + msg);
            }

            @Override
            public void onSendBarrageMessageContent(String content) {
                presenter.sendBarrageMsg(content, new OnSendLiveMessageCallBack() {
                    @Override
                    public void onSuccess(EMMessage message) {
                        barrageView.addData(message);
                    }

                    @Override
                    public void onError(int code, String error) {

                    }
                });
            }

            @Override
            public void onChatRoomMessageItemClickListener(final EMMessage message) {
                String clickUsername = message.getFrom();
                showUserDetailsDialog(clickUsername);
            }

            @Override
            public void onHiderBottomBar(boolean hide) {
                if (hide) {
                    bottomBar.setVisibility(View.GONE);
                } else {
                    bottomBar.setVisibility(View.VISIBLE);
                }
            }
        });

        messageView.setVisibility(mMessageListViewVisibility);
        bottomBar.setVisibility(View.VISIBLE);
        if (!chatroom.getAdminList().contains(EMClient.getInstance().getCurrentUser())
                && !chatroom.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
            userManagerView.setVisibility(View.INVISIBLE);
        }
        messageView.enableInputView(!chatroom.getMuteList().containsKey(EMClient.getInstance().getCurrentUser()));
    }

    protected void showUserDetailsDialog(String username) {
        RoomUserDetailDialog fragment = (RoomUserDetailDialog) getChildFragmentManager().findFragmentByTag("RoomManageUserDialog");
        if (fragment == null) {
            fragment = RoomUserDetailDialog.getNewInstance(chatroomId, username);
        }
        if (fragment.isAdded()) {
            return;
        }
        fragment.show(getChildFragmentManager(), "RoomManageUserDialog");
    }

    protected void onWatchedMemberListInit() {
        ThreadManager.getInstance().runOnMainThread(new Runnable() {
            @Override
            public void run() {
                userManageViewModel.getObservable().observe(getViewLifecycleOwner(), response -> {
                    parseResource(response, new OnResourceParseCallback<List<String>>() {
                        @Override
                        public void onSuccess(List<String> data) {
                            if (null == memberList) {
                                memberList = new LinkedList<>();
                            } else {
                                memberList.clear();
                            }
                            boolean haveOwner = data.contains(anchorId);
                            if (haveOwner) {
                                data.remove(anchorId);
                            }
                            memberList.addAll(data);
                            updateWatchedMemberView(true);
                        }
                    });
                });
                userManageViewModel.getMembers(chatroomId);
            }
        });
    }

    private float preX, preY;

    protected void slideToLeft(int startX, float endX) {
//        startAnimation(getView(), startX, endX);
    }


    protected void slideToRight(float startX, float endX) {
//        startAnimation(getView(), startX, endX);
    }

    protected void startAnimation(View target, float startX, float endX) {
        if (target == null) {
            return;
        }
        if (target instanceof ViewGroup) {
            float x = ((ViewGroup) target).getChildAt(0).getX();
            if (x != startX) {
                return;
            }
            int childCount = ((ViewGroup) target).getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View child = ((ViewGroup) target).getChildAt(i);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(child, "translationX", startX, endX);
                    animator.setDuration(500);
                    animator.start();
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX = ev.getX();
                preY = ev.getY();
                hideSoftKeyBoard();
                break;
            case MotionEvent.ACTION_MOVE:
                float curX = ev.getX();
                float curY = ev.getY();
                float x = curX - preX;
                float y = curY - preY;
                if (Math.abs(x) > Math.abs(y) && Math.abs(x) > 20) {
                    float[] screenInfo = EaseCommonUtils.getScreenInfo(mContext);
                    if (x > 0) {
                        slideToLeft(0, screenInfo[0]);
                    } else {
                        slideToRight(screenInfo[0], 0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    private void hideSoftKeyBoard() {
        Utils.hideKeyboard(messageView);
    }

    @Override
    public void onChatRoomOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {
        // owner changed
    }

    @Override
    public void onChatRoomMemberAdded(String participant) {
        List<String> userIdList = new ArrayList<>(1);
        userIdList.add(participant);
        UserRepository.getInstance().fetchUserInfo(userIdList, new OnUpdateUserInfoListener() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> userInfoMap) {
                LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER).postValue(true);
                onRoomMemberAdded(participant);
            }

            @Override
            public void onError(int error, String errorMsg) {
                LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER).postValue(true);
                onRoomMemberAdded(participant);
            }
        });
    }

    @Override
    public void onChatRoomMemberExited(String participant) {
        LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER).postValue(true);
        onRoomMemberExited(participant);
    }

    @Override
    public void onMessageRefresh() {
        if (mContext != null && !mContext.isFinishing()) {
            mContext.runOnUiThread(() -> messageView.refresh());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (barrageLayout != null) {
            barrageLayout.destroy();
        }
    }

    private static class MemberIconSpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public MemberIconSpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view,
                                   RecyclerView parent, @NonNull RecyclerView.State state) {
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 1) {
                outRect.left = space;
            }
        }

    }

    @Override
    public void onGiftMessageReceived(EMMessage message) {
        mConversation.getMessage(message.getMsgId(), true);
        if (message.getMsgTime() >= joinTime) {
            DemoHelper.saveGiftInfo(message);
        }
        if (message.getMsgTime() < joinTime - 2000) {
            return;
        }
        String giftId = DemoMsgHelper.getInstance().getMsgGiftId(message);
        if (TextUtils.isEmpty(giftId)) {
            return;
        }
        GiftBean bean = DemoHelper.getGiftById(giftId);
        if (bean == null) {
            return;
        }
        User user = new User();
        user.setId(message.getFrom());
        bean.setUser(user);
        bean.setNum(DemoMsgHelper.getInstance().getMsgGiftNum(message));
        ThreadManager.getInstance().runOnMainThread(() -> {
            barrageLayout.showGift(bean);
        });
    }

    @Override
    public void onPraiseMessageReceived(EMMessage message) {
        mConversation.getMessage(message.getMsgId(), true);
        if (message.getMsgTime() >= joinTime) {
            DemoHelper.saveLikeInfo(message);
        }
        int likeNum = DemoMsgHelper.getInstance().getMsgPraiseNum(message);
        if (likeNum <= 0) {
            return;
        }
        showPraise(likeNum);
    }

    @Override
    public void onBarrageMessageReceived(EMMessage message) {
        mConversation.getMessage(message.getMsgId(), true);
        ThreadManager.getInstance().runOnMainThread(() -> {
            barrageView.addData(message);
        });
    }

    protected void showAttention(int time, String message, boolean isAlert) {
        AttentionBean attention = new AttentionBean();
        attention.setShowTime(time);
        attention.setAlert(isAlert);
        attention.setShowContent(message);
        LiveDataBus.get().with(DemoConstants.REFRESH_ATTENTION).postValue(attention);
    }

    protected void showMessageInputTextHint(int resId, boolean isCenter) {
        if (null != messageView) {
            messageView.getInputView().setHint(resId);
            messageView.getInputTipView().setText(resId);
            if (isCenter) {
                messageView.getInputTipView().setGravity(Gravity.CENTER);
            } else {
                messageView.getInputTipView().setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            }
        }
    }
}
