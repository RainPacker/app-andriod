package com.tedu.zhongzhao.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.WorkApplication;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送工具类
 * Created by huangyx on 2018/4/9.
 */
public class JPushUtil {

    private static final String TAG = "JPush";


    /**
     * 初始化极光推送
     *
     * @param context Context
     * @param isDebug 是否debug模式
     */
    public static void init(Context context, boolean isDebug) {
        // 禁止收集崩溃日志
        JPushInterface.stopCrashHandler(context);
        // 初始化极光推送
        JPushInterface.setDebugMode(isDebug);
        JPushInterface.init(context);
        postJPushId(JPushInterface.getRegistrationID(context));
        setTags();
    }

    /**
     * 上报极光推送ID到服务端，方便服务端推送消息
     *
     * @param id 极光id
     */
    public static void postJPushId(String id) {
        // TODO
    }

    /**
     * 设置标签
     */
    public static void setTags() {
        Set<String> tags = new HashSet<String>();
        tags.add(WorkApplication.getApplication().getChannelId());
        tags.add(WorkApplication.getApplication().getVersion());
        JPushInterface.setTags(WorkApplication.getApplication(), 0, tags);
    }


    /**
     * 处理push
     *
     * @param context Context
     * @param push    push内容
     */
    public static void doProcessPush(Context context, PushInfo push) {
        if (push == null) {
            return;
        }
        Intent dstIntent = PushTransitActivity.newIntent(context, push);
        if (dstIntent == null) {
            return;
        }
        // 定义NotificationManager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int id = push.hashCode();
        PendingIntent contentIntent = PendingIntent.getActivity(context, id, dstIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        builder.setContentTitle(push.getTitle());
        builder.setContentText(push.getMessage());
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(contentIntent);
        builder.mNotification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        mNotificationManager.notify(id, builder.build());
    }

    /**
     * 打开通知
     *
     * @param context Context
     * @param push    通知内容
     */
    public static void openNotification(Context context, PushInfo push) {
        Intent dstIntent = PushTransitActivity.newIntent(context, push);
        if (dstIntent != null) {
            context.startActivity(dstIntent);
        }
    }

    public static void test(Context context) {
        PushInfo push = new PushInfo();
        push.setMessage("push推送测试内容");
        push.setNotify(false);
        push.setTargetUrl("http://www.baidu.com");
        push.setTitle("push推送");
        JPushUtil.doProcessPush(context, push);
    }
}
