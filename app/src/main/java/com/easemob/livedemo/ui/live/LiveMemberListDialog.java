package com.easemob.livedemo.ui.live;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.common.OnItemClickListener;
import com.easemob.livedemo.ui.activity.BaseDialogFragment;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class LiveMemberListDialog extends BaseDialogFragment {
    private TextView tvMemberNum;
    private RecyclerView rvList;
    private LiveMemberAdapter adapter;
    private String chatRoomId;
    private OnMemberItemClickListener listener;

    public static LiveMemberListDialog getNewInstance(String chatRoomId) {
        LiveMemberListDialog dialog = new LiveMemberListDialog();
        Bundle bundle = new Bundle();
        bundle.putString("username", chatRoomId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public int getLayoutId() {
        return R.layout.em_fragment_live_member_list;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Window dialogWindow = getDialog().getWindow();
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            dialogWindow.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            chatRoomId = bundle.getString("username");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvMemberNum = findViewById(R.id.tv_member_num);
        rvList = findViewById(R.id.rv_list);

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new LiveMemberAdapter();
        rvList.setAdapter(adapter);
        rvList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void initListener() {
        super.initListener();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(listener != null) {
                    listener.OnMemberItemClick(view, position, adapter.getItem(position));
                }
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        getMemberList();
    }

    private void getMemberList() {
        ThreadPoolManager.getInstance().executeTask(new ThreadPoolManager.Task<List<String>>() {
            @Override public List<String> onRequest() throws HyphenateException {
                EMChatRoom chatRoom = EMClient.getInstance().chatroomManager().fetchChatRoomFromServer(chatRoomId, true);
                return chatRoom.getMemberList();
            }

            @Override public void onSuccess(List<String> list) {
                tvMemberNum.setText(getString(R.string.em_live_member_num, list.size()));
                adapter.setData(list);
            }

            @Override public void onError(HyphenateException exception) {

            }
        });
    }

    public void setOnItemClickListener(OnMemberItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnMemberItemClickListener {
        void OnMemberItemClick(View view, int position, String member);
    }
}
