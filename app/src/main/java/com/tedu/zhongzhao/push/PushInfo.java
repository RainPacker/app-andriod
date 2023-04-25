package com.tedu.zhongzhao.push;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Push内容定义
 * Created by huangyx on 2018/4/9.
 */
public class PushInfo implements Parcelable {

    /**
     * push id
     */
    private String pushId;
    /**
     * 标题
     */
    private String title;
    /**
     * push内容
     */
    private String message;
    /**
     * 目标Url
     */
    private String targetUrl;
    /**
     * 是否通知：true通知，false透传
     */
    private boolean isNotify;

    public PushInfo() {

    }

    protected PushInfo(Parcel in) {
        pushId = in.readString();
        title = in.readString();
        message = in.readString();
        targetUrl = in.readString();
        isNotify = in.readByte() != 0;
    }

    public static final Creator<PushInfo> CREATOR = new Creator<PushInfo>() {
        @Override
        public PushInfo createFromParcel(Parcel in) {
            return new PushInfo(in);
        }

        @Override
        public PushInfo[] newArray(int size) {
            return new PushInfo[size];
        }
    };

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pushId);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(targetUrl);
        dest.writeByte((byte) (isNotify ? 1 : 0));
    }
}
