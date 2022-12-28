package com.easemob.livedemo.common.db.entity;

import androidx.room.Entity;
import androidx.room.Index;

import java.io.Serializable;

@Entity(tableName = "em_receive_gift", primaryKeys = {"timestamp"},
        indices = {@Index(value = {"timestamp"}, unique = true)})
public class ReceiveGiftEntity implements Serializable {
    private String from;
    private String to;
    private String gift_id;
    private int gift_num;
    private long timestamp;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
    }

    public int getGift_num() {
        return gift_num;
    }

    public void setGift_num(int gift_num) {
        this.gift_num = gift_num;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
