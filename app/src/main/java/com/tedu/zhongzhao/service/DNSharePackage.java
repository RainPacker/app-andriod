package com.tedu.zhongzhao.service;

import android.content.Context;
import android.text.TextUtils;

import com.tedu.zhongzhao.bean.ShareInfo;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.zhongzhao.utils.ShareUtil;
import com.tedu.zhongzhao.web.ActionCallback;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * 分享
 * Created by huangyx on 2018/4/19.
 */
public class DNSharePackage extends BaseService {

    private static DNSharePackage sInstance;

    synchronized public static DNSharePackage getInstance() {
        if (sInstance == null) {
            sInstance = new DNSharePackage();
        }
        return sInstance;
    }

    private DNSharePackage() {

    }

    /**
     * 指定分享平台进行分享
     *
     * @param params
     */
    public void doPublishContent_andShareType_(final ActionCallback callback, Context context, Map<String, String> params) {
        sendBegin(callback, "");
        if (params != null) {
            String shareType = null;
            ShareInfo shareInfo = null;
            if (params.containsKey("shareType")) {
                shareType = params.get("shareType");
            }
            if (params.containsKey("shareInfo")) {
                String info = params.get("shareInfo");
                if (!TextUtils.isEmpty(info)) {
                    shareInfo = JsonUtil.fromJson(info, ShareInfo.class);
                }
            }
            if (!TextUtils.isEmpty(shareType) && shareInfo != null) {
                ShareUtil.SharePlatform platform = null;
                if ("22".equals(shareType)) {
                    // 微信好友
                    platform = ShareUtil.SharePlatform.WECHAT;
                } else if ("23".equals(shareType)) {
                    // 微信朋友圈
                    platform = ShareUtil.SharePlatform.WECHAT_MOMENTS;
                } else if ("1".equals(shareType)) {
                    // 新浪微博
                    platform = ShareUtil.SharePlatform.SINA;
                } else if ("19".equals(shareType)) {
                    // 短信
                    platform = ShareUtil.SharePlatform.SMS;
                } else if ("18".equals(shareType)) {
                    // 邮件
                    platform = ShareUtil.SharePlatform.EMAIL;
                }
                ShareUtil.share(context, platform, shareInfo, new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        sendSuccess(callback, "true");
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        sendSuccess(callback, "false");
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        sendCancel(callback);
                    }
                });
                return;
            }
        }
        sendSuccess(callback, "false");
    }

    public void doPublishContent_andType_andContainer_(final ActionCallback callback, Context context, Map<String, String> params) {
        sendBegin(callback, "");
        if (params != null) {
            ShareInfo shareInfo = null;
            if (params.containsKey("shareInfo")) {
                String info = params.get("shareInfo");
                if (!TextUtils.isEmpty(info)) {
                    shareInfo = JsonUtil.fromJson(info, ShareInfo.class);
                }
            }
            if (shareInfo != null) {
                ShareUtil.share(context, shareInfo, new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        sendSuccess(callback, "true");
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        sendSuccess(callback, "false");
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        sendCancel(callback);
                    }
                });
                return;
            }
        }
        sendSuccess(callback, "false");
    }
}
