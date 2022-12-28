package com.easemob.livedemo.data.model;

import com.easemob.livedemo.ui.widget.barrage.DataSource;
import com.hyphenate.chat.EMMessage;

public class MessageBean implements DataSource {
    private EMMessage message;
    private int type;

    public EMMessage getMessage() {
        return message;
    }

    public void setMessage(EMMessage message) {
        this.message = message;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "message=" + message +
                ", type=" + type +
                '}';
    }
}
