package com.tedu.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.tedu.base.BaseApplication;
import com.tedu.base.bean.DeviceInfo;
import com.tedu.base.encrypt.Md5Util;

/**
 * Android 工具类
 */
public class AndroidUtils {

    private static final String TAG = AndroidUtils.class.getSimpleName();

    /**
     * 获取应用target sdk version
     *
     * @return
     */
    public static int getTargetSDKVersion() {
        int targetSdkVersion = Build.VERSION_CODES.LOLLIPOP_MR1;
        try {
            final PackageInfo info = BaseApplication.getApplication().getPackageManager().getPackageInfo(
                    BaseApplication.getApplication().getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return targetSdkVersion;
    }

    /**
     * 获取屏幕分辨率
     *
     * @return Pair<Integer   ,       Integer>
     */
    public static Pair<Integer, Integer> getScreenSize() {
        WindowManager wm = (WindowManager) BaseApplication.getApplication().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return new Pair<Integer, Integer>(dm.widthPixels, dm.heightPixels);
    }

    /**
     * 获取通知栏高度
     *
     * @return int
     */
    public static int getStatusHeight() {
        int statusHeight = 0;
        Rect localRect = new Rect();
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = BaseApplication.getApplication().getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 根据View隐藏键盘
     *
     * @param context Context
     * @param view    焦点View
     */
    public static void hideInputMethod(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 判断当前是否在线，true表示在线
     *
     * @return true/false
     */
    public static boolean isOnline() {
        boolean flag = false;
        try {
            ConnectivityManager cwjManager = (ConnectivityManager) BaseApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = cwjManager.getActiveNetworkInfo();
            if (null == networkinfo || !networkinfo.isAvailable()) {
                LogUtil.w(TAG, "RemoteService.isOnline(): isn't online");
                flag = false;
            } else {
                LogUtil.d(TAG, "RemoteService.isOnline() is online: netType="
                        + cwjManager.getActiveNetworkInfo().getType() + "  name="
                        + cwjManager.getActiveNetworkInfo().getTypeName());
                flag = true;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "RemoteService.isOnline(): error:" + e.getMessage());
        }
        return flag;
    }

    /**
     * 获取网络类型
     *
     * @return WIFI/4G/3G/other
     */
    public static String getNetworkType() {
        String strNetworkType = "other";
        NetworkInfo networkInfo = ((ConnectivityManager) BaseApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        strNetworkType = "4G";
                        break;
                    default:
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }
                        break;
                }
            }
        }
        return strNetworkType;
    }

    /**
     * dp转换成Px
     *
     * @param dp dp
     * @return px
     */
    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                BaseApplication.getApplication().getResources().getDisplayMetrics());
    }

    /**
     * 返回手机运营商名称，在调用支付前调用作判断
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getProvidersName() {
        String ProvidersName = null;
        TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        if (IMSI == null) {
            return "unknow";
        }
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            ProvidersName = "中国移动";
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = "中国联通";
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = "中国电信";
        }
        return ProvidersName;
    }

    /**
     * 获取设备唯一标识
     *
     * @return String
     */
    public static String getUniqueId() {
        String androidID = Settings.Secure.getString(BaseApplication.getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        id = Md5Util.getMD5String(id);
        return id;
    }


    /**
     * 获取信息信息
     *
     * @return DeviceInfo
     */
    public static DeviceInfo getDeviceInfo() {
        DeviceInfo dev = new DeviceInfo();
        dev.setOsPlatform("Android");
        dev.setOsVersion(Build.VERSION.RELEASE);
        dev.setModel(Build.MODEL);
        Pair<Integer, Integer> screen = getScreenSize();
        dev.setScreen(screen.first + "," + screen.second);
        dev.setNet(getNetworkType());
        dev.setOperator(getProvidersName());
        dev.setUnique(getUniqueId());
        return dev;
    }
}
