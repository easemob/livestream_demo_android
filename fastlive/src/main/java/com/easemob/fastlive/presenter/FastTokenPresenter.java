package com.easemob.fastlive.presenter;

public abstract class FastTokenPresenter extends FastBasePresenter{
    /**
     * 通过环信相关信息，换取声网的token
     * @param hxId      环信id
     * @param channel   channel
     * @param hxAppkey  环信appkey
     * @param uid       声网需要的uid，用于标识用户，建议和环信id相关，由用户生成
     * @param isRenew   是否更新Token的操作
     */
    public abstract void getFastToken(String hxId, String channel, String hxAppkey, int uid, boolean isRenew);
}
