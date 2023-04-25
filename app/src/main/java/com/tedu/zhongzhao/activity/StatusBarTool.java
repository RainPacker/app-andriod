package com.tedu.zhongzhao.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangyx on 2018/7/31.
 */
class StatusBarTool {

    private static Map<Activity, SystemBarTintManager> sTintManagers = new HashMap<Activity, SystemBarTintManager>();

    /**
     * 设置状态栏颜色
     *
     * @param activity   Activity
     * @param color      状态栏颜色
     * @param spareColor 备用底色，当状态栏设定的颜色为Color.TRANSPARENT时，需要使用该属性来设定状态栏中图标和文字颜色
     */
    public static void setTranslucentStatus(Activity activity, int color, int spareColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            } else {
                SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintColor(color);
            }
            boolean isDarkColor = false;
            if (color == Color.TRANSPARENT || Color.alpha(color) < 50) {
                isDarkColor = isDarkColor(spareColor);
            } else {
                isDarkColor = isDarkColor(color);
            }
            if (isDarkColor) {
                setStatusBarStyle(activity, false);
            } else {
                setStatusBarStyle(activity, true);
            }
        }
    }

    public static void changeStatusColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            } else {
                if (sTintManagers.containsKey(activity)) {
                    SystemBarTintManager tintManager = sTintManagers.get(activity);
                    if (tintManager != null) {
                        tintManager.setStatusBarTintColor(color);
                    }
                }
            }
        }
    }

    public static void onActivityDestroy(Activity activity) {
        if (activity != null && sTintManagers.containsKey(activity)) {
            sTintManagers.remove(activity);
        }
    }

    /**
     * 判断是不是深颜色
     *
     * @return true/false
     */
    private static boolean isDarkColor(int color) {
        int grayLevel = (int) (Color.red(color) * 0.299 + Color.green(color) * 0.587 + Color.blue(color) * 0.114);
        if (grayLevel <= 192) {
            return true;
        }
        return false;
    }

    private static void setStatusBarStyle(Activity activity, boolean dark) {
        Window window = activity.getWindow();
        // 小米MIUI
        try {
            Class clazz = window.getClass();
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {       //清除黑色字体
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        // 魅族FlymeUI
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        // android6.0+系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
