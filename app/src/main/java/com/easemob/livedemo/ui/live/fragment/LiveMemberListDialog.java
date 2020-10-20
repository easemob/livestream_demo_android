package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.common.OnItemClickListener;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.base.BaseLiveDialogFragment;
import com.easemob.livedemo.ui.live.adapter.LiveMemberAdapter;
import com.easemob.livedemo.ui.live.viewmodels.LiveMemberListViewModel;
import com.easemob.livedemo.ui.live.viewmodels.LivingViewModel;

import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class LiveMemberListDialog extends BaseLiveDialogFragment {
    private TextView tvMemberNum;
    private RecyclerView rvList;
    private LiveMemberAdapter adapter;
    private String chatRoomId;
    private OnMemberItemClickListener listener;
    private LivingViewModel viewModel;

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
    public void initViewModel() {
        super.initViewModel();
        viewModel = new ViewModelProvider(mContext).get(LivingViewModel.class);
        viewModel.getMemberNumberObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {
                    tvMemberNum.setText(getString(R.string.em_live_member_num, data.getAudienceNum()));
                    LinkedList<String> memberList = data.getMemberList(DemoConstants.MAX_SHOW_MEMBERS_COUNT);
                    adapter.setData(memberList);
                }
            });
        });
//        LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER, Boolean.class).observe(getViewLifecycleOwner(), event -> {
//            if(event != null && event) {
//                getMemberList();
//            }
//        });
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
        LiveDataBus.get().with(DemoConstants.REFRESH_MEMBER_COUNT).postValue(true);
    }

    public void setOnItemClickListener(OnMemberItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnMemberItemClickListener {
        void OnMemberItemClick(View view, int position, String member);
    }
}
