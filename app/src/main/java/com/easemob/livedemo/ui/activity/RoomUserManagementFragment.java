package com.easemob.livedemo.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMChatRoomManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.easemob.livedemo.ui.activity.RoomUserManagementFragment.ManagementType.ADMIN;
import static com.easemob.livedemo.ui.activity.RoomUserManagementFragment.ManagementType.MUTE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomUserManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomUserManagementFragment extends Fragment {
    private ManagementType type;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private EMChatRoomManager chatRoomManager;
    private String chatroomId;

    public RoomUserManagementFragment() {
        // Required empty public constructor
    }

    public static RoomUserManagementFragment newInstance(String chatroomId, ManagementType type) {
        RoomUserManagementFragment fragment = new RoomUserManagementFragment();
        Bundle args = new Bundle();
        args.putSerializable("ManagementType", type);
        args.putString("chatroomId", chatroomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = (ManagementType) getArguments().getSerializable("ManagementType");
            chatroomId = getArguments().getString("chatroomId");
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_user_management, container, false);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chatRoomManager = EMClient.getInstance().chatroomManager();
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_layout);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));

        fetchData();

    }

    private void fetchData(){
        refreshLayout.setRefreshing(true);
        executeFetchTask();
    }

    private void executeFetchTask() {
        ThreadPoolManager.getInstance().executeTask(new ThreadPoolManager.Task<List<String>>() {
            @Override public List<String> onRequest() throws HyphenateException {
                if(type == ADMIN){
                    EMChatRoom chatRoom = chatRoomManager.fetchChatRoomFromServer(chatroomId);
                    return chatRoom.getAdminList();
                }else if(type == MUTE){
                    Map<String, Long> map = chatRoomManager.fetchChatRoomMuteList(chatroomId, 1, 50);
                    List<String> list = new ArrayList<String>(map.keySet());
                    return list;
                }else{
                    return chatRoomManager.fetchChatRoomBlackList(chatroomId, 1, 50);
                }
            }

            @Override public void onSuccess(List<String> list) {
                setAdapter(list);
            }

            @Override public void onError(HyphenateException exception) {

            }
        });
    }

    private void setAdapter(List<String> list){
        refreshLayout.setRefreshing(false);
        ManagementAdapter adapter = new ManagementAdapter(getActivity(), list, type);
        recyclerView.setAdapter(adapter);
    }

    private class ManagementAdapter extends RecyclerView.Adapter<ManagementViewHolder>{
        private ManagementType type;
        private Context context;
        private List<String> userList;

        public ManagementAdapter(Context context, List<String> userList, ManagementType type){
            this.userList = userList;
            this.type = type;
            this.context = context;
        }


        @Override public ManagementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ManagementViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_room_user_manager_item, parent, false));
        }

        @Override public void onBindViewHolder(ManagementViewHolder holder, final int position) {
            final String username = userList.get(position);
            holder.usernickView.setText(username);
            switch (type) {
                case ADMIN:
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
                            if(type == ManagementType.ADMIN){
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
                            notifyDataSetChanged();
                        }

                        @Override public void onError(HyphenateException exception) {
                            Toast.makeText(context, "操作失败：" + exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }

        @Override public int getItemCount() {
            return userList.size();
        }
    }

    static class ManagementViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_usernick) TextView usernickView;
        @BindView(R.id.btn_manager) TextView managerButton;

        public ManagementViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public enum ManagementType {
        ADMIN,
        MUTE,
        BLACKLIST
    }
}
