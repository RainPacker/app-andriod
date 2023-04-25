package com.tedu.zhongzhao.task;

import android.text.TextUtils;

import com.tedu.zhongzhao.ui.EntranceInfo;
import com.tedu.zhongzhao.ui.UiUtil;
import com.tedu.zhongzhao.utils.FileUtils;
import com.tedu.zhongzhao.utils.JsonUtil;

/**
 * Created by huangyx on 2018/4/16.
 */
public class BuildViewTask extends BaseTask {

    private String path;

    public void init() {
        init("structureconfig", false);
    }

    @Override
    public void init(String param, boolean isMultiParams) {
        this.path = param;
    }

    @Override
    public void acceptParam(String params) {

    }

    @Override
    public String doTask() {
        if (!TextUtils.isEmpty(path)) {
            String json = FileUtils.readAssetFile("config/" + path + ".txt");
            if (!TextUtils.isEmpty(json)) {
                EntranceInfo entranceInfo = JsonUtil.fromJson(json, EntranceInfo.class);
                UiUtil.setEntranceInfo(entranceInfo);
            }
        }
        return null;
    }
}
