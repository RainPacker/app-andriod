package com.tedu.zhongzhao.net;

import java.io.Serializable;

/**
 * 请求数据
 * Created by huangyx on 2018/3/7.
 */
public class RequestData<T> implements Serializable {
    /**
     * 应用名称
     */
    private String app;
    /**
     * 应用版本
     */
    private String ver;
    /**
     * 客户端Id
     */
    private String cid;
    /**
     * 用户id;
     */
    private String uid;
    /**
     * 界面Id
     */
    private String wid;
    /**
     * 父界面Id
     */
    private String pid;
    /**
     * 请求时间
     */
    private String time;
    /**
     * 验证码
     */
    private String hmac;
    /**
     * 业务数据
     */
    private T data;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
