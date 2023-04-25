package com.tedu.zhongzhao.ui;

import java.util.List;

/**
 * 应用入口界面信息
 * Created by huangyx on 2018/3/12.
 */
public class EntranceInfo implements java.io.Serializable {

    private EntranceAppInfo appinfo;
    /**
     * 页面信息
     */
    private List<PageInfo> items;

    public EntranceAppInfo getAppinfo() {
        return appinfo;
    }

    public void setAppinfo(EntranceAppInfo appinfo) {
        this.appinfo = appinfo;
    }

    public List<PageInfo> getItems() {
        return items;
    }

    public void setItems(List<PageInfo> items) {
        this.items = items;
    }
}
