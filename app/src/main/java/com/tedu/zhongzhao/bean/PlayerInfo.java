package com.tedu.zhongzhao.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 视频播放信息
 * Created by huangyx on 2018/7/14.
 */
public class PlayerInfo implements java.io.Serializable {

    /**
     * 0 代表普通的播放
     * 1 代表使用STS播放
     */
    @SerializedName("playtype")
    private int playType;
    /**
     * 视频播放地址（playType=0时使用）
     */
    private String url;
    /**
     * playType=1时使用
     */
    private String vid;
    /**
     * playType=1时使用
     */
    private String accessKeyId;
    /**
     * playType=1时使用
     */
    private String accessKeySecret;
    /**
     * playType=1时使用
     */
    private String securityToken;

    public int getPlayType() {
        return playType;
    }

    public void setPlayType(int playType) {
        this.playType = playType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
    /**

     * playType=1时使用
     */


}
