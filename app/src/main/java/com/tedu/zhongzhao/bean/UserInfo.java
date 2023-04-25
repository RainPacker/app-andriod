package com.tedu.zhongzhao.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 用户信息
 * Created by huangyx on 2018/5/2.
 */
public class UserInfo implements java.io.Serializable {

    @SerializedName("userid")
    private String userId;
    private String account;
    private String secret;
    private String hxuserid;
    private String hxsecret;
    @SerializedName("JSSESSIONID")
    private String sessionId;
    private String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getHxuserid() {
        return hxuserid;
    }

    public void setHxuserid(String hxuserid) {
        this.hxuserid = hxuserid;
    }

    public String getHxsecret() {
        return hxsecret;
    }

    public void setHxsecret(String hxsecret) {
        this.hxsecret = hxsecret;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
