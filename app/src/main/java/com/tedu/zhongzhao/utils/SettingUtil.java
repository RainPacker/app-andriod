package com.tedu.zhongzhao.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.tedu.zhongzhao.WorkApplication;

/**
 * SharedPreferences工具类
 */
public class SettingUtil {

    private static final String SETTING_NAME = "app_settings";

    public static String getString(String key, String defaultValue) {
        SharedPreferences sp = getSharedPreferences(WorkApplication.getApplication(), SETTING_NAME);
        return sp.getString(key, defaultValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getEditor(WorkApplication.getApplication(), SETTING_NAME);
        editor.putString(key, value);
        editor.commit();
    }


    public static int getInt(String key, int defaultValue) {
        SharedPreferences sp = getSharedPreferences(WorkApplication.getApplication(), SETTING_NAME);
        return sp.getInt(key, defaultValue);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getEditor(WorkApplication.getApplication(), SETTING_NAME);
        editor.putInt(key, value);
        editor.commit();
    }


    public static float getFloat(String key, float defaultValue) {
        SharedPreferences sp = getSharedPreferences(WorkApplication.getApplication(), SETTING_NAME);
        return sp.getFloat(key, defaultValue);
    }

    public static void putFloat(String key, float value) {
        SharedPreferences.Editor editor = getEditor(WorkApplication.getApplication(), SETTING_NAME);
        editor.putFloat(key, value);
        editor.commit();
    }

    public static long getLong(String key, long defaultValue) {
        SharedPreferences sp = getSharedPreferences(WorkApplication.getApplication(), SETTING_NAME);
        return sp.getLong(key, defaultValue);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getEditor(WorkApplication.getApplication(), SETTING_NAME);
        editor.putLong(key, value);
        editor.commit();
    }


    public static boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences sp = getSharedPreferences(WorkApplication.getApplication(), SETTING_NAME);
        return sp.getBoolean(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getEditor(WorkApplication.getApplication(), SETTING_NAME);
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void clear(Context context) {
        SharedPreferences.Editor editor = getEditor(context, SETTING_NAME);
        editor.clear();
        editor.commit();
    }


    static SharedPreferences getSharedPreferences(Context context, String sharedPreferencesName) {
        return context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
    }

    static SharedPreferences.Editor getEditor(Context context, String sharedPreferencesName) {
        SharedPreferences sp = getSharedPreferences(context, sharedPreferencesName);
        return sp.edit();
    }

    public static void saveUserInfo(String userStr) {
        putString("userInfo", userStr);
    }

    public static String getUserInfoJson() {
        return getString("userInfo", null);
    }
}
