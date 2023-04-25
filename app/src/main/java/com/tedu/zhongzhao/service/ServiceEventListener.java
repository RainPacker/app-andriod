package com.tedu.zhongzhao.service;

import android.text.TextUtils;

import com.tedu.zhongzhao.web.ActionCallback;

/**
 * event service listener
 * Created by huangyx on 2018/5/8.
 */
public class ServiceEventListener {
    private String unique;
    private ActionCallback callback;

    public ServiceEventListener(String unique, ActionCallback callback) {
        this.unique = unique;
        this.callback = callback;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public ActionCallback getCallback() {
        return callback;
    }

    public void setCallback(ActionCallback callback) {
        this.callback = callback;
    }

    public void release() {
        this.callback = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServiceEventListener) {
            return TextUtils.equals(((ServiceEventListener) obj).getUnique(), unique);
        }
        return false;
    }
}
