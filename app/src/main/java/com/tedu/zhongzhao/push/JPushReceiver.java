package com.tedu.zhongzhao.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送Receiver
 * Created by huangyx on 2018/4/9.
 */
public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
            // 用户注册上
            Bundle bundle = intent.getExtras();
            String id = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            JPushUtil.postJPushId(id);
            JPushUtil.setTags();
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            // 接收到自定义消息
            Bundle bundle = intent.getExtras();
            String pushId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String url = null;
            if (!TextUtils.isEmpty(extras)) {
                try {
                    JSONObject json = new JSONObject(extras);
                    if (json.has("action_data") && !json.isNull("action_data")) {
                        url = json.optString("action_data");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            PushInfo push = new PushInfo();
            push.setMessage(message);
            push.setNotify(false);
            push.setPushId(pushId);
            push.setTargetUrl(url);
            push.setTitle(title);
            JPushUtil.doProcessPush(context, push);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
            // 接收到通知
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
            // 打开默认通知
            Bundle bundle = intent.getExtras();
            String pushId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String url = null;
            if (!TextUtils.isEmpty(extras)) {
                try {
                    JSONObject json = new JSONObject(extras);
                    if (json.has("action_data") && !json.isNull("action_data")) {
                        url = json.optString("action_data");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            PushInfo push = new PushInfo();
            push.setNotify(true);
            push.setPushId(pushId);
            push.setTargetUrl(url);
            JPushUtil.openNotification(context, push);
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(action)) {
            // 极光连接状态变化
            Bundle bundle = intent.getExtras();
            boolean connected = bundle.getBoolean(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            if (connected) {
                JPushUtil.setTags();
            }
        }
    }
}
