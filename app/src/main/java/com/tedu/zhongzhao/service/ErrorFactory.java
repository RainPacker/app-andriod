package com.tedu.zhongzhao.service;

import android.text.TextUtils;

import com.tedu.zhongzhao.bean.ErrorInfo;
import com.tedu.zhongzhao.utils.FileUtils;
import com.tedu.zhongzhao.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * error factory
 * Created by huangyx on 2018/6/25.
 */
public class ErrorFactory {

    private static ErrorFactory sInstance;

    synchronized public static ErrorFactory getInstance() {
        if (sInstance == null) {
            sInstance = new ErrorFactory();
        }
        return sInstance;
    }

    private Map<String, ErrorInfo> mErrors;

    public ErrorFactory() {
        mErrors = new HashMap<String, ErrorInfo>();
        String text = FileUtils.readAssetFile("config/errorsconfig.txt");
        if (!TextUtils.isEmpty(text)) {
            try {
                JSONObject json = new JSONObject(text);
                JSONArray array = JsonUtil.getArray(json, "errors");
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        ErrorInfo err = JsonUtil.fromJson(array.getString(i), ErrorInfo.class);
                        if (err != null && !TextUtils.isEmpty(err.getEid())) {
                            mErrors.put(err.getEid(), err);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过错误ID，获取错误信息
     *
     * @param eId 错误ID
     * @return 错误信息
     */
    public ErrorInfo getError(String eId) {
        if (!TextUtils.isEmpty(eId) && mErrors.containsKey(eId)) {
            return mErrors.get(eId);
        }
        return null;
    }
}
