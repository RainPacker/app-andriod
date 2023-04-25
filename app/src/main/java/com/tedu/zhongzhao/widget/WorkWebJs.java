package com.tedu.zhongzhao.widget;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.bean.KeyValueInfo;
import com.tedu.zhongzhao.db.DBHelper;

/**
 * H5调用原生方法
 */
public class WorkWebJs {

    private Activity mActivity;
    private Handler mHandler;
    private boolean isInterceptBack;

    public WorkWebJs(Activity activity) {
        this.mActivity = activity;
        mHandler = new Handler(Looper.getMainLooper());
        isInterceptBack = false;
    }

    @JavascriptInterface
    public void nativeBack() {
        if (mActivity != null && mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mActivity != null) {
                        mActivity.finish();
                    }
                }
            });
        }
    }

    @JavascriptInterface
    public void exitApp() {
        if (mActivity != null && mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    WorkApplication.getApplication().exit();
                }
            });
        }
    }

    @JavascriptInterface
    public void interceptBack(boolean intercept) {
        isInterceptBack = intercept;
    }

    public boolean isInterceptBack() {
        return isInterceptBack;
    }

    //设置localstorage
    @JavascriptInterface
    public void setLocalStorage(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            key = "localStorage@" + key;
            KeyValueInfo kv = DBHelper.getKeyValue(key);
            if (kv != null) {
                kv.setValue(value);
                DBHelper.update(kv);
            } else {
                kv = new KeyValueInfo();
                kv.setKey(key);
                kv.setValue(value);
                DBHelper.save(kv);
            }
        }
    }

    //获取localstorage
    @JavascriptInterface
    public String getLocalStorageByKey(String key) {
        if (!TextUtils.isEmpty(key)) {
            key = "localStorage@" + key;
            KeyValueInfo kv = DBHelper.getKeyValue(key);
            if (kv != null) {
                return kv.getValue();
            }
        }
        return "";
    }

    //通过key移除localstorage
    @JavascriptInterface
    public boolean removeLocalStorageByKey(String key) {
        if (!TextUtils.isEmpty(key)) {
            key = "localStorage@" + key;
            DBHelper.delete(key);
            return true;
        }
        return false;
    }


    //设置sessionstorage
    @JavascriptInterface
    public void setSessionStorage(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            key = "sessionStorage@" + key;
            KeyValueInfo kv = DBHelper.getKeyValue(key);
            if (kv != null) {
                kv.setValue(value);
                DBHelper.update(kv);
            } else {
                kv = new KeyValueInfo();
                kv.setKey(key);
                kv.setValue(value);
                DBHelper.save(kv);
            }
        }
    }

    //通过key获取sessionstorage
    @JavascriptInterface
    public String getSessionStorageByKey(String key) {
        if (!TextUtils.isEmpty(key)) {
            key = "sessionStorage@" + key;
            KeyValueInfo kv = DBHelper.getKeyValue(key);
            if (kv != null) {
                return kv.getValue();
            }
        }
        return "";
    }

    //通过key移除sessionstorage
    @JavascriptInterface
    public boolean removeSessionStorageByKey(String key) {
        if (!TextUtils.isEmpty(key)) {
            key = "sessionStorage@" + key;
            DBHelper.delete(key);
            return true;
        }
        return false;
    }

    public void onRelease() {
        mActivity = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
