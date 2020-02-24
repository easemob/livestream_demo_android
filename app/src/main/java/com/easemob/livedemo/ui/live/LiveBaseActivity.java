package com.easemob.livedemo.ui.live;

import android.animation.ObjectAnimator;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;

import com.bumptech.glide.Glide;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.common.OnItemClickListener;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ucloud.LiveCameraView;
import com.easemob.livedemo.ui.activity.BaseActivity;
import com.easemob.livedemo.ui.activity.RoomUserDetailsDialog;
import com.easemob.livedemo.ui.activity.RoomUserManagementDialog;
import com.easemob.livedemo.ui.live.adapter.MemberAvatarAdapter;
import com.easemob.livedemo.ui.widget.PeriscopeLayout;
import com.easemob.livedemo.ui.widget.RoomMessagesView;
import com.easemob.livedemo.ui.widget.ShowGiveGiftView;
import com.easemob.livedemo.ui.widget.SingleBarrageView;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wei on 2016/6/12.
 */
public abstract class LiveBaseActivity extends BaseActivity {
    protected static final String TAG = "LiveActivity";
    ImageView coverImage;
    protected LiveRoom liveRoom;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        liveRoom = (LiveRoom) getIntent().getSerializableExtra("liveroom");
        if(liveRoom == null) {
            finish();
            return;
        }
        onActivityCreated(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    protected abstract void onActivityCreated(@Nullable Bundle savedInstanceState);

    protected void initView() {
        coverImage = findViewById(R.id.cover_image);
        Log.e("TAG", "cover"+liveRoom.getCover());
        Glide.with(mContext)
                .load(liveRoom.getCover())
                .error(R.drawable.em_live_default_bg)
                .into(coverImage);
    }

    protected void initListener() {}

    protected void initData() {}
}
