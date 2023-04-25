package com.tedu.zhongzhao.service;

import android.content.Context;
import android.text.TextUtils;

import com.tedu.base.util.LogUtil;
import com.tedu.zhongzhao.web.ActionCallback;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 业务service工厂类
 * Created by huangyx on 2018/3/14.
 */
public class ServiceFactory {

    private static final String TAG = ServiceFactory.class.getSimpleName();

    private static ServiceFactory sInstance;

    synchronized public static ServiceFactory getInstance() {
        if (sInstance == null) {
            sInstance = new ServiceFactory();
        }
        return sInstance;
    }

    /**
     * 配置的service
     */
    private List<ServiceInfo> mConfigServices;

    private ServiceFactory() {
        mConfigServices = new ArrayList<ServiceInfo>(0);
    }

    public void setServices(List<ServiceInfo> services) {
        mConfigServices.clear();
        if (services != null && !services.isEmpty()) {
            mConfigServices.addAll(services);
        }
    }

    public void doService(Context context, String sid, ActionCallback callback, Map<String, String> params) {
        if (!TextUtils.isEmpty(sid) && mConfigServices != null && !mConfigServices.isEmpty()) {
            ServiceInfo service = null;
            for (ServiceInfo s : mConfigServices) {
                if (sid.equals(s.getSid())) {
                    service = s;
                    break;
                }
            }
            doService(context, service, callback, params);
        }
    }

    public void doService(Context context, ServiceInfo service, ActionCallback callback, Map<String, String> params) {
        if (service != null) {
            if (callback != null) {
                if (TextUtils.isEmpty(callback.getCallbackId())) {
                    callback.setCallbackId(service.getSid());
                }
            }
            String msg = null;
            try {
                Class clazz = Class.forName("com.tedu.zhongzhao.service." + service.getClazz());
                if (clazz == null) {
                    msg = "class " + service.getClazz() + " not found!";
                    LogUtil.e(TAG, msg);
                    if (callback != null) {
                        callback.onFail(msg, msg);
                    }
                    return;
                }
                Object obj = null;
                try {
                    // 单例情况
                    Method instance = clazz.getDeclaredMethod("getInstance");
                    obj = instance.invoke(clazz);
                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
                }
                if (obj == null) {
                    Constructor constructor = clazz.getConstructor();
                    if (constructor != null) {
                        obj = constructor.newInstance();
                    }
                }
                if (obj == null) {
                    msg = "class " + service.getClazz() + " not found " + service.getClazz() + "(String.class, ActionCallback.class) !";
                    LogUtil.e(TAG, msg);
                    if (callback != null) {
                        callback.onFail(msg, msg);
                    }
                    return;
                }
                String methodName = service.getMethod();
                if (TextUtils.isEmpty(methodName)) {
                    methodName = "doWork";
                }
                methodName = methodName.replace(":", "_");
                Method method = clazz.getDeclaredMethod(methodName, new Class[]{ActionCallback.class, Context.class, Map.class});
                method.invoke(obj, callback, context, params);
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            msg = "class " + service.getClazz() + " do error!";
            if (callback != null) {
                callback.onFail(msg, msg);
            }
        }
    }
}
