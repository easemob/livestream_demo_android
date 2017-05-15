package com.easemob.livedemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.easemob.livedemo.R;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseConversationList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by wei on 2016/7/21.
 */
public class ConversationListFragment extends Fragment implements EMMessageListener {

  private static final String ARG_ANCHOOR = "anchorId";
  private static final String ARG_IS_NORMAL = "isNormalStyle";

  @BindView(R.id.conversation_list) EaseConversationList conversationListView;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.title) TextView titleView;

  boolean isNormalStyle;
  private String anchorId;
  private List<EMConversation> conversationList = new ArrayList<>();
  private Unbinder unbinder;

  public ConversationListFragment(){}

  /**
   * create a ConversationListFragment instance
   * @param anchorId anchori id
   * @param isNormalStyle whether the fragemnt is normal style
   * @return
   */
  public static ConversationListFragment newInstance(String anchorId, boolean isNormalStyle){
    ConversationListFragment fragment = new ConversationListFragment();
    Bundle bundle = new Bundle();
    bundle.putString(ARG_ANCHOOR,anchorId);
    bundle.putBoolean(ARG_IS_NORMAL,isNormalStyle);
    fragment.setArguments(bundle);
    return fragment;
  }


  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if(getArguments() != null) {
      anchorId = getArguments().getString(ARG_ANCHOOR);
      isNormalStyle = getArguments().getBoolean(ARG_IS_NORMAL, false);
    }

    if(isNormalStyle){
      getView().findViewById(R.id.close).setVisibility(View.INVISIBLE);
      toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
      titleView.setTextColor(getResources().getColor(R.color.common_white));
    }

    conversationList.clear();
    conversationList.addAll(loadConversationList());
    conversationListView.init(conversationList);
    conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!isNormalStyle) {
          ChatFragment chatFragment = ChatFragment.newInstance(conversationList.get(position).conversationId(), isNormalStyle);
          getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.message_container, chatFragment).addToBackStack(null).commit();
        } else {
          startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("username", conversationList.get(position).conversationId()));
        }
      }
    });
  }

  @OnClick(R.id.close) void close(){
      getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public void onResume() {
    super.onResume();
    refreshList();
    EaseUI.getInstance().pushActivity(getActivity());
    // register the event listener when enter the foreground
    EMClient.getInstance().chatManager().addMessageListener(this);
  }


  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if(!hidden)
      refreshList();
  }


  public void refreshList(){
    conversationList.clear();
    conversationList.addAll(loadConversationList());
    conversationListView.refresh();
  }


  @Override
  public void onStop() {
    super.onStop();
    // unregister this event listener when this activity enters the
    // background
    EMClient.getInstance().chatManager().removeMessageListener(this);

    // remove activity from foreground activity list
    EaseUI.getInstance().popActivity(getActivity());
  }

  /**
   * load conversation list
   *
   * @return
  +    */
  protected List<EMConversation> loadConversationList(){
    // getInstance all conversations
    Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
    if(anchorId != null && !conversations.keySet().contains(anchorId)){
      addAnchorToConversation(conversations);
    }
    List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
    /**
     * lastMsgTime will change if there is new message during sorting
     * so use synchronized to make sure timestamp of last message won't change.
     */
    synchronized (conversations) {
      for (final EMConversation conversation : conversations.values()) {
        if ((anchorId != null || conversation.getAllMessages().size() != 0) && !conversation.isGroup()) {
          if(conversation.getAllMessages().size() == 0){
            sortList.add(new Pair<Long, EMConversation>(0l, conversation));
            conversationListView.setConversationListHelper(new EaseConversationList.EaseConversationListHelper() {
              @Override public String onSetItemSecondaryText(EMMessage lastMessage) {
                if(conversation.getAllMessages().size() == 0){
                  return "Hi，我是主播，快来与我聊天吧";
                }
                return EaseCommonUtils.getMessageDigest(lastMessage, getActivity());
              }
            });
          }else{
            sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
          }
        }
      }
    }
    try {
      // Internal is TimSort algorithm, has bug
      sortConversationByLastChatTime(sortList);
    } catch (Exception e) {
      e.printStackTrace();
    }
    List<EMConversation> list = new ArrayList<EMConversation>();
    for (Pair<Long, EMConversation> sortItem : sortList) {
      list.add(sortItem.second);
    }
    return list;
  }

  private void addAnchorToConversation(Map<String, EMConversation> conversations) {
    final EMConversation conversation = EMClient.getInstance().chatManager().getConversation(anchorId,
        EMConversation.EMConversationType.Chat, true);
  }

  /**
   * sort conversations according time stamp of last message
   *
   * @param conversationList
   */
  private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
    Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
      @Override
      public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {
        if(anchorId != null){
          if(con1.second.conversationId().equals(anchorId)){
            return -1;
          }else if(con1.second.conversationId().equals(anchorId)){
            return 1;
          }else {
            return con1.first.compareTo(con2.first);
          }
        }else{
          return con1.first.compareTo(con2.first);
        }

      }

    });
  }

  @Override public void onMessageReceived(List<EMMessage> list) {
    conversationListView.refresh();
  }

  @Override public void onCmdMessageReceived(List<EMMessage> list) {

  }

  @Override public void onMessageRead(List<EMMessage> messages) {

  }

  @Override public void onMessageDelivered(List<EMMessage> messages) {

  }


  @Override public void onMessageChanged(EMMessage emMessage, Object o) {

  }
}
