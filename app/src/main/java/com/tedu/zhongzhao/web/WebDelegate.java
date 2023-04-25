package com.tedu.zhongzhao.web;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.text.TextUtils;

import com.tedu.zhongzhao.activity.BaseActivity;
import com.tedu.zhongzhao.service.ServiceFactory;
import com.tedu.zhongzhao.ui.UiUtil;
import com.tedu.base.encrypt.Base64;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

/**
 * 网页-原生协议处理
 * Created by huangyx on 2018/3/7.
 */
public class WebDelegate {

    private static final String TAG = WebDelegate.class.getSimpleName();

    /**
     * 处理自定义协议
     *
     * @param uri 自定义协议url
     */
    public static void doAction(Context context, Uri uri, ActionCallback callback) {
        BaseActivity act = null;
        if (context instanceof BaseActivity) {
            act = (BaseActivity) context;
        } else {
            while (context != null && context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
                if (context instanceof BaseActivity) {
                    act = (BaseActivity) context;
                    break;
                }
            }
        }
        String type = uri.getHost();
        String method = uri.getPath();
        if (method.startsWith("/")) {
            method = method.substring(1);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        Set<String> names = uri.getQueryParameterNames();
        String value;
        if (names != null && !names.isEmpty()) {
            for (String n : names) {
                if ("callback".equalsIgnoreCase(n)) {
                    continue;
                }
                value = uri.getQueryParameter(n);
                if (!TextUtils.isEmpty(value)) {
                    try {
                        value = value.replace(" ", "+");
                        value = new String(Base64.decode(value), "UTF-8");
                        if (!TextUtils.isEmpty(value)) {
                            if (value.startsWith("\"") && value.endsWith("\"")) {
                                value = value.substring(1);
                                value = value.substring(0, value.length() - 1);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                params.put(n, value);
            }
        }
        if ("3".equals(type)) {
            // 返回
            if (callback != null) {
                callback.onBack();
            }
            return;
        } else if ("5".equals(type)) {
            // 调用本地服务
            ServiceFactory.getInstance().doService(context, method, callback, params);
            return;
        } else if ("1".equals(type) || "2".equals(type)) {
            UiUtil.doActiveFromWeb(act, method, type, params, callback.getCallbackId());
            return;
        }
    }
}
