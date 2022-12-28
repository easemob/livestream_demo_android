package com.easemob.livedemo.data.model;

import android.text.TextUtils;

public class MemberBean extends BaseBean {
    private String owner;
    private String member;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public boolean isOwner() {
        return !TextUtils.isEmpty(owner);
    }

    public String getId() {
        if (isOwner()) {
            return owner;
        }
        return member;
    }

    @Override
    public String toString() {
        return "MemberBean{" +
                "owner='" + owner + '\'' +
                ", member='" + member + '\'' +
                '}';
    }
}

