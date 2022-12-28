package com.easemob.livedemo.ui.live.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMChatRoomManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.callback.OnResourceParseCallback;
import com.easemob.livedemo.common.livedata.LiveDataBus;
import com.easemob.livedemo.ui.base.BaseLiveDialogFragment;
import com.easemob.livedemo.ui.live.viewmodels.UserManageViewModel;
import com.easemob.livedemo.utils.StatusBarCompat;


public class LiveMemberListDialog extends BaseLiveDialogFragment {
    private RecyclerView mUserListView;
    private UserListAdapter mUserListAdapter;
    private String chatRoomId;
    private UserManageViewModel viewModel;

    protected EMChatRoomManager mChatRoomManager;
    protected EMChatRoom mChatRoom;

    private List<String> mUserListData;
    private List<String> mMuteListData;
    private List<String> mAdminListData;

    public static LiveMemberListDialog getNewInstance(String chatRoomId) {
        LiveMemberListDialog dialog = new LiveMemberListDialog();
        Bundle bundle = new Bundle();
        bundle.putString("chatRoomId", chatRoomId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_live_member_list;
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if (bundle != null) {
            chatRoomId = bundle.getString("chatRoomId");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            final float screenHeight = EaseCommonUtils.getScreenInfo(mContext)[1];
            final int navBarHeight = StatusBarCompat.getNavigationBarHeight(mContext);
            lp.height = (int) screenHeight * 2 / 5 + navBarHeight;
            dialogWindow.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mUserListView = findViewById(R.id.rv_user_list);

        mUserListView.setLayoutManager(new LinearLayoutManager(mContext));

        mUserListAdapter = new UserListAdapter();
        mUserListAdapter.hideEmptyView(true);
        mUserListView.setAdapter(mUserListAdapter);
        mUserListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LiveMemberListDialog.this.dismiss();
                LiveDataBus.get().with(DemoConstants.SHOW_USER_DETAIL).postValue(mUserListAdapter.getItem(position));
            }
        });
    }

    @Override
    public void initViewModel() {
        super.initViewModel();
        viewModel = new ViewModelProvider(mContext).get(UserManageViewModel.class);
        viewModel.getChatRoomObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(EMChatRoom data) {
                    mChatRoom = data;
                }
            });
        });

        LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER, Boolean.class).observe(getViewLifecycleOwner(), event -> {
            if (event != null && event) {
                updateChatRoom();
            }
        });

        LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER_STATE, Boolean.class).observe(getViewLifecycleOwner(), event -> {
            if (event != null && event) {
                updateChatRoom();
            }
        });
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    public void initData() {
        super.initData();
        mChatRoomManager = EMClient.getInstance().chatroomManager();
        updateChatRoom();

        mUserListData = new ArrayList<>();
        mMuteListData = new ArrayList<>();
        mAdminListData = new ArrayList<>();

        updateUserList();
    }

    private void updateChatRoom() {
        mChatRoom = mChatRoomManager.getChatRoom(chatRoomId);
    }

    private void updateUserList() {
        if (null == mChatRoom) {
            return;
        }

        mUserListAdapter.setOwner(mChatRoom.getOwner());

        mAdminListData.clear();
        mAdminListData.addAll(mChatRoom.getAdminList());
        mUserListAdapter.setAdminList(mAdminListData);

        Map<String, Long> muteMap = mChatRoom.getMuteList();
        mMuteListData.clear();
        for (Map.Entry<String, Long> entry : muteMap.entrySet()) {
            mMuteListData.add(entry.getKey());
        }
        mUserListAdapter.setMuteList(mMuteListData);

        mUserListData.clear();

        mUserListData.add(mChatRoom.getOwner());
        mUserListData.addAll(mAdminListData);
        mUserListData.addAll(mChatRoom.getMemberList());

        mUserListAdapter.setData(mUserListData);
    }

    private static class UserListAdapter extends EaseBaseRecyclerViewAdapter<String> {
        private static List<String> adminList;
        private static List<String> muteList;
        private static String owner;

        public UserListAdapter() {
        }

        public static void setOwner(String owner) {
            UserListAdapter.owner = owner;
        }

        public static void setAdminList(List<String> adminList) {
            UserListAdapter.adminList = adminList;
        }

        public void setMuteList(List<String> muteList) {
            UserListAdapter.muteList = muteList;
        }


        @Override
        public UserListViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_list, parent, false);
            return new UserListViewHolder(view, mContext);
        }

        private static class UserListViewHolder extends ViewHolder<String> {
            private View layout;
            private ImageView ivUserAvatar;
            private TextView tvUserName;
            private ImageView ivRoleType;
            private ImageView roleState;
            private Context context;

            public UserListViewHolder(@NonNull View itemView, Context context) {
                super(itemView);
                this.context = context;
            }

            @Override
            public void initView(View itemView) {
                layout = findViewById(R.id.layout);
                ivUserAvatar = findViewById(R.id.iv_user_avatar);
                tvUserName = findViewById(R.id.tv_user_name);
                ivRoleType = findViewById(R.id.iv_role_type);
                roleState = findViewById(R.id.iv_state_icon);
            }

            @Override
            public void setData(String item, int position) {
                EaseUserUtils.setUserNick(item, tvUserName);
                EaseUserUtils.setUserAvatar(context, item, ivUserAvatar);

                if (!TextUtils.isEmpty(owner) && owner.contains(item)) {
                    ivRoleType.setImageResource(R.drawable.live_streamer);
                    ivRoleType.setVisibility(View.VISIBLE);
                } else if (null != adminList && adminList.contains(item)) {
                    ivRoleType.setImageResource(R.drawable.live_moderator);
                    ivRoleType.setVisibility(View.VISIBLE);
                } else {
                    ivRoleType.setVisibility(View.GONE);
                }

                if (null != muteList && muteList.contains(item)) {
                    roleState.setVisibility(View.VISIBLE);
                    roleState.setImageResource(R.drawable.mute);
                } else {
                    roleState.setVisibility(View.GONE);
                }

                int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                ivRoleType.measure(spec, spec);
                final int roleTypeWidth = View.VISIBLE == ivRoleType.getVisibility() ?
                        ivRoleType.getMeasuredWidth() : 0;

                spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                roleState.measure(spec, spec);
                final int roleStateWidth = View.VISIBLE == roleState.getVisibility() ?
                        roleState.getMeasuredWidth() : 0;

                layout.post(new Runnable() {
                    @Override
                    public void run() {
                        int nicknameMaxWidth = layout.getWidth() - layout.getPaddingStart() - layout.getPaddingEnd() -
                                ((RecyclerView.LayoutParams) layout.getLayoutParams()).getMarginStart() - ((RecyclerView.LayoutParams) layout.getLayoutParams()).getMarginEnd() -
                                ivUserAvatar.getWidth() - ivUserAvatar.getPaddingStart() - ivUserAvatar.getPaddingEnd() -
                                ((RelativeLayout.LayoutParams) ivUserAvatar.getLayoutParams()).getMarginStart() - ((RelativeLayout.LayoutParams) ivUserAvatar.getLayoutParams()).getMarginEnd() -
                                ((RelativeLayout.LayoutParams) tvUserName.getLayoutParams()).getMarginStart() - ((RelativeLayout.LayoutParams) tvUserName.getLayoutParams()).getMarginEnd();

                        if (View.VISIBLE == ivRoleType.getVisibility()) {
                            nicknameMaxWidth = nicknameMaxWidth -
                                    roleTypeWidth - ivRoleType.getPaddingLeft() - ivRoleType.getPaddingRight() -
                                    ((RelativeLayout.LayoutParams) ivRoleType.getLayoutParams()).getMarginStart() - ((RelativeLayout.LayoutParams) ivRoleType.getLayoutParams()).getMarginEnd();
                        }

                        if (View.VISIBLE == roleState.getVisibility()) {
                            nicknameMaxWidth = nicknameMaxWidth -
                                    roleStateWidth - roleState.getPaddingLeft() - roleState.getPaddingRight() -
                                    ((RelativeLayout.LayoutParams) roleState.getLayoutParams()).getMarginStart() - ((RelativeLayout.LayoutParams) roleState.getLayoutParams()).getMarginEnd();
                        }
                        tvUserName.setMaxWidth(nicknameMaxWidth);
                    }
                });
            }
        }
    }

    private static class UserListSpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public UserListSpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view,
                                   RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            } else {
                outRect.top = 0;
            }
        }
    }
}
