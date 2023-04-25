package com.tedu.zhongzhao.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Service基本信息
 * Created by huangyx on 2018/3/14.
 */
public class ServiceInfo implements Serializable {
    /**
     * service编号
     */
    @SerializedName("sid")
    private String sid;
    /**
     * 类名
     */
    @SerializedName("s_cls")
    private String clazz;
    /**
     * 方法名
     */
    @SerializedName("s_mth")
    private String method;
    @Expose
    private boolean isSingle;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }
}
