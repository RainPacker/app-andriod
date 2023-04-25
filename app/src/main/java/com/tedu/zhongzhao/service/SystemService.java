package com.tedu.zhongzhao.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.tedu.base.encrypt.AESUtil;
import com.tedu.base.encrypt.HMacMD5;
import com.tedu.base.permission.PermissionDialog;
import com.tedu.base.permission.PermissionUtil;
import com.tedu.base.util.AndroidUtils;
import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.bean.ImageSize;
import com.tedu.zhongzhao.bean.KeyValueInfo;
import com.tedu.zhongzhao.db.DBHelper;
import com.tedu.zhongzhao.event.UploadFileEvent;
import com.tedu.zhongzhao.net.NetReqUtil;
import com.tedu.zhongzhao.net.RequestConstants;
import com.tedu.zhongzhao.net.RequestData;
import com.tedu.zhongzhao.net.RequestFactory;
import com.tedu.zhongzhao.net.RequestInfo;
import com.tedu.zhongzhao.net.UploadCallback;
import com.tedu.zhongzhao.net.UploadInfo;
import com.tedu.zhongzhao.push.JPushUtil;
import com.tedu.zhongzhao.push.PushInfo;
import com.tedu.zhongzhao.ui.UiUtil;
import com.tedu.zhongzhao.utils.FileUtils;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.zhongzhao.utils.ZipUtil;
import com.tedu.zhongzhao.web.ActionCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义系统服务
 * Created by huangyx on 2018/4/17.
 */
public class SystemService extends BaseService {

    private EventBus mEventBus;

    public SystemService() {
        mEventBus = EventBus.getDefault();
    }

    /**
     * 发送广告
     *
     * @param params 参数
     */
    public void sendNotification(ActionCallback callback, Context context, Map<String, String> params) {
        PushInfo pushInfo = new PushInfo();
        pushInfo.setTitle("push");
        if (params != null) {
            pushInfo.setMessage(JsonUtil.toJson(params));
        }
        JPushUtil.doProcessPush(WorkApplication.getApplication(), pushInfo);
        sendSuccess(callback, "true");
    }

    /**
     * 请求用户信息
     *
     * @param params 参数
     */
    public void doUserRequest_andPwd_Pci_(ActionCallback callback, Context context, Map<String, String> params) {
        sendSuccess(callback, JsonUtil.toJson(params));
    }

    /**
     * 获取手机唯一标识
     *
     * @param params 参数
     */
    public void deviceGuid(ActionCallback callback, Context context, Map<String, String> params) {
        sendSuccess(callback, AndroidUtils.getUniqueId());
    }

    /**
     * WIFI/客户端流量
     *
     * @param params 参数
     */
    public void getNetworkType(ActionCallback callback, Context context, Map<String, String> params) {
        sendSuccess(callback, AndroidUtils.getNetworkType());
    }

    /**
     * 网络类型
     *
     * @param params 参数
     */
    public void getNetType(ActionCallback callback, Context context, Map<String, String> params) {
        sendSuccess(callback, AndroidUtils.getNetworkType());
    }

    /**
     * 分辨率
     *
     * @param params 参数
     */
    public void getResolution(ActionCallback callback, Context context, Map<String, String> params) {
        Pair<Integer, Integer> screenSize = AndroidUtils.getScreenSize();
        sendSuccess(callback, screenSize.first + "," + screenSize.second);
    }

    /**
     * 运营商
     *
     * @param params 参数
     */
    public void getCarrierName(ActionCallback callback, Context context, Map<String, String> params) {
        sendSuccess(callback, AndroidUtils.getProvidersName());
    }

    /**
     * 设备类型？什么鬼
     *
     * @param params 参数
     */
    public void iphoneType(ActionCallback callback, Context context, Map<String, String> params) {
        sendSuccess(callback, Build.BOARD + ", " + Build.MODEL);
    }

    /**
     * 操作系统
     *
     * @param params 参数
     */
    public void phoneSystem(ActionCallback callback, Context context, Map<String, String> params) {
        sendSuccess(callback, "Android");
    }

    /**
     * 操作系统版本
     *
     * @param params 参数
     */
    public void phoneSystemVersion(ActionCallback callback, Context context, Map<String, String> params) {
        sendSuccess(callback, Build.VERSION.RELEASE);
    }

