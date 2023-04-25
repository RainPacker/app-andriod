package com.tedu.zhongzhao.ui;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 页面信息
 * Created by huangyx on 2018/3/12.
 */
public class PageInfo implements java.io.Serializable {

    public static final String ACT_PUSH = "1";
    public static final String ACT_PRESENT = "2";
    public static final String ACT_BACK = "3";
    public static final String ACT_BACK_2 = "4";
    public static final String ACT_SERVICE = "5";
    public static final String ACT_OPEN_APP = "6";

    /**
     * Element id
     */
    @SerializedName("el_id")
    private String elId;
    /**
     * Path id
     */
    @SerializedName("path_id")
    private String pathId;
    @SerializedName("p_path_id")
    private String parentPathId;
    private String act;
    /**
     * 服务调用Id， act=5是调用
     */
    private String sid;
    /**
     * 模板
     */
    private String templates;
    /**
     * 页面风格
     */
    private IconStyleInfo style;
    /**
     * 显示名字
     */
    private String name;
    /**
     * 导航信息
     */
    private NavInfo navinfo;
    /**
     * 页面信息
     */
    @SerializedName("v")
    private PageViewInfo view;
    /**
     * 页面显示控制器
     */
    @SerializedName("c")
    private String control;

    private boolean showTab;
    /**
     * 子页面列表
     */
    private List<PageInfo> items;

    private List<PageNotifi> notifies;

    public String getElId() {
        return elId;
    }

    public void setElId(String elId) {
        this.elId = elId;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getTemplates() {
        return templates;
    }

    public void setTemplates(String template) {
        this.templates = template;
    }

    public String getParentPathId() {
        return parentPathId;
    }

    public void setParentPathId(String parentPathId) {
        this.parentPathId = parentPathId;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public IconStyleInfo getStyle() {
        return style;
    }

    public void setStyle(IconStyleInfo style) {
        this.style = style;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NavInfo getNavinfo() {
        return navinfo;
    }

    public void setNavinfo(NavInfo navinfo) {
        this.navinfo = navinfo;
    }

    public PageViewInfo getView() {
        return view;
    }

    public void setView(PageViewInfo view) {
        this.view = view;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public boolean isShowTab() {
        return showTab;
    }

    public void setShowTab(boolean showTab) {
        this.showTab = showTab;
    }

    public List<PageInfo> getItems() {
        return items;
    }

    public void setItems(List<PageInfo> items) {
        this.items = items;
    }

    public List<PageNotifi> getNotifies() {
        return notifies;
    }

    public void setNotifis(List<PageNotifi> notifies) {
        this.notifies = notifies;
    }
}
