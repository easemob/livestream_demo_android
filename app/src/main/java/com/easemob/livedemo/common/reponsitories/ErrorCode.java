package com.easemob.livedemo.common.reponsitories;

import com.easemob.livedemo.R;
import com.hyphenate.EMError;

/**
 * 定义一些本地的错误code
 */
public class ErrorCode extends EMError {
    /**
     * 当前网络不可用
     */
    public static final int EM_NETWORK_ERROR = -2;

    /**
     * 未登录过环信
     */
    public static final int EM_NOT_LOGIN = -8;

    /**
     * result解析错误
     */
    public static final int EM_PARSE_ERROR = -10;

    /**
     * 网络问题请稍后重试
     */
    public static final int EM_ERR_UNKNOWN = -20;

    /**
     * 安卓版本问题,只支持4.4以上
     */
    public static final int EM_ERR_IMAGE_ANDROID_MIN_VERSION = -50;

    /**
     * 文件不存在
     */
    public static final int EM_ERR_FILE_NOT_EXIST = -55;

    /**
     * 添加自己为好友
     */
    public static final int EM_ADD_SELF_ERROR = -100;

    /**
     * 已经是好友
     */
    public static final int EM_FRIEND_ERROR = -101;

    /**
     * 已经添加到黑名单中
     */
    public static final int EM_FRIEND_BLACK_ERROR = -102;

    /**
     * 删除对话失败
     */
    public static final int EM_DELETE_CONVERSATION_ERROR = -110;

    public static final int EM_DELETE_SYS_MSG_ERROR = -115;

    public enum Error {
        UNKNOWN_ERROR(-9999, 0);


        private int code;
        private int messageId;

        private Error(int code, int messageId) {
            this.code = code;
            this.messageId = messageId;
        }

        public static Error parseMessage(int errorCode) {
            for (Error error: Error.values()) {
                if(error.code == errorCode) {
                    return error;
                }
            }
            return UNKNOWN_ERROR;
        }


        public int getMessageId() {
            return messageId;
        }


    }
}
