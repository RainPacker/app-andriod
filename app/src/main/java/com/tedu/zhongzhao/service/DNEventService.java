package com.tedu.zhongzhao.service;

import android.content.Context;

import com.tedu.chat.IMEvent;
import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.web.ActionCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 通知事件Service
 * Created by huangyx on 2018/5/2.
 */
public class DNEventService extends BaseService {

    private static DNEventService sInstance;

    synchronized public static DNEventService getInstance() {
        if (sInstance == null) {
            sInstance = new DNEventService();
        }
        return sInstance;
    }

    /**
     * 目前定义的事件有：
     * friendaccept：好友接受添加请求
     * frienddecline：好友拒绝请求
     * friendRequest：添加好友请求
     * receiveTextMessage 接收环信消息
     */
    final private Map<Context, List<ServiceEventListener>> mCallbacks;

    private DNEventService() {
        mCallbacks = new HashMap<Context, List<ServiceEventListener>>();
        EventBus.getDefault().register(this);
    }

    /**
     * 添加事件监听
     *
     * @param callback
     * @param context
     * @param params
     */
    public void addEventListenerForKey_andContainerObj_(ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("eventkey")) {
                String value = params.get("eventkey");
                if (value != null) {
                    synchronized (mCallbacks) {
                        ServiceEventListener listener = new ServiceEventListener(value, callback);
                        List<ServiceEventListener> cbs = null;
                        if (mCallbacks.containsKey(context)) {
                            cbs = mCallbacks.get(context);
                        }
                        if (cbs == null) {
                            cbs = new ArrayList<ServiceEventListener>();
                            mCallbacks.put(context, cbs);
                        }
                        if (!cbs.contains(listener)) {
                            cbs.add(listener);
                        } else {
                            listener.release();
                            listener = null;
                        }
                    }
                    sendSuccess(callback, "true");
                    return;
                }
            }
        }
        sendFail(callback, "e005", "");
    }

    /**
     * 移除事件监听
     *
     * @param callback
     * @param context
     * @param params
     */
    public void removeEventListenerForKey_andContainerObj_(ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("eventkey")) {
                String value = params.get("eventkey");
                if (value != null) {
                    synchronized (mCallbacks) {
                        ServiceEventListener listener = new ServiceEventListener(value, callback);
                        List<ServiceEventListener> cbs = null;
                        if (mCallbacks.containsKey(context)) {
                            cbs = mCallbacks.get(context);
                        }
                        if (cbs != null) {
                            int idx = cbs.indexOf(listener);
                            if (idx != -1) {
                                ServiceEventListener l = cbs.remove(idx);
                                l.release();
                                l = null;
                            }
                            if (cbs.isEmpty()) {
                                clear(context);
                            }
                        }
                        listener.release();
                        listener = null;
                        sendSuccess(callback, "true");
                        return;
                    }
                }
            }
        }
        sendFail(callback, "e004", "");
    }

    public void clear(Context context) {
        synchronized (mCallbacks) {
            if (mCallbacks.containsKey(context)) {
                List<ServiceEventListener> listeners = mCallbacks.get(context);
                if (listeners != null && !listeners.isEmpty()) {
                    for (ServiceEventListener l : listeners) {
                        l.release();
                    }
                    listeners.clear();
                }
                mCallbacks.remove(context);
            }
        }
    }

    public void release() {
        mCallbacks.clear();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(IMEvent event) {
        if (event != null) {
            switch (event.tag) {
                case CONTACT_AGREED:// 好友请求被同意
                    notifyEvent("friendaccept", event.getContent());
                    break;
                case CONTACT_REFUSED: // 好友拒绝请求
                    notifyEvent("frienddecline", event.getContent());
                    break;
                case CONTACT_INVITED:// 请求加为好友
                    notifyEvent("friendRequest", event.getContent());
                    break;
                case RECEIVER_TEXT_MESSAGE:
                    notifyEvent("receiveTextMessage", event.getContent());
            }
        }
    }

    private void notifyEvent(String key, String content) {
        synchronized (mCallbacks) {
            ServiceEventListener l = new ServiceEventListener(key, null);
            List<ServiceEventListener> listeners;
            Set<Context> keys = mCallbacks.keySet();
            for (Context k : keys) {
                listeners = mCallbacks.get(k);
                int idx = listeners.indexOf(l);
                if (idx != -1) {
                    ServiceEventListener sl = listeners.get(idx);
                    if (sl != null && sl.getCallback() != null) {
                        sendNotify(sl.getCallback(), content);
                    }
                }
            }
        }
    }
}
