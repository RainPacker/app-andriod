package com.tedu.zhongzhao.service;

import android.content.Context;
import android.text.TextUtils;

import com.tedu.zhongzhao.bean.UserInfo;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.zhongzhao.utils.SettingUtil;
import com.tedu.zhongzhao.web.ActionCallback;

import java.util.Map;

/**
 * 用户Service
 * Created by huangyx on 2018/5/2.
 */
public class DNUserService extends BaseService {

    private static DNUserService sInstance;

    synchronized public static DNUserService getInstance() {
        if (sInstance == null) {
            sInstance = new DNUserService();
        }
        return sInstance;
    }

    private UserInfo mUser;

    private DNUserService() {
        String jsonStr = SettingUtil.getUserInfoJson();
        if (!TextUtils.isEmpty(jsonStr)) {
            mUser = JsonUtil.fromJson(jsonStr, UserInfo.class);
        }
    }

    /**
     * 保存用户信息
     *
     * @param context
     * @param params
     */
    public void saveUserInfo_(ActionCallback callback, Context context, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("userinfo")) {
                String value = params.get("userinfo");
                if (!TextUtils.isEmpty(value)) {
                    mUser = JsonUtil.fromJson(value, UserInfo.class);
                    SettingUtil.saveUserInfo(value);
                    sendSuccess(callback, "true");
                    return;
                }
            }
        }
        sendSuccess(callback, "false");
    }

    /**
     * 获取 uid
     * @return 用户uid
     */
    public String getUid() {
        if (mUser != null) {
            return mUser.getUserId();
        }
        return "";
    }

    public UserInfo getUser(){
        return mUser;
    }
}
