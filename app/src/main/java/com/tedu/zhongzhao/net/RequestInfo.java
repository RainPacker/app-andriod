package com.tedu.zhongzhao.net;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 网络请求基本信息
 * Created by huangyx on 2018/3/6.
 */
public class RequestInfo implements Serializable {

    /**
     * 请求编号
     */
    @SerializedName("reqid")
    private String reqId;
    /**
     * 请求链接
     */
    @SerializedName("requrl")
    private String reqUrl;
    /**
     * 请求方式：GET/POST
     */
    private String method;
    /**
     * 提交类型：json或表单
     */
    @SerializedName("reqtype")
    private String reqType;
    /**
     * 表单提交对应类型：普通/下载/上传
     */
    @SerializedName("fileaction")
    private String action;
    /**
     * 缓存
     */
    private String cache;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }
}
