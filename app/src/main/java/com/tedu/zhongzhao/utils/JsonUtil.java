package com.tedu.zhongzhao.utils;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Json工具类
 * Created by huangyx on 2018/3/6.
 */
public class JsonUtil {

    private JsonUtil() {

    }

    /**
     * json 转换成对象
     *
     * @param json  json串
     * @param clazz 类class
     * @param <T>   访问的类实例class
     * @return T
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (!TextUtils.isEmpty(json) && clazz != null) {
            if (clazz.getName().equals(String.class.getName())) {
                return (T) json;
            }
            return new Gson().fromJson(json, clazz);
        }
        return null;
    }

    /**
     * 将对象转换成json
     *
     * @param t   要转换的对象
     * @param <T> 泛类型
     * @return json串
     */
    public static <T> String toJson(T t) {
        if (t == null) {
            return "";
        }
        if (t instanceof String) {
            return t.toString();
        }
        return new Gson().toJson(t);
    }


    /**
     * json串中根据属性名获取int值
     *
     * @param json json对象
     * @param key  属性名
     * @return int
     */
    public static int getInt(JSONObject json, String key) {
        return getInt(json, key, -1);
    }


    /**
     * json串中根据属性名获取int值
     *
     * @param json json对象
     * @param key  属性名
     * @param def  默认值
     * @return int
     */
    public static int getInt(JSONObject json, String key, int def) {
        if (json != null && json.has(key) && !json.isNull(key)) {
            return json.optInt(key);
        }
        return def;
    }


    /**
     * json串中根据属性名获取Long值
     *
     * @param json json对象
     * @param key  属性名
     * @return long
     */
    public static long getLong(JSONObject json, String key) {
        return getLong(json, key, -1);
    }


    /**
     * json串中根据属性名获取Long值
     *
     * @param json json对象
     * @param key  属性名
     * @param def  默认值
     * @return Long
     */
    public static long getLong(JSONObject json, String key, long def) {
        if (json != null && json.has(key) && !json.isNull(key)) {
            return json.optLong(key);
        }
        return def;
    }


    /**
     * json串中根据属性名获取String值
     *
     * @param json json对象
     * @param key  属性名
     * @return String
     */
    public static String getString(JSONObject json, String key) {
        if (json != null && json.has(key) && !json.isNull(key)) {
            return json.optString(key);
        }
        return null;
    }


    /**
     * json串中根据属性名获取Boolean值
     *
     * @param json json对象
     * @param key  属性名
     * @return true/false
     */
    public static boolean getBoolean(JSONObject json, String key) {
        return getBoolean(json, key, false);
    }


    /**
     * json串中根据属性名获取Boolean值
     *
     * @param json json对象
     * @param key  属性名
     * @param def  默认值
     * @return true/false
     */
    public static boolean getBoolean(JSONObject json, String key, boolean def) {
        if (json != null && json.has(key) && !json.isNull(key)) {
            return json.optBoolean(key, def);
        }
        return def;
    }


    /**
     * json串中根据属性名获取JsonArray
     *
     * @param json json对象
     * @param key  属性名
     * @return JsonArray
     */
    public static JSONArray getArray(JSONObject json, String key) {
        if (json != null && json.has(key) && !json.isNull(key)) {
            return json.optJSONArray(key);
        }
        return null;
    }


    /**
     * json串中根据属性名获取JsonObject
     *
     * @param json json对象
     * @param key  属性名
     * @return JsonObject
     */
    public static JSONObject getJsonObject(JSONObject json, String key) {
        if (json != null && json.has(key) && !json.isNull(key)) {
            return json.optJSONObject(key);
        }
        return null;
    }
}
