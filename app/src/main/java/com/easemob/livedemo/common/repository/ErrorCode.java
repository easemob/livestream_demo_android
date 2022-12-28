package com.easemob.livedemo.common.repository;

import com.hyphenate.EMError;

public class ErrorCode extends EMError {
    public static final int NETWORK_ERROR = -2;

    public static final int NOT_LOGIN = -8;

    public static final int PARSE_ERROR = -10;


    public static final int ERR_UNKNOWN = -20;

    public static final int ERR_IMAGE_ANDROID_MIN_VERSION = -50;


    public static final int ERR_FILE_NOT_EXIST = -55;

    public static final int EM_ADD_SELF_ERROR = -100;


    public static final int FRIEND_ERROR = -101;


    public static final int FRIEND_BLACK_ERROR = -102;


    public static final int DELETE_CONVERSATION_ERROR = -110;

    public static final int DELETE_SYS_MSG_ERROR = -115;

    /**
     * request error
     */
    public static final int REQUEST_ERROR = -9998;
    public static final int UNKNOWN_ERROR = -9999;

    public enum Error {
        UNKNOWN_ERROR(-9999, 0);


        private int code;
        private int messageId;

        private Error(int code, int messageId) {
            this.code = code;
            this.messageId = messageId;
        }

        public static Error parseMessage(int errorCode) {
            for (Error error : Error.values()) {
                if (error.code == errorCode) {
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
