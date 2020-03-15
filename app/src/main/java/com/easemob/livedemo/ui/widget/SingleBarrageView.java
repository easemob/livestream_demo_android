package com.easemob.livedemo.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.custommessage.EmCustomMsgHelper;
import com.easemob.custommessage.EmCustomMsgType;
import com.easemob.custommessage.MsgConstant;
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.MessageBean;
import com.easemob.livedemo.ui.widget.barrage.BarrageAdapter;
import com.easemob.livedemo.ui.widget.barrage.BarrageView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleBarrageView extends BarrageView {
    private BarrageAdapter<MessageBean> mAdapter;
    private EMConversation conversation;
    private String chatId;

    public SingleBarrageView(Context context) {
        super(context);
    }

    public SingleBarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleBarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initBarrage() {
        BarrageView.Options options = new BarrageView.Options()
                .setGravity(BarrageView.GRAVITY_TOP)                // 设置弹幕的位置
                .setInterval(100)                                     // 设置弹幕的发送间隔
                .setSpeed(200,29)                   // 设置速度和波动值
                .setModel(BarrageView.MODEL_COLLISION_DETECTION)     // 设置弹幕生成模式
                .setRepeat(1)                                       // 循环播放 默认为1次 -1 为无限循环
                .setClick(false);                                    // 设置弹幕是否可以点击
        setOptions(options);

        mAdapter = new BarrageAdapter<MessageBean>(null, getContext()) {
            @Override
            protected BarrageViewHolder<MessageBean> onCreateViewHolder(View root, int type) {
                return new SingleBarrageView.ViewHolder(root);
            }

            @Override
            public int getItemLayout(MessageBean messageBean) {
                return R.layout.em_barrage_item;
            }
        };

        setAdapter(mAdapter);
    }

    /**
     * refresh
     */
    public void refresh() {
        if(!TextUtils.isEmpty(chatId)) {
            setData(chatId);
        }
    }

    public void addData(EMMessage message) {
        MessageBean bean = new MessageBean();
        bean.setMessage(message);
        bean.setType(message.getType().ordinal());
        mAdapter.add(bean);
    }

    public void setData(String id) {
        this.chatId = id;
        conversation = EMClient.getInstance().chatManager().getConversation(id, EMConversation.EMConversationType.ChatRoom, true);
        List<EMMessage> messages = conversation.getAllMessages();
        setData(messages);
    }

    public void setData(List<EMMessage> messages) {
        if(messages != null && !messages.isEmpty()) {
            List<MessageBean> list = new ArrayList<>();
            MessageBean bean;
            for (EMMessage message : messages) {
                bean = new MessageBean();
                bean.setMessage(message);
                bean.setType(message.getType().ordinal());
                list.add(bean);
            }
            mAdapter.addList(list);
        }
    }

    public class ViewHolder extends BarrageAdapter.BarrageViewHolder<MessageBean> {
        private ImageView mHeadView;
        private TextView mContent;

        public ViewHolder(View itemView) {
            super(itemView);

            mHeadView = itemView.findViewById(R.id.image);
            mContent = itemView.findViewById(R.id.content);
        }

        @Override
        protected void onBind(MessageBean data) {
            String barrageTxt = EmCustomMsgHelper.getInstance().getMsgBarrageTxt(data.getMessage());
            if(!TextUtils.isEmpty(barrageTxt)) {
                mContent.setText(barrageTxt);
            }
        }
    }
}
