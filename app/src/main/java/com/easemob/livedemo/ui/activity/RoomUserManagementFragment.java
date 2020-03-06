package com.easemob.livedemo.ui.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.ui.live.fragment.RoomMemberManageFragment;
import com.easemob.livedemo.ui.live.fragment.RoomMuteManageFragment;
import com.easemob.livedemo.ui.live.fragment.RoomWhiteManageFragment;
import com.easemob.livedemo.ui.viewmodels.UserManageViewModel;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMChatRoomManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.exceptions.HyphenateException;
import java.util.ArrayList;
import java.util.List;

import static com.easemob.livedemo.ui.activity.RoomUserManagementFragment.ManagementType.BLACKLIST;
import static com.easemob.livedemo.ui.activity.RoomUserManagementFragment.ManagementType.MUTE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomUserManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomUserManagementFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private ManagementType type;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    protected EMChatRoomManager chatRoomManager;
    protected String chatroomId;
    protected ManagementAdapter adapter;
    protected UserManageViewModel viewModel;
    protected EMChatRoom chatRoom;
    protected boolean isAllMuted;
    protected List<String> muteList;

    public RoomUserManagementFragment() {
        // Required empty public constructor
    }

    public static RoomUserManagementFragment newInstance(String chatroomId, ManagementType type) {
        RoomUserManagementFragment fragment;
        if(type == BLACKLIST) {
            fragment = new RoomWhiteManageFragment();
        }else if(type == MUTE) {
            fragment = new RoomMuteManageFragment();
        }else {
            fragment = new RoomMemberManageFragment();
        }
        Bundle args = new Bundle();
        args.putSerializable("ManagementType", type);
        args.putString("chatroomId", chatroomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = (ManagementType) getArguments().getSerializable("ManagementType");
            chatroomId = getArguments().getString("chatroomId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_user_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycleview);
        refreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatRoomManager = EMClient.getInstance().chatroomManager();
        chatRoom = chatRoomManager.getChatRoom(chatroomId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        adapter = new ManagementAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        initViewModel();
        fetchData();
    }

    protected void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(UserManageViewModel.class);

        viewModel.getChatRoomObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(EMChatRoom data) {
                    chatRoom = data;
                    isAllMuted = chatRoom.isAllMemberMuted();
                    executeFetchTask();
                }
            });
        });
    }

    private void fetchData(){
//        refreshLayout.setRefreshing(true);
//        executeFetchTask();
    }

    /**
     * 请求数据
     */
    protected void executeFetchTask() {}

    /**
     * 设置数据
     * @param list
     */
    protected void setAdapter(List<String> list){
        adapter.setData(list);
    }

    /**
     * 停止刷新
     */
    protected void finishRefresh() {
        if(refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        executeFetchTask();
    }

    protected class ManagementAdapter extends RecyclerView.Adapter<ManagementAdapter.ManagementViewHolder>{
        private Context context;
        private List<String> userList;

        public ManagementAdapter(Context context){
            this.context = context;
        }

        public ManagementAdapter(Context context, List<String> userList){
            this.userList = userList;
            this.context = context;
        }

        @Override
        public ManagementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ManagementViewHolder(LayoutInflater.from(context).inflate(R.layout.em_layout_live_member_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ManagementViewHolder holder, final int position) {
            final String username = userList.get(position);
            holder.usernickView.setText(DemoHelper.getNickName(username));
            holder.imgAvatar.setImageResource(DemoHelper.getAvatarResource(username, R.drawable.ease_default_avatar));
            showOtherInfo(holder, userList, position);
        }

        @Override
        public int getItemCount() {
            return userList == null ? 0 : userList.size();
        }

        public void setData(List<String> data) {
            this.userList = data;
            notifyDataSetChanged();
        }

        public class ManagementViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.img_avatar)
            public EaseImageView imgAvatar;
            @BindView(R.id.txt_usernick)
            public TextView usernickView;
            @BindView(R.id.btn_manager)
            public TextView managerButton;
            @BindView(R.id.tv_label)
            public TextView tvLabel;
            @BindView(R.id.switch_mute)
            public Switch switchMute;
            @BindView(R.id.tv_mute_hint)
            public TextView tvMuteHint;

            public ManagementViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

        }
    }

    protected void showOtherInfo(ManagementAdapter.ManagementViewHolder holder, List<String> userList, int position) {
        String username = userList.get(position);
        switch (type) {
            case MEMBER:
                EMChatRoom chatRoom = chatRoomManager.getChatRoom(chatroomId);
                if(chatRoom.getAdminList().contains(EMClient.getInstance().getCurrentUser())){
                    holder.managerButton.setVisibility(View.INVISIBLE);
                }else {
                    holder.managerButton.setVisibility(View.VISIBLE);
                    holder.managerButton.setText("移除房管");
                }
                break;
            case MUTE:
                holder.managerButton.setText("解除禁言");
                break;
            case BLACKLIST:
                holder.managerButton.setText("移除黑名单");
                break;
        }

        holder.managerButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final List<String> list = new ArrayList<>();
                list.add(username);
                ThreadPoolManager.getInstance().executeTask(new ThreadPoolManager.Task<Void>() {
                    @Override public Void onRequest() throws HyphenateException {
                        if(type == ManagementType.MEMBER){
                            chatRoomManager.removeChatRoomAdmin(chatroomId, username);
                        }else if(type == MUTE){
                            chatRoomManager.unMuteChatRoomMembers(chatroomId, list);
                        }else{
                            chatRoomManager.unblockChatRoomMembers(chatroomId, list);
                        }
                        return null;
                    }

                    @Override public void onSuccess(Void aVoid) {
                        userList.remove(username);
                        adapter.notifyDataSetChanged();
                    }

                    @Override public void onError(HyphenateException exception) {
                        Toast.makeText(mContext, "操作失败：" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public enum ManagementType {
        MEMBER,
        MUTE,
        BLACKLIST
    }
}
