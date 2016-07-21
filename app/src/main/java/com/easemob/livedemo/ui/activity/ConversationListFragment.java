package com.easemob.livedemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.easemob.livedemo.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.EaseConversationList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by wei on 2016/7/21.
 */
public class ConversationListFragment extends Fragment{

  @BindView(R.id.conversation_list) EaseConversationList conversationListView;

  String anchorId;
  protected List<EMConversation> conversationList = new ArrayList<>();
  private Unbinder unbinder;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    anchorId = getArguments().getString("anchorId");

    conversationList.addAll(loadConversationList());
    conversationListView.init(conversationList);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  /**
   * load conversation list
   *
   * @return
  +    */
  protected List<EMConversation> loadConversationList(){
    // get all conversations
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
      for (EMConversation conversation : conversations.values()) {
        if (conversation.getAllMessages().size() != 0) {
          sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
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
    EMMessage message = EMMessage.createTxtSendMessage("Hi,我是主播，快来与我聊天吧", anchorId);
    EMClient.getInstance().chatManager().saveMessage(message);
    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(anchorId);
    if(conversation != null) {
      conversation.insertMessage(message);
      conversations.put(anchorId, conversation);
    }
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
          if(con1.second.getUserName().equals(anchorId)){
            return -1;
          }else if(con1.second.getUserName().equals(anchorId)){
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
}
