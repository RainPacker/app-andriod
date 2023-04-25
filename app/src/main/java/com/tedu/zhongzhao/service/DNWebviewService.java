package com.tedu.zhongzhao.service;

import android.content.Context;
import android.text.TextUtils;

import com.tedu.zhongzhao.bean.KeyValueInfo;
import com.tedu.zhongzhao.db.DBHelper;
import com.tedu.zhongzhao.web.ActionCallback;

import java.util.Map;

public class DNWebviewService extends BaseService {

    private static DNWebviewService sInstance;

    synchronized public static DNWebviewService getInstance() {
        if (sInstance == null) {
            sInstance = new DNWebviewService();
        }
        return sInstance;
    }

    private DNWebviewService() {

    }


    /**
     * 保存数据
     *
     * @param callback ActionCallback
     * @param context  Context
     * @param params   参数
     */
    public void saveDataToLocalStorageWithKey_andValue_andWebView_(ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("key")) {
                String key = params.get("key");
                if (!TextUtils.isEmpty(key)) {
                    String value = null;
                    if (params.containsKey("value")) {
                        value = params.get("value");
                    }
                    KeyValueInfo kv = DBHelper.getKeyValue(key);
                    if (kv != null) {
                        kv.setValue(value);
                        DBHelper.update(kv);
                    } else {
                        kv = new KeyValueInfo(null, key, value);
                        DBHelper.save(new KeyValueInfo(null, key, value));
                    }
                    sendSuccess(callback, "true");
                    return;
                }
            }
        }
        sendSuccess(callback, "false");
    }


    /**
     * 删除数据
     *
     * @param context Context
     * @param params  参数
     */
    public void clearFromLocalStorageForKey_andWebView_(ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("key")) {
                String key = params.get("key");
                if (!TextUtils.isEmpty(key)) {
                    KeyValueInfo kv = DBHelper.getKeyValue(key);
                    if (kv != null) {
                        DBHelper.delete(kv);
                    }
                    sendSuccess(callback, "true");
                    return;
                }
            }
        }
        sendSuccess(callback, "false");
    }

    /**
     * 通过key查询数据
     *
     * @param context Context
     * @param params  参数
     */
    public void fetchFromLocalStorageForKey_andWebView_(ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("key")) {
                String key = params.get("key");
                if (!TextUtils.isEmpty(key)) {
                    KeyValueInfo kv = DBHelper.getKeyValue(key);
                    String result = null;
                    if (kv != null) {
                        result = kv.getValue();
                    }
                    sendSuccess(callback, result);
                    return;
                }
            }
        }
        sendSuccess(callback, "false");
    }
}
