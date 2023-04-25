package com.tedu.zhongzhao.map;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangyx on 2018/4/15.
 */
public class AMapUtil {

    private static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat(",###.#");

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 把集合体的LatLonPoint转化为集合体的LatLng
     */
    public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {
        ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
        for (LatLonPoint point : shapes) {
            LatLng latLngTemp = AMapUtil.convertToLatLng(point);
            lineShapes.add(latLngTemp);
        }
        return lineShapes;
    }

    /**
     * 格式化距离
     *
     * @param distance
     * @return
     */
    public static String formatDistance(float distance) {
        if (distance < 1000) {
            return DISTANCE_FORMAT.format(distance) + "米";
        }
        return DISTANCE_FORMAT.format(distance / 1000) + "千米";
    }

    public static String formatDuration(long duration) {
        if (duration < 60) {
            return 1 + "分钟";
        }
        int m = (int) (duration / 60);
        if (m < 60) {
            return m + "分钟";
        }
        int h = m / 60;
        m = m % 60;
        if (m == 0) {
            return h + "小时";
        }
        return h + "小时" + m + "分钟";
    }
}
