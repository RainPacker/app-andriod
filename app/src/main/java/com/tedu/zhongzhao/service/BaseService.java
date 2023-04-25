package com.tedu.zhongzhao.service;

import com.tedu.zhongzhao.bean.ErrorInfo;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.zhongzhao.web.ActionCallback;

import java.util.Map;

/**
 * Service基本类
 * Created by huangyx on 2018/3/14.
 */
public class BaseService {

    public void doWork(ActionCallback callback, Map<String, String> params) {

    }

    /**
     * 通知执行成功
     *
     * @param result 成功结果
     */
    protected void sendSuccess(ActionCallback callback, String result) {
        if (callback != null) {
            callback.onSuccess(result);
        }
    }

    /**
     * 通知执行失败
     *
     * @param result 失败结果
     * @param error  错误信息
     */
    protected void sendFail(ActionCallback callback, String result, String error) {
        if (callback != null) {
            ErrorInfo err = ErrorFactory.getInstance().getError(result);
            String resultStr = "";
            if (err != null) {
                resultStr = JsonUtil.toJson(err);
            }
            callback.onFail(resultStr, error);
        }
    }

    /**
     * 通知开始执行
     */
    protected void sendBegin(ActionCallback callback, String result) {
        if (callback != null) {
            callback.onBegin(result);
        }
    }

    /**
     * 通知执行进度
     *
     * @param result 当前进度
     */
    protected void sendProgress(ActionCallback callback, String result) {
        if (callback != null) {
            callback.onProgress(result);
        }
    }

    /**
     * 通知开始执行
     */
    protected void sendCancel(ActionCallback callback) {
        if (callback != null) {
            callback.onCancel();
        }
    }

    protected void sendNotify(ActionCallback callback, String content) {
        if (callback != null) {
            callback.onNotify(content);
        }
    }

}
