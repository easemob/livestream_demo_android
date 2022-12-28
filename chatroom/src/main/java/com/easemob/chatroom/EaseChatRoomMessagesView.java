package com.easemob.chatroom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easemob.chatroom.model.EaseLiveMessageStyleHelper;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatroom.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class EaseChatRoomMessagesView extends RelativeLayout implements EMMessageListener {
    private Context mContext;

    private RecyclerView mMessageListView;
    private MessageListAdapter mAdapter;
    private EditText mMessageInputEt;
    private ConstraintLayout mMessageInputLayout;
    private TextView mMessageInputTip;
    private RelativeLayout mViewLayout;
    private TextView mUnreadMessageView;

    private MessageViewListener mMessageViewListener;
    private boolean mMessageStopRefresh;
    private List<EMMessage> mEMMessageList;
    private EaseLiveMessageStyleHelper mMessageStyleHelper;

    private String mChatroomId;
    private EMChatRoom mChatRoom;
    private EMConversation mConversation;
    private int mTxtNicknameHeight = 0;
    private boolean mShowKeyboard;


    public EaseChatRoomMessagesView(Context context) {
        this(context, null, 0);
    }

    public EaseChatRoomMessagesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatRoomMessagesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        mMessageStyleHelper = EaseLiveMessageStyleHelper.getInstance();

        LayoutInflater.from(context).inflate(R.layout.ease_live_chat_room_messages, this);
        mMessageListView = findViewById(R.id.room_message_list);
        mMessageInputEt = findViewById(R.id.message_input_et);
        mMessageInputLayout = findViewById(R.id.message_input_layout);
        mViewLayout = findViewById(R.id.view_layout);
        mMessageInputTip = findViewById(R.id.message_input_tip);
        mUnreadMessageView = findViewById(R.id.unread_message_view);


        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatRoomMessagesView);
            mMessageStyleHelper.setInputEditMarginBottom(ta.getDimension(R.styleable.EaseChatRoomMessagesView_ease_live_input_edit_margin_bottom, -1));
            mMessageStyleHelper.setInputEditMarginEnd(ta.getDimension(R.styleable.EaseChatRoomMessagesView_ease_live_input_edit_margin_end, -1));
            mMessageStyleHelper.setMessageListMarginEnd(ta.getDimension(R.styleable.EaseChatRoomMessagesView_ease_live_message_list_margin_end, -1));

            mMessageStyleHelper.setMessageListBackground(ta.getDrawable(R.styleable.EaseChatRoomMessagesView_ease_live_message_list_background));

            int textColorRes = ta.getResourceId(R.styleable.EaseChatRoomMessagesView_ease_live_message_item_text_color, -1);
            int textColor;
            if (textColorRes != -1) {
                textColor = ContextCompat.getColor(context, textColorRes);
            } else {
                textColor = ta.getColor(R.styleable.EaseChatRoomMessagesView_ease_live_message_item_text_color, 0);
            }
            mMessageStyleHelper.setMessageItemTxtColor(textColor);

            mMessageStyleHelper.setMessageItemTxtSize((int) ta.getDimension(R.styleable.EaseChatRoomMessagesView_ease_live_message_item_text_size
                    , 0));

            mMessageStyleHelper.setMessageItemBubblesBackground(ta.getDrawable(R.styleable.EaseChatRoomMessagesView_ease_live_message_item_bubbles_background));

            textColorRes = ta.getResourceId(R.styleable.EaseChatRoomMessagesView_ease_live_message_nickname_text_color, -1);
            if (textColorRes != -1) {
                textColor = ContextCompat.getColor(context, textColorRes);
            } else {
                textColor = ta.getColor(R.styleable.EaseChatRoomMessagesView_ease_live_message_nickname_text_color, 0);
            }
            mMessageStyleHelper.setMessageNicknameColor(textColor);

            mMessageStyleHelper.setMessageNicknameSize((int) ta.getDimension(R.styleable.EaseChatRoomMessagesView_ease_live_message_nickname_text_size
                    , 0));

            mMessageStyleHelper.setMessageShowNickname(ta.getBoolean(R.styleable.EaseChatRoomMessagesView_ease_live_message_show_nickname
                    , true));

            mMessageStyleHelper.setMessageShowAvatar(ta.getBoolean(R.styleable.EaseChatRoomMessagesView_ease_live_message_show_avatar
                    , true));

            mMessageStyleHelper.setMessageAvatarShapeType(ta.getInteger(R.styleable.EaseChatRoomMessagesView_ease_live_message_avatar_shape_type, -1));

            ta.recycle();
        }

        LayoutParams listParams = (LayoutParams) mMessageListView.getLayoutParams();
        listParams.setMarginEnd((int) mMessageStyleHelper.getMessageListMarginEnd());
        mMessageListView.setLayoutParams(listParams);

        mAdapter = new MessageListAdapter();
        mAdapter.hideEmptyView(true);
        mMessageListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageListView.setAdapter(mAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setSize(0, (int) EaseCommonUtils.dip2px(getContext(), 4));
        itemDecoration.setDrawable(drawable);
        mMessageListView.addItemDecoration(itemDecoration);

        mMessageListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                enableInputViewShow(false);
                return false;
            }
        });

        mMessageListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (RecyclerView.SCROLL_STATE_IDLE != newState && !mMessageStopRefresh) {
                    mMessageStopRefresh = true;
                }

                if (RecyclerView.SCROLL_STATE_IDLE == newState && mMessageStopRefresh) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (null != layoutManager && layoutManager.findLastVisibleItemPosition() == recyclerView.getLayoutManager().getItemCount() - 1) {
                        mMessageStopRefresh = false;
                        if (View.VISIBLE == mUnreadMessageView.getVisibility()) {
                            mUnreadMessageView.setVisibility(GONE);
                        }
                    }
                }

                super.onScrollStateChanged(recyclerView, newState);

            }
        });


        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mMessageStopRefresh = true;
                if (null != mMessageViewListener) {
                    mMessageViewListener.onChatRoomMessageItemClickListener(mAdapter.getItem(position));
                }
                enableInputViewShow(false);
            }
        });

        mUnreadMessageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageStopRefresh = false;
                refresh();
            }
        });
        if (null != mMessageStyleHelper.getMessageListBackground()) {
            mMessageListView.setBackground(mMessageStyleHelper.getMessageListBackground());
        }

        LayoutParams tipParams = (LayoutParams) mMessageInputTip.getLayoutParams();
        tipParams.setMarginEnd((int) mMessageStyleHelper.getInputEditMarginEnd());
        tipParams.bottomMargin = (int) mMessageStyleHelper.getInputEditMarginBottom();
        mMessageInputTip.setLayoutParams(tipParams);
        mMessageInputTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                enableInputViewShow(true);
            }
        });

        mMessageInputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
        mMessageInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    if (!TextUtils.isEmpty(mMessageInputEt.getText().toString())) {
                        EaseLiveMessageHelper.getInstance().sendTxtMsg(mMessageInputEt.getText().toString(), new OnSendLiveMessageCallBack() {
                            @Override
                            public void onSuccess(EMMessage message) {
                                mMessageStopRefresh = false;
                                if (null != mMessageViewListener) {
                                    mMessageViewListener.onSendTextMessageSuccess(message);
                                }
                                refresh();
                            }

                            @Override
                            public void onError(int code, String error) {
                                if (null != mMessageViewListener) {
                                    mMessageViewListener.onSendTextMessageError(code, error);
                                }
                            }
                        });

                        mMessageInputEt.setText("");
                    }
                    return true;
                }
                return false;
            }
        });
        mMessageInputEt.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (VISIBLE == mMessageInputEt.getVisibility()) {
                    Rect r = new Rect();
                    mMessageInputEt.getWindowVisibleDisplayFrame(r);
                    int height = mMessageInputEt.getContext().getResources().getDisplayMetrics().heightPixels;
                    if (height - r.bottom > 100) {
                        mShowKeyboard = true;
                    } else {
                        if (mShowKeyboard) {
                            enableInputViewShow(false);
                        }
                    }
                }

            }
        });
    }

    /**
     * init with chatroom id
     *
     * @param chatroomId
     */
    public void init(String chatroomId) {
        mChatroomId = chatroomId;
        mChatRoom = EMClient.getInstance().chatroomManager().getChatRoom(mChatroomId);
        mConversation = EMClient.getInstance().chatManager().getConversation(mChatroomId, EMConversation.EMConversationType.ChatRoom, true);
        refresh();
    }

    /**
     * update chat room info
     */
    public void updateChatRoomInfo() {
        mChatRoom = EMClient.getInstance().chatroomManager().getChatRoom(mChatroomId);
        refresh();
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (View.VISIBLE == visibility) {
            mViewLayout.setVisibility(View.VISIBLE);
            refresh();
        } else {
            if (null != mMessageViewListener) {
                mMessageViewListener.onHiderBottomBar(false);
            }
            mViewLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getVisibility() {
        return mViewLayout.getVisibility();
    }

    /**
     * Get the input edit view
     *
     * @return
     */
    public EditText getInputView() {
        return mMessageInputEt;
    }


    /**
     * Get the list view of message
     *
     * @return
     */
    public RecyclerView getMessageListView() {
        return mMessageListView;
    }

    /**
     * Get the view of input tip
     *
     * @return
     */
    public TextView getInputTipView() {
        return mMessageInputTip;
    }

    public void enableInputView(boolean enable) {
        mMessageInputTip.post(new Runnable() {
            @Override
            public void run() {
                mMessageInputTip.setEnabled(enable);
                if (!enable) {
                    enableInputViewShow(false);
                }
            }
        });
    }


    public void setMessageViewListener(MessageViewListener messageViewListener) {
        this.mMessageViewListener = messageViewListener;
    }

    /**
     * Whether the message list can be refreshed to the bottom
     *
     * @param messageStopRefresh can be refresh
     */
    public void setMessageStopRefresh(boolean messageStopRefresh) {
        this.mMessageStopRefresh = messageStopRefresh;
    }

    public void refresh() {
        if (mMessageStopRefresh) {
            mUnreadMessageView.post(new Runnable() {
                @Override
                public void run() {
                    if (mConversation.getUnreadMsgCount() > 0) {
                        mUnreadMessageView.setVisibility(VISIBLE);
                        mUnreadMessageView.setText(mContext.getString(R.string.ease_live_unread_message_tip, mConversation.getUnreadMsgCount()));
                    }
                    updateData();
                }
            });
        } else {
            mUnreadMessageView.post(new Runnable() {
                @Override
                public void run() {
                    mUnreadMessageView.setVisibility(GONE);
                    if (mAdapter != null) {
                        updateData();
                        if (mAdapter.getItemCount() > 1) {
                            mMessageListView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                        }
                    }
                }
            });
        }
    }

    public void setInputEditMarginBottom(float inputEditMarginBottom) {
        mMessageStyleHelper.setInputEditMarginBottom(inputEditMarginBottom);
    }

    public void setInputEditMarginEnd(float inputEditMarginEnd) {
        mMessageStyleHelper.setInputEditMarginEnd(inputEditMarginEnd);
    }

    public void setMessageListMarginEnd(float messageListMarginEnd) {
        mMessageStyleHelper.setMessageListMarginEnd(messageListMarginEnd);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    private void enableInputViewShow(boolean state) {
        if (state) {
            mMessageInputTip.setVisibility(INVISIBLE);
            mMessageInputLayout.setVisibility(VISIBLE);
            if (null != mMessageViewListener) {
                mMessageViewListener.onHiderBottomBar(true);
            }
            mMessageInputEt.setFocusable(true);
            mMessageInputEt.setFocusableInTouchMode(true);
            mMessageInputEt.requestFocus();
            showInputMethod();
        } else {
            hideInputMethod();
            mMessageInputTip.setVisibility(VISIBLE);
            mMessageInputLayout.setVisibility(INVISIBLE);
            if (null != mMessageViewListener) {
                mMessageViewListener.onHiderBottomBar(false);
            }
            mMessageInputEt.setFocusable(false);
            mMessageInputEt.setFocusableInTouchMode(false);
            mMessageInputEt.clearFocus();
        }
    }

    private int getNavigationBarHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            return dm.heightPixels - display.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void showInputMethod() {
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void hideInputMethod() {
        mShowKeyboard = false;
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mMessageInputEt.getWindowToken(), 0);
    }

    private void updateData() {
        if (null == mAdapter || null == mConversation) {
            return;
        }
        EMMessage[] messages = mConversation.getAllMessages().toArray(new EMMessage[0]);
        if (!mMessageStopRefresh) {
            mConversation.markAllMessagesAsRead();
        }
        if (null == mEMMessageList) {
            mEMMessageList = new ArrayList<>(messages.length);
        } else {
            mEMMessageList.clear();
        }
        for (EMMessage message : messages) {
            if (EMMessage.Status.SUCCESS == message.status()) {
                if (message.getBody() instanceof EMTextMessageBody) {
                    mEMMessageList.add(message);
                }
            }
        }
        mAdapter.setData(mEMMessageList);
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            String username = null;
            if (message.getChatType() == EMMessage.ChatType.GroupChat
                    || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                username = message.getFrom();
            }
            if (username.equals(mChatroomId)) {

                refresh();
            }
        }
    }

    private class MessageListAdapter extends EaseBaseRecyclerViewAdapter<EMMessage> {

        public MessageListAdapter() {
        }

        @Override
        public MessageViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ease_live_room_msgs_item, parent, false);
            return new MessageViewHolder(view, mContext);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private class MessageViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<EMMessage> {
        private final Context context;

        private EaseImageView avatar;

        private View joinLayout;
        private TextView joinNickname;
        private TextView joinText;
        private ImageView joinIcon;

        View textMessageLayout;
        private TextView txtMessageNickname;
        private ImageView txtMessageNicknameRole;
        private TextView txtMessageContent;

        public MessageViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
        }

        @Override
        public void initView(View itemView) {
            avatar = findViewById(R.id.iv_avatar);

            joinLayout = findViewById(R.id.join_layout);
            joinNickname = findViewById(R.id.joined_nickname);
            joinText = findViewById(R.id.joined);
            joinIcon = findViewById(R.id.iv_join_welcome);

            textMessageLayout = findViewById(R.id.text_message_layout);
            txtMessageNickname = findViewById(R.id.txt_message_nickname);
            txtMessageNicknameRole = findViewById(R.id.txt_message_nickname_role);
            txtMessageContent = findViewById(R.id.txt_message_content);

            if (null != mMessageStyleHelper.getMessageItemBubblesBackground()) {
                joinLayout.setBackground(mMessageStyleHelper.getMessageItemBubblesBackground());
                textMessageLayout.setBackground(mMessageStyleHelper.getMessageItemBubblesBackground());
            }

            if (-1 != mMessageStyleHelper.getMessageAvatarShapeType()) {
                avatar.setShapeType(mMessageStyleHelper.getMessageAvatarShapeType());
            }

            if (0 != mMessageStyleHelper.getMessageItemTxtSize()) {
                txtMessageContent.setTextSize(mMessageStyleHelper.getMessageItemTxtSize());
            }
            if (0 != mMessageStyleHelper.getMessageItemTxtColor()) {
                txtMessageContent.setTextColor(mMessageStyleHelper.getMessageItemTxtColor());
            }

            if (0 != mMessageStyleHelper.getMessageNicknameSize()) {
                joinNickname.setTextSize(mMessageStyleHelper.getMessageNicknameSize());
                txtMessageNickname.setTextSize(mMessageStyleHelper.getMessageNicknameSize());
            }
            if (0 != mMessageStyleHelper.getMessageNicknameColor()) {
                joinNickname.setTextColor(mMessageStyleHelper.getMessageNicknameColor());
                txtMessageNickname.setTextColor(mMessageStyleHelper.getMessageNicknameColor());
            }
        }

        @Override
        public void setData(EMMessage message, int position) {
            avatar.setVisibility(mMessageStyleHelper.isMessageShowAvatar() ? View.VISIBLE : View.GONE);
            String from = message.getFrom();
            if (message.getBody() instanceof EMTextMessageBody) {
                boolean memberAdd = false;
                Map<String, Object> ext = message.ext();
                if (ext.containsKey(EaseLiveMessageConstant.LIVE_MESSAGE_KEY_MEMBER_JOIN)) {
                    memberAdd = (boolean) ext.get(EaseLiveMessageConstant.LIVE_MESSAGE_KEY_MEMBER_JOIN);
                }
                String content = ((EMTextMessageBody) message.getBody()).getMessage();
                if (memberAdd) {
                    showMemberAddMsg(from);
                } else {
                    showText(from, content);
                }
            } else if (message.getBody() instanceof EMCustomMessageBody) {
                //TODO handle custom message
            }
        }

        private void showMemberAddMsg(final String id) {
            joinLayout.setVisibility(VISIBLE);
            textMessageLayout.setVisibility(GONE);
            joinNickname.setVisibility(mMessageStyleHelper.isMessageShowNickname() ? View.VISIBLE : View.GONE);
            txtMessageNickname.setVisibility(View.GONE);
            EaseUserUtils.setUserNick(id, joinNickname);
            EaseUserUtils.setUserAvatar(context, id, avatar);

            int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            joinText.measure(spec, spec);
            final int joinTextWidth = joinText.getMeasuredWidth();

            spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            joinIcon.measure(spec, spec);
            final int joinIconWidth = joinIcon.getMeasuredWidth();

            joinLayout.post(new Runnable() {
                @Override
                public void run() {
                    joinNickname.setMaxWidth(mMessageListView.getWidth() - mMessageListView.getPaddingLeft() - mMessageListView.getPaddingRight() -
                            ((LayoutParams) mMessageListView.getLayoutParams()).leftMargin - ((LayoutParams) mMessageListView.getLayoutParams()).rightMargin -
                            joinTextWidth - joinText.getPaddingLeft() - joinText.getPaddingRight() -
                            ((LayoutParams) joinText.getLayoutParams()).leftMargin - ((LayoutParams) joinText.getLayoutParams()).rightMargin -
                            joinIconWidth - joinIcon.getPaddingLeft() - joinIcon.getPaddingRight() -
                            ((LayoutParams) joinIcon.getLayoutParams()).leftMargin - ((LayoutParams) joinIcon.getLayoutParams()).rightMargin -
                            avatar.getWidth() - avatar.getPaddingLeft() - avatar.getPaddingRight() -
                            ((LayoutParams) avatar.getLayoutParams()).leftMargin - ((LayoutParams) avatar.getLayoutParams()).rightMargin -
                            ((LayoutParams) joinNickname.getLayoutParams()).leftMargin - ((LayoutParams) joinNickname.getLayoutParams()).rightMargin
                    );
                }
            });
        }

        private void showText(String id, String content) {
            joinLayout.setVisibility(GONE);
            textMessageLayout.setVisibility(VISIBLE);
            joinNickname.setVisibility(View.GONE);
            txtMessageNickname.setVisibility(mMessageStyleHelper.isMessageShowNickname() ? View.VISIBLE : View.GONE);

            txtMessageContent.setText(content);
            if (null == mChatRoom) {
                txtMessageNicknameRole.setVisibility(View.GONE);
                return;
            }

            EaseUserUtils.setUserNick(id, txtMessageNickname);
            EaseUserUtils.setUserAvatar(context, id, avatar);

            if (mChatRoom.getOwner().equals(id)) {
                txtMessageNicknameRole.setVisibility(View.VISIBLE);
                txtMessageNicknameRole.setImageResource(R.drawable.live_streamer);
            } else if (mChatRoom.getAdminList().contains(id)) {
                txtMessageNicknameRole.setVisibility(View.VISIBLE);
                txtMessageNicknameRole.setImageResource(R.drawable.live_moderator);
            } else {
                txtMessageNicknameRole.setVisibility(View.GONE);
            }

            int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            txtMessageNicknameRole.measure(spec, spec);
            final int txtMessageNicknameRoleWidth = View.VISIBLE == txtMessageNicknameRole.getVisibility() ?
                    txtMessageNicknameRole.getMeasuredWidth() : 0;

            textMessageLayout.post(new Runnable() {
                @Override
                public void run() {
                    int nicknameMaxWidth = mMessageListView.getMeasuredWidth() - mMessageListView.getPaddingLeft() - mMessageListView.getPaddingRight() -
                            ((LayoutParams) mMessageListView.getLayoutParams()).leftMargin - ((LayoutParams) mMessageListView.getLayoutParams()).rightMargin -
                            avatar.getMeasuredWidth() - avatar.getPaddingLeft() - avatar.getPaddingRight() -
                            ((LayoutParams) avatar.getLayoutParams()).leftMargin - ((LayoutParams) avatar.getLayoutParams()).rightMargin -
                            ((LayoutParams) txtMessageNickname.getLayoutParams()).leftMargin - ((LayoutParams) txtMessageNickname.getLayoutParams()).rightMargin;
                    if (View.VISIBLE == txtMessageNicknameRole.getVisibility()) {
                        nicknameMaxWidth = nicknameMaxWidth -
                                txtMessageNicknameRoleWidth - txtMessageNicknameRole.getPaddingLeft() - txtMessageNicknameRole.getPaddingRight() -
                                ((LayoutParams) txtMessageNicknameRole.getLayoutParams()).leftMargin - ((LayoutParams) txtMessageNicknameRole.getLayoutParams()).rightMargin;
                    }
                    txtMessageNickname.setMaxWidth(nicknameMaxWidth);
                }
            });

            if (txtMessageNickname.getVisibility() == View.GONE && txtMessageNicknameRole.getVisibility() == View.VISIBLE) {
                if (0 != mTxtNicknameHeight) {
                    ConstraintLayout.LayoutParams contentParams = (ConstraintLayout.LayoutParams) txtMessageContent.getLayoutParams();
                    contentParams.topMargin = mTxtNicknameHeight;
                    txtMessageContent.setLayoutParams(contentParams);
                } else {
                    txtMessageNicknameRole.post(new Runnable() {
                        @Override
                        public void run() {
                            mTxtNicknameHeight = txtMessageNicknameRole.getHeight();
                            ConstraintLayout.LayoutParams contentParams = (ConstraintLayout.LayoutParams) txtMessageContent.getLayoutParams();
                            contentParams.topMargin = mTxtNicknameHeight;
                            txtMessageContent.setLayoutParams(contentParams);
                        }
                    });
                }
            }
        }
    }

    public interface MessageViewListener {
        /**
         * send text message success
         *
         * @param message message
         */
        default void onSendTextMessageSuccess(EMMessage message) {
        }

        /**
         * send text message error
         *
         * @param code
         * @param msg
         */
        void onSendTextMessageError(int code, String msg);

        /**
         * send barrageMessage message
         *
         * @param content
         */
        default void onSendBarrageMessageContent(String content) {
        }

        /**
         * @param message
         */
        void onChatRoomMessageItemClickListener(EMMessage message);

        /**
         * hide bootom bar
         *
         * @param hide
         */
        void onHiderBottomBar(boolean hide);
    }

}
