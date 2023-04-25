package com.tedu.zhongzhao;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import com.aliyun.common.httpfinal.QupaiHttpFinal;
import com.mob.MobSDK;
import com.tedu.base.BaseApplication;
import com.tedu.zhongzhao.service.DNEventService;
import com.tedu.zhongzhao.task.TaskExecutor;
import com.tedu.zhongzhao.tracking.TrackingManager;
import com.tedu.zhongzhao.utils.CrashHandler;
import com.tedu.zhongzhao.utils.FileUtils;
import com.tedu.zhongzhao.utils.GalleryFinalImageLoader;
import com.tedu.zhongzhao.utils.SettingUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;

/**
 * Application
 * Created by huangyx on 2018/3/5.
 */
public class WorkApplication extends BaseApplication {


    public static WorkApplication getApplication() {
        return (WorkApplication) BaseApplication.getApplication();
    }

    private static List<Activity> sActivities = new ArrayList<Activity>();

    /**
     * 是否debug模式
     */
    private static boolean isDebug = true;
    /**
     * 根目录
     */
    private static String rootDir;
    /**
     * crash 文件目录
     */
    private static String crashDir;
    /**
     * 下载目录
     */
    private static String downloadDir;
    /**
     * 图片目录
     */
    private static String imageDir;
    /**
     * web view缓存目录
     */
    private static String webCacheDir;
    /**
     * 缓存目录
     */
    private static String cacheDir;

    private String mVersion;
    private String mChannel;

    private String wxAppId;
    /**
     * 是否要从网络加载Start page的标识
     */
    private boolean showLoadStartPage;

    @Override
    public void onCreate() {
        super.onCreate();
        TrackingManager.getInstance().init(this);
        mChannel = SettingUtil.getString("channel", null);
        try {
            mVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (TextUtils.isEmpty(mChannel)) {
                ApplicationInfo app = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                mChannel = String.valueOf(app.metaData.get("CHANNEL_ID"));
                if (!TextUtils.isEmpty(mChannel)) {
                    SettingUtil.putString("channel", mChannel);
                }
                wxAppId = app.metaData.get("WX_APP_ID").toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        initDir();
        if (!isDebug) {
            CrashHandler.getInstance().init(this);
        }

        // share sdk初始化
        MobSDK.init(this);

        /**-----配置图片选择器开始---**/
        //设置主题
        ThemeConfig themeConfig = new ThemeConfig.Builder()
                .setIconBack(R.mipmap.icon_back)
                .setTitleBarBgColor(getResources().getColor(R.color.title_bg))
//                .setIconPreview(R.mipmap.icon_carma)
                .build();

        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableEdit(false)    // 禁止编辑
                .setEnableCrop(true)       // 禁止裁剪
                .setForceCrop(true)
                .setCropSquare(true)
                .setEnableRotate(true)//旋转
                .setEnablePreview(true)
                .setEnableCamera(false)
                .setEnablePreview(true)
                .build();
        // 核心配置
        CoreConfig coreConfig = new CoreConfig.Builder(getApplicationContext(), new GalleryFinalImageLoader(), themeConfig)
                .setFunctionConfig(functionConfig)
                .setTakePhotoFolder(new File(getImageDir()))
                .setEditPhotoCacheFolder(new File(getImageDir()))
                .build();
        GalleryFinal.init(coreConfig);

        System.loadLibrary("aliresample");
        System.loadLibrary("live-openh264");
        System.loadLibrary("QuCore-ThirdParty");
        System.loadLibrary("QuCore");
        QupaiHttpFinal.getInstance().initOkHttpFinal();
        com.aliyun.vod.common.httpfinal.QupaiHttpFinal.getInstance().initOkHttpFinal();
    }

    /**
     * 初始化缓存目录
     */
    private void initDir() {
        rootDir = Environment.getExternalStorageDirectory() + "/tedu/framework/";
        try {
            FileUtils.createFolder(rootDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        crashDir = rootDir + "/crash/";
        try {
            FileUtils.createFolder(crashDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        downloadDir = rootDir + "/download/";
        try {
            FileUtils.createFolder(downloadDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageDir = rootDir + "/image/";
        try {
            FileUtils.createFolder(imageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        webCacheDir = rootDir + "/webview/";
        try {
            FileUtils.createFolder(webCacheDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cacheDir = rootDir + "/cache/";
        try {
            FileUtils.createFolder(cacheDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取版本号
     *
     * @return 如：1.0.9
     */
    public String getVersion() {
        return mVersion;
    }

    /**
     * 渠道号
     *
     * @return String
     */
    public String getChannelId() {
        return mChannel;
    }

    /**
     * 获取微信app id
     *
     * @return
     */
    public String getWxAppId() {
        return wxAppId;
    }

    /**
     * 应用名称
     *
     * @return
     */
    public String getApp() {
        return getString(R.string.app_name);
    }

    /**
     * 退出应用
     */
    public void exit() {
        TaskExecutor.getInstance().release();
        while (!sActivities.isEmpty()) {
            Activity act = sActivities.remove(0);
            act.finish();
        }
        DNEventService.getInstance().release();
    }


    public static String getCrashDir() {
        return crashDir;
    }

    public static String getDownloadDir() {
        return downloadDir;
    }

    public static String getImageDir() {
        return imageDir;
    }

    public static String getWebCacheDir() {
        return webCacheDir;
    }

    public static String getMyCacheDir() {
        return cacheDir;
    }

    /**
     * 是否debug模式
     *
     * @return true/false
     */
    @Override
    public boolean isDebug() {
        return isDebug;
    }

    public static void addActivity(Activity activity) {
        if (!sActivities.contains(activity)) {
            sActivities.add(activity);
        }
    }

    public static void removeActivity(Activity activity) {
        if (sActivities.contains(activity)) {
            sActivities.remove(activity);
        }
        if (sActivities.isEmpty()) {
            DNEventService.getInstance().release();
        }
    }

    public boolean isShowLoadStartPage() {
        return showLoadStartPage;
    }

    public void setShowLoadStartPage(boolean showLoadStartPage) {
        this.showLoadStartPage = showLoadStartPage;
    }
}
