package com.tedu.zhongzhao.utils;

import java.text.DecimalFormat;

/**
 * String util
 * Created by huangyx on 2018/6/30.
 */
public class StringUtils {

    private static final DecimalFormat TRIP_FORMAT = new DecimalFormat("#.#公里");

    /**
     * 格式化里程公里数
     *
     * @param mileage 里程（米）
     * @return #.# KM
     */
    public static String formatMileage(double mileage) {
        return TRIP_FORMAT.format(mileage / 1000);
    }

    /**
     * 将时间秒转成x小时y分钟
     *
     * @param time
     * @return
     */
    public static String formatTime(int time) {
        time = time / 60;
        if (time <= 60) {
            return time + "分钟";
        }
        int h = time / 60;
        int m = time % 60;
        if (m == 0) {
            return h + "小时";
        } else {
            return h + "小时" + m + "分钟";
        }
    }
}
