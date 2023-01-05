package com.easemob.livedemo.ui.live.fragment;

import android.content.Context;
import android.graphics.Color;
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
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.base.BaseActivity;
import com.easemob.livedemo.ui.base.BaseLiveDialogFragment;
import com.easemob.livedemo.ui.live.viewmodels.LivingViewModel;
import com.easemob.livedemo.ui.live.viewmodels.UserManageViewModel;
import com.easemob.livedemo.ui.widget.CustomConstraintLayout;
import com.easemob.livedemo.utils.StatusBarCompat;

public class RoomUserManagementDialog extends BaseLiveDialogFragment implements CustomConstraintLayout.OnGestureChangeListener {
    private BaseActivity mContext;
    private String chatroomId;
    private UserManageViewModel viewModel;
    protected EMChatRoomManager mChatRoomManager;
    protected EMChatRoom mChatRoom;

    private CustomConstraintLayout mLayout;
    private RecyclerView mRoleTypeView;
    private RecyclerView mUserListView;
    private RoleTypeAdapter mRoleTypeAdapter;
    private UserListAdapter mUserListAdapter;
    private List<String> mRoleTypeListData;
    private List<String> mUserListData;
    private List<String> mMuteListData;
    private List<String> mAdminListData;

    private String mCurrentRoleType;

    private int mRoleTypeIndex;

    public RoomUserManagementDialog() {
    }

    public RoomUserManagementDialog(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (BaseActivity) context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_room_user_management;
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

        mLayout = findViewById(R.id.layout);
        mLayout.setOnGestureChangeListener(this);

        mRoleTypeView = findViewById(R.id.rv_role_type_list);
        mUserListView = findViewById(R.id.rv_user_list);

        LinearLayoutManager ms = new LinearLayoutManager(mContext);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRoleTypeView.setLayoutManager(ms);

        mRoleTypeAdapter = new RoleTypeAdapter();

        mRoleTypeView.setAdapter(mRoleTypeAdapter);
        mRoleTypeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mRoleTypeIndex = position;
                updateRoteType();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mUserListView.setLayoutManager(linearLayoutManager);

        mUserListAdapter = new UserListAdapter();
        mUserListAdapter.hideEmptyView(true);
        mUserListView.setAdapter(mUserListAdapter);
        mUserListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RoomUserManagementDialog.this.dismiss();
                LiveDataBus.get().with(DemoConstants.SHOW_USER_DETAIL).postValue(mUserListAdapter.getItem(position));
            }
        });

    }

    private void updateRoteType() {
        mCurrentRoleType = mRoleTypeAdapter.getItem(mRoleTypeIndex);
        mRoleTypeAdapter.setCurrentRoleType(mCurrentRoleType);
        updateUserList();
        mRoleTypeView.smoothScrollToPosition(mRoleTypeIndex);
    }

    @Override
    public void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(UserManageViewModel.class);
        LivingViewModel livingViewModel = new ViewModelProvider(mContext).get(LivingViewModel.class);
        livingViewModel.getMemberNumberObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {

                }
            });
        });

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
    public void initData() {
        super.initData();
        mChatRoomManager = EMClient.getInstance().chatroomManager();
        updateChatRoom();

        mRoleTypeIndex = 0;
        mRoleTypeListData = new ArrayList<>(5);
        mRoleTypeListData.add(mContext.getString(R.string.live_role_type_all));
        mRoleTypeListData.add(mContext.getString(R.string.live_role_type_moderators));
        mRoleTypeListData.add(mContext.getString(R.string.live_role_type_allowed));
        mRoleTypeListData.add(mContext.getString(R.string.live_role_type_timeout));
        mRoleTypeListData.add(mContext.getString(R.string.live_role_type_banned));

        mCurrentRoleType = mRoleTypeListData.get(0);
        mRoleTypeAdapter.setCurrentRoleType(mCurrentRoleType);
        mRoleTypeAdapter.setData(mRoleTypeListData);

        mUserListData = new ArrayList<>();
        mMuteListData = new ArrayList<>();
        mAdminListData = new ArrayList<>();

        updateUserList();
    }

    private void updateChatRoom() {
        mChatRoom = mChatRoomManager.getChatRoom(chatroomId);
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
        if (mContext.getString(R.string.live_role_type_all).equals(mCurrentRoleType)) {
            mUserListData.add(mChatRoom.getOwner());
            mUserListData.addAll(mChatRoom.getAdminList());
            mUserListData.addAll(mChatRoom.getMemberList());
        } else if (mContext.getString(R.string.live_role_type_moderators).equals(mCurrentRoleType)) {
            mUserListData.addAll(mAdminListData);
        } else if (mContext.getString(R.string.live_role_type_allowed).equals(mCurrentRoleType)) {
            mUserListData.addAll(mChatRoom.getWhitelist());
        } else if (mContext.getString(R.string.live_role_type_timeout).equals(mCurrentRoleType)) {
            mUserListData.addAll(mMuteListData);
        } else if (mContext.getString(R.string.live_role_type_banned).equals(mCurrentRoleType)) {
            mUserListData.addAll(mChatRoom.getBlacklist());
        }
        mUserListAdapter.setData(mUserListData);
    }

    @Override
    public void scrollLeft() {
        if (mRoleTypeIndex >= 0 && mRoleTypeIndex < mRoleTypeListData.size() - 1) {
            mRoleTypeIndex++;
            updateRoteType();
        }
    }

    @Override
    public void scrollRight() {
        if (mRoleTypeIndex > 0 && mRoleTypeIndex <= mRoleTypeListData.size() - 1) {
            mRoleTypeIndex--;
            updateRoteType();
        }

    }

    private static class RoleTypeAdapter extends EaseBaseRecyclerViewAdapter<String> {

        private static String mCurrentRoleType;

        public void setCurrentRoleType(String currentRoleType) {
            mCurrentRoleType = currentRoleType;
            notifyDataSetChanged();
        }

        @Override
        public RoleTypeViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_role_type, parent, false);
            return new RoleTypeViewHolder(view);
        }

        private static class RoleTypeViewHolder extends ViewHolder<String> {
            private TextView roleType;
            private ConstraintLayout layout;

            public RoleTypeViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void initView(View itemView) {
                layout = findViewById(R.id.layout);
                roleType = findViewById(R.id.role_type);
            }

            @Override
            public void setData(String item, int position) {
                roleType.setText(item);
                if (!TextUtils.isEmpty(mCurrentRoleType) && mCurrentRoleType.equals(item)) {
                    layout.setBackgroundResource(R.drawable.bg_role_type_checked);
                } else {
                    layout.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
    }

    private static class UserListAdapter extends EaseBaseRecyclerViewAdapter<String> {
        private static String owner;
        private static List<String> adminList;
        private static List<String> muteList;

        public UserListAdapter() {
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public void setAdminList(List<String> adminList) {
            this.adminList = adminList;
        }

        public void setMuteList(List<String> muteList) {
            this.muteList = muteList;
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
                if (item.equals(owner)) {
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
