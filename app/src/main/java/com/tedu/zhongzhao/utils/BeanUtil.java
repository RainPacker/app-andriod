package com.tedu.zhongzhao.utils;

import com.tedu.base.util.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * java实体类处理工具
 * Created by huangyx on 2018/3/8.
 */
public class BeanUtil {

    private static final String TAG = BeanUtil.class.getSimpleName();

    /**
     * 将实体类转换成map
     *
     * @param obj 类的实例，类的属性只能是基本类型
     * @return Map<String, Object>
     */
    public static Map<String, Object> bean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = null;
        String clzName = obj.getClass().getSimpleName();
        LogUtil.i(TAG, "将类 " + clzName + " 转换成map");
        fields = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String proName = field.getName();
                Object proValue = field.get(obj);
                map.put(proName, proValue);
                LogUtil.d(TAG, "key：" + proName + ", value:" + proValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        LogUtil.i(TAG, "类" + clzName + "转map结束");
        return map;
    }

    /**
     * 将map转换成bean
     *
     * @param map   要转换的map
     * @param clazz 要转换成的bean, 该bean只支持基本属性
     * @param <T>   泛类
     * @return T
     */
    public static <T> T map2Bean(Map<String, Object> map, Class<T> clazz) {
        T obj = null;
        try {
            obj = clazz.newInstance();
            String clzName = clazz.getSimpleName();
            LogUtil.i(TAG, "将map转换成类 " + clzName);
            if (map != null && !map.isEmpty()) {
                for (String k : map.keySet()) {
                    Object v = "";
                    if (!k.isEmpty()) {
                        v = map.get(k);
                    }
                    Field[] fields = null;
                    fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        int mod = field.getModifiers();
                        if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                            continue;
                        }
                        if (field.getName().equals(k)) {
                            field.setAccessible(true);
                            field.set(obj, v);
                            LogUtil.d(TAG, "key：" + k + ", value:" + v);
                        }

                    }
                    LogUtil.i(TAG, "将map转换成类" + clzName + "结束");
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
