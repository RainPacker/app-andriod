package com.tedu.zhongzhao.web;

/**
 * 网页-原生协议处理回调
 * Created by huangyx on 2018/3/8.
 */
public abstract class ActionCallback {

    protected String callbackId;

    public ActionCallback(String callbackId) {
        this.callbackId = callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getCallbackId() {
        return callbackId;
    }

    abstract public void onBack();

    abstract public void onBegin(String result);

    abstract public void onProgress(String result);

    abstract public void onSuccess(String result);

    abstract public void onFail(String result, String error);

    abstract public void onCancel();

    abstract public void onNotify(String content);
}
