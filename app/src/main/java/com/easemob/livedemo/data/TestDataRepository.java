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
    public static List<LiveRoom> getLiveRoomList(){
        List<LiveRoom> roomList = new ArrayList<>();
        for(int i = 1; i <= 6; i++){
            LiveRoom liveRoom = new LiveRoom();
            liveRoom.setName("Test" + i);
//            liveRoom.setId("em_" + (10000+i));
            liveRoom.setId("em_" + 10001);
            liveRoom.setAudienceNum(new Random().nextInt(200)+1);
            liveRoom.setChatroomId("203138578711052716");
            roomList.add(liveRoom);
        }
        roomList.get(0).setCover(R.drawable.test1);
        roomList.get(1).setCover(R.drawable.test2);
        roomList.get(2).setCover(R.drawable.test3);
        roomList.get(3).setCover(R.drawable.test4);
        roomList.get(4).setCover(R.drawable.test5);
        roomList.get(5).setCover(R.drawable.test6);

        return roomList;
    }
}
