package com.easemob.livedemo.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.livedemo.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

/**
 * Created by wei on 2016/6/3.
 */
public class RoomMessagesView extends RelativeLayout{
    private EMConversation conversation;
    ListAdapter adapter;

    RecyclerView listview;
    EditText editview;
    Button sendBtn;
    View sendContainer;
    ImageView closeView;
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
        sendBtn = (Button) findViewById(R.id.btn_send);
        closeView = (ImageView) findViewById(R.id.close_image);
        sendContainer = findViewById(R.id.container_send);
        //danmuImage = (ImageView) findViewById(R.id.danmu_image);

    }

    public EditText getInputView(){
        return editview;
    }

    public void init(String chatroomId){
        conversation = EMClient.getInstance().chatManager().getConversation(chatroomId, EMConversation.EMConversationType.ChatRoom, true);
        adapter = new ListAdapter(getContext(), conversation);
        listview.setLayoutManager(new LinearLayoutManager(getContext()));
        listview.setAdapter(adapter);
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageViewListener != null){
                    if(TextUtils.isEmpty(editview.getText())){
                        Toast.makeText(getContext(), "文字内容不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageViewListener.onMessageSend(editview.getText().toString());
                    editview.setText("");
                }
            }
        });
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setShowInputView(false);
                if(messageViewListener != null){
                    messageViewListener.onHiderBottomBar();
                }
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

    public void setShowInputView(boolean showInputView){
        if(showInputView){
            sendContainer.setVisibility(View.VISIBLE);
        }else{
            sendContainer.setVisibility(View.INVISIBLE);
        }
    }

    private MessageViewListener messageViewListener;
    public interface MessageViewListener{
        void onMessageSend(String content);
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
            if(message.getBody() instanceof EMTextMessageBody) {
                holder.name.setText(message.getFrom());
                holder.content.setText(((EMTextMessageBody) message.getBody()).getMessage());
                if (EMClient.getInstance().getCurrentUser().equals(message.getFrom())) {
                    holder.content.setTextColor(getResources().getColor(R.color.color_room_my_msg));
                } else {
                    holder.content.setTextColor(getResources().getColor(R.color.common_white));
                }
                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override public void onClick(View v) {
                        if (messageViewListener != null) {
                            messageViewListener.onItemClickListener(message);
                        }
                    }
                });
            }
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
