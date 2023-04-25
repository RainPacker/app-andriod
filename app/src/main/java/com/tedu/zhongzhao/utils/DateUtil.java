package com.tedu.zhongzhao.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DateUtil {

    private static final SimpleDateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 格式化成yyyy-MM-dd HH:mm:ss格式
     *
     * @param date 要格式化的日期
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String toDefaultDate(Date date) {
        return DEFAULT_DATE_FORMATTER.format(date);
    }

    /**
     * 将长时间格式字符串转换为long
     *
     * @param strDate 字符型时间
     * @param pattern 字符型时间的样式
     * @return long时间
     */
    public static long strToLongDate(String strDate, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        ParsePosition pos = new ParsePosition(0);

        long time = 0;
        try {
            time = formatter.parse(strDate, pos).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 将long型时间转换成字符串(指定格式)
     *
     * @param date    long型的时间
     * @param pattern 想要格式化的样式
     * @return 指定类型的时间字符串
     */
    public static String LongDateToStr(long date, String pattern) {
        String dateStr = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            dateStr = formatter.format(new Date(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static int getDay() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return day;
    }


    public static String getTimeDateString() {
        return new StringBuffer()
                .append(Calendar.getInstance().get(Calendar.YEAR))
                .append("/")
                .append(Calendar.getInstance().get(Calendar.MONTH) + 1)
                .append("/")
                .append(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .append(":")
                .append(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) / 8).toString();
    }

    /**
     * 判断两个时间是否为同一天
     *
     * @param time1 时间1
     * @param time2 时间2
     * @return true/false
     */
    public static boolean isSameDay(long time1, long time2) {
        Date date1 = new Date(time1);
        Date date2 = new Date(time2);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);
        return isSameDate;
    }

}
