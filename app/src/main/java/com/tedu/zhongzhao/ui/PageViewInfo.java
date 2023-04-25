package com.tedu.zhongzhao.ui;

import com.google.gson.annotations.SerializedName;

/**
 * 页面视图信息
 * Created by huangyx on 2018/3/12.
 */
public class PageViewInfo implements java.io.Serializable {
    /**
     * 页面类型：webview
     */
    @SerializedName("v_type")
    private String viewType;
    /**
     * true使用url+param访问，false使用localUrl
     */
    @SerializedName("isremote")
    private boolean isRemote;
    /**
     * 访问地址
     */
    private String url;
    /**
     * 参数
     */
    private String param;
    /**
     * 本地地址
     */
    @SerializedName("local_url")
    private String localUrl;

    @SerializedName("needloadinganimation")
    private boolean needLoadingAni;

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public void setRemote(boolean remote) {
        isRemote = remote;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public boolean isNeedLoadingAni() {
        return needLoadingAni;
    }

    public void setNeedLoadingAni(boolean needLoadingAni) {
        this.needLoadingAni = needLoadingAni;
    }
}
