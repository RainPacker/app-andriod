package com.tedu.base.util;

import android.text.TextUtils;
import android.util.Log;

import com.tedu.base.BaseApplication;

/**
 * 日志工具
 */
public class LogUtil {

    private LogUtil() {
    }

    public static void i(String tag, String msg) {
        if (BaseApplication.getApplication().isDebug() && !TextUtils.isEmpty(tag)) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BaseApplication.getApplication().isDebug() && !TextUtils.isEmpty(tag)) {
            Log.w(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BaseApplication.getApplication().isDebug() && !TextUtils.isEmpty(tag)) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BaseApplication.getApplication().isDebug() && !TextUtils.isEmpty(tag)) {
            Log.e(tag, msg);
        }
    }
}
