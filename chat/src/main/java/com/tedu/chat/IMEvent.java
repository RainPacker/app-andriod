package com.tedu.chat;

/**
 * IM事件回调
 * Created by huangyx on 2018/5/8.
 */
public class IMEvent {

    public IMEventTag tag;
    public String content;

    public IMEvent(IMEventTag tag, String content) {
        this.tag = tag;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    static public enum IMEventTag {
        /**
         * 好友请求被同意
         */
        CONTACT_AGREED,
        /**
         * 好友请求被拒绝
         */
        CONTACT_REFUSED,
        /**
         * 收到好友请求
         */
        CONTACT_INVITED,
        /**
         * 接收环信文本消息
         */
        RECEIVER_TEXT_MESSAGE
    }
}
