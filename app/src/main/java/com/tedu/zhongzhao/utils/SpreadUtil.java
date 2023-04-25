package com.tedu.zhongzhao.utils;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.bean.SpreadInfo;
import com.tedu.zhongzhao.net.NetReqUtil;
import com.tedu.zhongzhao.net.RequestCallback;
import com.tedu.zhongzhao.net.RequestData;
import com.tedu.zhongzhao.net.RequestFactory;
import com.tedu.zhongzhao.net.RequestInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 启动页工具
 */
public class SpreadUtil {

    private SpreadUtil() {

    }

    /**
     * 新手引导信息
     *
     * @return
     */
    public static SpreadInfo getGuideInfo() {
        SpreadInfo s = getSpreadInfo(1);
        if (s != null && s.getVersionId() != getShowedGuideVersion()) {
            return s;
        }
        return null;
    }

    /**
     * 启动信息
     *
     * @return
     */
    public static SpreadInfo getSpreadInfo() {
        return getSpreadInfo(0);
    }

    /**
     * 已显示的亲手引导版本
     *
     * @return
     */
    private static int getShowedGuideVersion() {
        return SettingUtil.getInt("guide_version", 0);
    }

    /**
     * 设置新手引导已显示
     *
     * @param s SpreadInfo
     */
    public static void setGuideShowed(SpreadInfo s) {
        SettingUtil.putInt("guide_version", s == null ? 0 : s.getVersionId());
    }

    /**
     * 启动信息
     *
     * @param type （0启动页，1-引导图）
     * @return
     */
    private static SpreadInfo getSpreadInfo(int type) {
        String value = SettingUtil.getString("spread-" + type, null);
        if (!TextUtils.isEmpty(value)) {
            SpreadInfo s = JsonUtil.fromJson(value, SpreadInfo.class);
            return s;
        }
        return null;
    }

    /**
     * 保存启动信息
     *
     * @param type （0启动页，1-引导图）
     * @param info 启动信息
     */
    private static void saveSpreadInfo(int type, SpreadInfo info) {
        SettingUtil.putString("spread-" + type, info == null ? "" : JsonUtil.toJson(info));
    }

    /**
     * 网络获取启动页图
     */
    public static void loadSpreadImage() {
        load(0);
    }

    /**
     * 网络获取新手引导页图
     */
    public static void loadSpreadGuideImages() {
        load(1);
    }

    /**
     * 网络加载数据
     *
     * @param type 要获取的类型（0启动页，1-引导图）
     */
    private static void load(final int type) {
        RequestInfo requestInfo = RequestFactory.getInstance().getRequestInfo("r008");
        if (requestInfo != null) {
            SpreadInfo si = getSpreadInfo(type);
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("type", String.valueOf(type));
            data.put("versionCode", si == null ? "0" : String.valueOf(si.getVersionId()));
            RequestData<HashMap<String, String>> requestData = NetReqUtil.createRequestData("", "", data);
            RequestFactory.getInstance().request(requestInfo, requestData, new RequestCallback<String>() {
                @Override
                public void onResult(int code, String result) {
                    if (code == 200) {
                        try {
                            JSONObject json = new JSONObject(result);
                            String data = JsonUtil.getString(json, "data");
                            if (!TextUtils.isEmpty(data)) {
                                SpreadInfo spread = JsonUtil.fromJson(data, SpreadInfo.class);
                                if (spread != null) {
                                    if (spread.getUpdateFlag() == 0) {
                                        saveSpreadInfo(type, spread);
                                        if (spread.getUrlList() != null && !spread.getUrlList().isEmpty()) {
                                            for (String u : spread.getUrlList()) {
                                                if (!TextUtils.isEmpty(u)) {
                                                    Glide.with(WorkApplication.getApplication()).load(u).preload();
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    saveSpreadInfo(type, null);
                                }
                            } else {
                                saveSpreadInfo(type, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, String.class);
        }
    }
}
