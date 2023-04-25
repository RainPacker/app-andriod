package com.tedu.zhongzhao.utils;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * 数据传递临时存储中心
 * Created by change on 2016/5/31.
 */
public class TempIntentData {

    private static HashMap<String, Object> sDatas;

    static {
        sDatas = new HashMap<String, Object>(8);
    }

    public static void putData(String key, Object obj) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        sDatas.put(key, obj);
    }

    public static Object getData(String key) {
        if(TextUtils.isEmpty(key)){
            return null;
        }
        return sDatas.remove(key);
    }

    public static boolean contains(String key){
        if(TextUtils.isEmpty(key)){
            return false;
        }
        return sDatas.containsKey(key);
    }

    public static void clear(){
        sDatas.clear();
    }
}