    /**
     * 保存到数据库
     *
     * @param context Context
     * @param params  参数
     */
    public void saveValue_forKey_(ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("key")) {
                String key = params.get("key");
                if (!TextUtils.isEmpty(key)) {
                    String value = null;
                    if (params.containsKey("myvalue")) {
                        value = params.get("myvalue");
                    }
                    long id = DBHelper.save(new KeyValueInfo(null, key, value));
                    sendSuccess(callback, "true");
                    return;
                }
            }
        }
        sendSuccess(callback, "false");
    }

    /**
     * 更新数据
     *
     * @param context Context
     * @param params  参数
     */
    public void updateValue_forKey_(ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("key")) {
                String key = params.get("key");
                if (!TextUtils.isEmpty(key)) {
                    String value = null;
                    if (params.containsKey("myvalue")) {
                        value = params.get("myvalue");
                    }
                    KeyValueInfo kv = DBHelper.getKeyValue(key);
                    long id = -1;
                    if (kv != null) {
                        id = kv.getId();
                        kv.setValue(value);
                        DBHelper.update(kv);
                    } else {
                        kv = new KeyValueInfo(null, key, value);
                        id = DBHelper.save(new KeyValueInfo(null, key, value));
                    }
                    sendSuccess(callback, "true");
                    return;
                }
            }
        }
        sendSuccess(callback, "false");
    }

    /**
     * 删除数据
     *
     * @param context Context
     * @param params  参数
     */
    public void deleteValueFor_(ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("key")) {
                String key = params.get("key");
                if (!TextUtils.isEmpty(key)) {
                    KeyValueInfo kv = DBHelper.getKeyValue(key);
                    int delCount = 0;
                    if (kv != null) {
                        DBHelper.delete(kv);
                        delCount = 1;
                    }
                    sendSuccess(callback, "true");
                    return;
                }
            }
        }
        sendSuccess(callback, "false");
    }

    /**
     * 通过key查询数据
     *
     * @param context Context
     * @param params  参数
     */
    public void queryValueForKey_(ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("key")) {
                String key = params.get("key");
                if (!TextUtils.isEmpty(key)) {
                    KeyValueInfo kv = DBHelper.getKeyValue(key);
                    String result = null;
                    if (kv != null) {
                        result = JsonUtil.toJson(kv);
                    }
                    sendSuccess(callback, result);
                    return;
                }
            }
        }
        sendSuccess(callback, "false");
    }

    /**
     * 查询所有数据
     *
     * @param context Context
     * @param params  参数
     */
    public void queryAllValueAndKey(ActionCallback callback, Context context, Map<String, String> params) {
        List<KeyValueInfo> kvs = DBHelper.getKeyValues();
        String result = "";
        if (kvs != null && !kvs.isEmpty()) {
            result = JsonUtil.toJson(kvs);
        }
        sendSuccess(callback, result);
    }

    /**
     * 获取定位数据
     *
     * @param context Context
     * @param params  参数
     */
    public void getCurrentLocation(final ActionCallback callback, Context context, Map<String, String> params) {
        if (!AndroidUtils.isOnline()) {
            sendFail(callback, "e001", null);
            return;
        }
        if (!PermissionUtil.isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            sendFail(callback, "e010", null);
            PermissionDialog.newInstance("定位", "定位").show((Activity) context);
        } else {
            sendBegin(callback, "true");
            final AMapLocationClient locationClient = new AMapLocationClient(WorkApplication.getApplication());
            locationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    //{"longitute":120.5995006644577,"city":"苏州市","country":"中国","distirct":"相城区","street":"织锦路",
                    // "latitute":31.37503514381195,"intact_address":"中国江苏省苏州市相城区织锦路",
                    // "altitude":13.72459125518799,"province":"江苏省"}
                    if (aMapLocation.getErrorCode() == 0) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("longitute", aMapLocation.getLongitude());
                        map.put("latitute", aMapLocation.getLatitude());
                        map.put("city", aMapLocation.getCity());
                        map.put("province", aMapLocation.getProvince());
                        map.put("country", aMapLocation.getCountry());
                        map.put("distirct", aMapLocation.getDistrict());
                        map.put("street", aMapLocation.getStreet());
                        map.put("intact_address", aMapLocation.getAddress());
                        map.put("altitude", aMapLocation.getAltitude());
                        sendSuccess(callback, JsonUtil.toJson(map));
                    } else if (aMapLocation.getErrorCode() == 12) {
                        sendFail(callback, "e010", aMapLocation.getErrorInfo());
                    } else if (aMapLocation.getErrorCode() == 18 || aMapLocation.getErrorCode() == 19) {
                        sendFail(callback, "e001", aMapLocation.getErrorInfo());
                    } else {
                        sendFail(callback, "e011", aMapLocation.getErrorInfo());
                    }
                    locationClient.stopLocation();
                }
            });
            locationClient.startLocation();
        }
    }

    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache(final ActionCallback callback, Context context, Map<String, String> params) {

        //清理Webview缓存数据库
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileUtils.deleteFile(new File(WorkApplication.getWebCacheDir()));
        FileUtils.deleteFile(new File(context.getCacheDir(), "webviewCache"));
        sendSuccess(callback, "true");
    }

    /**
     * 上传文件
     *
     * @param callback 回调
     * @param context  Context上下文
     * @param params   参数
     */
    public void doUploadFile_andLogicType_(final ActionCallback callback, Context context, Map<String, String> params) {
        doUploadFileWithOutZip_andLogicType_width_height_(callback, context, params);
    }


    /**
     * 上传文件(重新定义文件高宽--仅限图片)
     *
     * @param callback 回调
     * @param context  Context上下文
     * @param params   参数
     */
    public void doUploadFileWithOutZip_andLogicType_width_height_(final ActionCallback callback, Context context, Map<String, String> params) {
        if (!AndroidUtils.isOnline()) {
            sendFail(callback, "e001", null);
            return;
        }
        sendBegin(callback, "true");
        String path = null;
        String logicType = null;
        if (params != null && params.containsKey("path") && params.containsKey("logic_type")) {
            path = params.get("path");
            logicType = params.get("logic_type");
            if (TextUtils.isEmpty(path)) {
                sendSuccess(callback, "false");
            } else {
                File file = new File(path);
                if (!file.exists() || !file.isFile()) {
                    sendFail(callback, "e009", null);
                } else {
                    int width = 0;
                    int height = 0;
                    if (params.containsKey("width")) {
                        String sw = params.get("width");
                        try {
                            width = Integer.parseInt(sw);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (params.containsKey("height")) {
                        String sh = params.get("height");
                        try {
                            height = Integer.parseInt(sh);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ImageSize size = null;
                    if (width > 0 || height > 0) {
                        size = new ImageSize(width, height);
                    }
                    if (!mEventBus.isRegistered(this)) {
                        mEventBus.register(this);
                    }
                    // 有压缩操作，放子线程中操作
                    UploadFileEvent event = new UploadFileEvent(logicType, file, callback);
                    event.setSize(size);
                    mEventBus.post(event);
                }
            }
        } else {
            sendSuccess(callback, "false");
        }
    }

    /**
     * HMac加密
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void getHmac_(final ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && params.containsKey("content")) {
            sendSuccess(callback, HMacMD5.getHmacMd5Str(params.get("content")));
        } else {
            sendFail(callback, "e006", null);
        }
    }

    /**
     * 解密
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void doDecryptWithAes_(final ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && params.containsKey("content")) {
            String content = params.get("content");
            if (content == null) {
                content = "";
            }
            try {
                String result = AESUtil.decrypt(content);
                sendSuccess(callback, result);
            } catch (Exception e) {
                e.printStackTrace();
                sendFail(callback, "e003", e.getMessage());
            }
        } else {
            sendFail(callback, "e003", null);
        }
    }

    /**
     * 加密
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void doEncryptWithAes_(final ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && params.containsKey("content")) {
            String content = params.get("content");
            if (content == null) {
                content = "";
            }
            try {
                String result = AESUtil.encrypt(content);
//                String result = DHCoder.encrypt(content);
                sendSuccess(callback, result);
            } catch (Exception e) {
                e.printStackTrace();
                sendFail(callback, "e002", e.getMessage());
            }
        } else {
            sendFail(callback, "e002", null);
        }
    }

    /**
     * 打开第三方app
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void openAppForUrl_(final ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && params.containsKey("app")) {
            String url = params.get("app");
            boolean opend = UiUtil.openApp(context, url);
            if (opend) {
                sendSuccess(callback, "true");
            } else {
                sendFail(callback, "e008", "应用未安装或版本太低");
            }
        } else {
            sendFail(callback, "e008", null);
        }
    }

    /**
     * 执行上传操作
     *
     * @param callback  回调
     * @param file      文件
     * @param logicType 业务类型
     * @param size      上传限定图片高宽
     */
    private void doUpload(final ActionCallback callback, File file, String logicType, ImageSize size) {
        UploadInfo info = new UploadInfo();
        int idx = file.getName().lastIndexOf(".");
        if (idx >= 0) {
            info.setFileType(file.getName().substring(idx + 1));
        }
        info.setLogicType(logicType);
        info.setOsVersion(Build.VERSION.RELEASE);
        info.setOs("Android");
        RequestData<UploadInfo> reqData = NetReqUtil.createRequestData("", "", info);
        reqData.setData(info);
        final String zipDst = ZipUtil.zip(file, reqData, size);
        if (!TextUtils.isEmpty(zipDst)) {
            RequestInfo reqInfo = RequestFactory.getInstance().getRequestInfo("r005");
            if (reqInfo != null) {
                sendBegin(callback, "true");
                String url = RequestFactory.getInstance().getUrl(reqInfo);
                NetReqUtil.upload(url, reqData, NetReqUtil.getBaseHeader(), zipDst, new UploadCallback() {
                    @Override
                    public void onProgress(long completed, long length, boolean isDone) {
                        sendProgress(callback, completed * 100 / length + "%");
                    }

                    @Override
                    public void onResult(int code, String result) {
//                        FileUtils.deleteFile(new File(zipDst));
                        if (code == RequestConstants.RESULT_OK) {
                            sendSuccess(callback, result);
                        } else {
                            sendSuccess(callback, "false");
                        }
                    }
                });
            }
        } else {
            sendFail(callback, "e009", null);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundEvent(UploadFileEvent event) {
        mEventBus.unregister(this);
        if (event != null) {
            doUpload(event.getCallback(), event.getFile(), event.getLogicType(), event.getSize());
        }
    }
}
