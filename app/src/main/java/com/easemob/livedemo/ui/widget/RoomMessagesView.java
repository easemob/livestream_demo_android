package com.easemob.livedemo.ui.widget;

import android.app.Activity;
import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chatroommessage.MsgConstant;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.SoftKeyboardChangeHelper;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;

import java.util.Map;

/**
 * Created by wei on 2016/6/3.
 */
public class RoomMessagesView extends RelativeLayout{
    private EMConversation conversation;
    ListAdapter adapter;

    RecyclerView listview;
    EditText editview;
    ImageView sendBtn;
    View sendContainer;
    ImageView closeView;
    Switch switchMsgType;
    boolean isBarrageMsg;
    //ImageView danmuImage;

    public boolean isBarrageShow = false;


    public RoomMessagesView(Context context) {
        super(context);
        init(context, null);
    }

    public RoomMessagesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoomMessagesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.widget_room_messages, this);
        listview = (RecyclerView) findViewById(R.id.listview);
        editview = (EditText) findViewById(R.id.edit_text);
        sendBtn = (ImageView) findViewById(R.id.btn_send);
        closeView = (ImageView) findViewById(R.id.close_image);
        sendContainer = findViewById(R.id.container_send);
        switchMsgType = findViewById(R.id.switch_msg_type);
        //danmuImage = (ImageView) findViewById(R.id.danmu_image);

    }

    public EditText getInputView(){
        return editview;
    }

    public void init(String chatroomId){
        addSoftKeyboardListener();
        conversation = EMClient.getInstance().chatManager().getConversation(chatroomId, EMConversation.EMConversationType.ChatRoom, true);
        adapter = new ListAdapter(getContext(), conversation);
        listview.setLayoutManager(new LinearLayoutManager(getContext()));
        listview.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setSize(0, (int) EaseCommonUtils.dip2px(getContext(), 4));
        itemDecoration.setDrawable(drawable);
        listview.addItemDecoration(itemDecoration);
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageViewListener != null){
                    if(TextUtils.isEmpty(editview.getText())){
                        Toast.makeText(getContext(), "文字内容不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageViewListener.onMessageSend(editview.getText().toString(), isBarrageMsg);
                    editview.setText("");
                }
            }
        });
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                setShowInputView(false);
                if(messageViewListener != null){
                    messageViewListener.onHiderBottomBar();
                }
            }
        });
        switchMsgType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isBarrageMsg = isChecked;
            }
        });

        //danmuImage.setOnClickListener(new OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        if(danmuImage.isSelected()){
        //            danmuImage.setSelected(false);
        //            isBarrageShow = false;
        //        }else {
        //            danmuImage.setSelected(true);
        //            isBarrageShow = true;
        //        }
        //    }
        //});

    }

    private void addSoftKeyboardListener() {
        if(getContext() instanceof Activity) {
            SoftKeyboardChangeHelper.setOnSoftKeyboardChangeListener((Activity) getContext(), new SoftKeyboardChangeHelper.OnSoftKeyboardChangeListener() {
                @Override
                public void keyboardShow(int height) {
                    Log.e("TAG", "软键盘展示");
                }

                @Override
                public void keyboardHide(int height) {
                    Log.e("TAG", "软键盘隐藏");
                    setShowInputView(false);
                    if(messageViewListener != null){
                        messageViewListener.onHiderBottomBar();
                    }
                }
            });
        }

    }

    private void hideKeyboard() {
        Utils.hideKeyboard(this);
    }

    public void setShowInputView(boolean showInputView){
        if(showInputView){
            sendContainer.setVisibility(View.VISIBLE);
        }else{
            sendContainer.setVisibility(View.INVISIBLE);
        }
    }

    private MessageViewListener messageViewListener;
    public interface MessageViewListener{
        void onMessageSend(String content, boolean isBarrageMsg);
        void onItemClickListener(EMMessage message);
        void onHiderBottomBar();
    }

    public void setMessageViewListener(MessageViewListener messageViewListener){
        this.messageViewListener = messageViewListener;
    }

    public void refresh(){
        if(adapter != null){
            adapter.refresh();
        }
    }

    public void refreshSelectLast(){
        if(adapter != null){
            adapter.refresh();
            listview.smoothScrollToPosition(adapter.getItemCount()-1);
        }
    }


    private class ListAdapter extends RecyclerView.Adapter<MyViewHolder>{

        private final Context context;
        EMMessage[] messages;


        public ListAdapter(Context context, EMConversation conversation){
            this.context = context;
            messages = conversation.getAllMessages().toArray(new EMMessage[0]);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_room_msgs_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final EMMessage message = messages[position];
            String from = message.getFrom();
            String nickName = DemoHelper.getNickName(from);
            boolean isSelf = EMClient.getInstance().getCurrentUser().equals(from);
            if(message.getBody() instanceof EMTextMessageBody) {
                boolean memberAdd = false;
                Map<String, Object> ext = message.ext();
                if(ext.containsKey(DemoConstants.MSG_KEY_MEMBER_ADD)) {
                    memberAdd = (boolean) ext.get(DemoConstants.MSG_KEY_MEMBER_ADD);
                }
                String content = ((EMTextMessageBody) message.getBody()).getMessage();
                if(memberAdd) {
                    showMemberAddMsg(holder.name, nickName, content);
                }else {
                    showText(holder.name, nickName, isSelf, content);
                }
            }else if(message.getBody() instanceof EMCustomMessageBody) {
                EMCustomMessageBody body = (EMCustomMessageBody) message.getBody();
                String event = body.event();
                if(!TextUtils.isEmpty(event)) {
                    switch (event) {
                        case MsgConstant.CUSTOM_GIFT :
                            showGiftMessage(holder.name, nickName, isSelf, body);
                            break;
                        case MsgConstant.CUSTOM_LIKE :
                            showLikeMessage(holder.name, nickName, isSelf, body);
                            break;
                        case MsgConstant.CUSTOM_BARRAGE :
                            showBarrageMessage(holder.name, nickName, isSelf, body);
                            break;
                    }
                }
            }
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) {
                    if (messageViewListener != null) {
                        messageViewListener.onItemClickListener(message);
                    }
                }
            });
        }

        private void showMemberAddMsg(TextView name, String nickName, String content) {
            StringBuilder builder = new StringBuilder();
            builder.append(nickName).append(" ").append(context.getString(R.string.em_live_msg_member_add));
            SpannableString span = new SpannableString(builder.toString());
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.white)), 0, nickName.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.gray)),
                    nickName.length() + 1, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            name.setText(span);
        }

        private void showText(TextView name, String nickName, boolean isSelf, String content) {
            StringBuilder builder = new StringBuilder();
            builder.append(nickName).append(":").append(content);
            SpannableString span = new SpannableString(builder.toString());
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.white)), 0, nickName.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(isSelf ? ContextCompat.getColor(getContext(), R.color.color_room_my_msg) : Color.parseColor("#FFC700")),
                    nickName.length() + 1, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            name.setText(span);
        }

        private void showGiftMessage(TextView name, String nickName, boolean isSelf, EMCustomMessageBody body) {
            GiftBean bean = DemoHelper.getGiftById(body.getParams().get(MsgConstant.CUSTOM_GIFT_KEY_ID));
            String num = body.getParams().get(MsgConstant.CUSTOM_LIKE_KEY_NUM);
            String content = context.getString(R.string.em_live_msg_gift, nickName, bean.getName(), num);
            SpannableString span = new SpannableString(content);
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.white)), 0, nickName.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.gray)),
                    nickName.length() + 1, nickName.length() + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#ff68ff95")),
                    nickName.length() + 4, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            name.setText(span);
        }

        private void showLikeMessage(TextView name, String nickName, boolean isSelf, EMCustomMessageBody body) {
            String content = context.getString(R.string.em_live_msg_like, nickName, body.getParams().get(MsgConstant.CUSTOM_LIKE_KEY_NUM));
            SpannableString span = new SpannableString(content);
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.white)), 0, nickName.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.gray)),
                    nickName.length() + 1, nickName.length() + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#ff68ff95")),
                    nickName.length() + 3, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            name.setText(span);
        }

        private void showBarrageMessage(TextView name, String nickName, boolean isSelf, EMCustomMessageBody body) {
            showText(name, nickName, isSelf, body.getParams().get(MsgConstant.CUSTOM_BARRAGE_KEY_TXT));
        }

        @Override
        public int getItemCount() {
            return messages.length;
        }

        public void refresh(){
            messages = conversation.getAllMessages().toArray(new EMMessage[0]);
            ((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

    }


    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView content;
        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }

}
