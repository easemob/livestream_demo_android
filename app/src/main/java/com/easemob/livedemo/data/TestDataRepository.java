package com.easemob.livedemo.data;

import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.LiveRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by wei on 2016/5/30.
 */
public class TestDataRepository {

  static int[] covers = new int[] {
      R.drawable.test1, R.drawable.test2, R.drawable.test3, R.drawable.test4, R.drawable.test5,
      R.drawable.test6
  };

  static String[] chatRoomIds = new String[]{"218746635482562996","218747106892972464","218747152489251244","218747179836113332","218747226120257964","218747262707171768"};
  static String[] liveRoomIds = new String[]{"em_100001","em_100002","em_100003","em_100004","em_100005","em_100006"};
  public static String[] anchorIds = new String[]{"test1","test2","test3","test4","test5","test6"};

  /**
   * 生成测试数据
   */
  public static List<LiveRoom> getLiveRoomList() {
    List<LiveRoom> roomList = new ArrayList<>();
    for (int i = 1; i <= 6; i++) {
      LiveRoom liveRoom = new LiveRoom();
      liveRoom.setName("Test" + i);
      liveRoom.setAudienceNum(new Random().nextInt(2000) + 1);
      liveRoom.setId(liveRoomIds[i-1]);
      liveRoom.setChatroomId(chatRoomIds[i-1]);
      liveRoom.setCover(covers[i - 1]);
      roomList.add(liveRoom);
    }

    return roomList;
  }

  public static String getLiveRoomId(String username){
    for(int i = 0; i <6; i++){
      if(anchorIds[i].equals(username)){
        return liveRoomIds[i];
      }
    }
    return  null;
  }

  public static String getChatRoomId(String username){
    for(int i = 0; i <6; i++){
      if(anchorIds[i].equals(username)){
        return chatRoomIds[i];
      }
    }
    return  null;
  }
}
