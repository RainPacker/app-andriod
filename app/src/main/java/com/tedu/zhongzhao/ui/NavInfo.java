package com.tedu.zhongzhao.ui;

import com.google.gson.annotations.SerializedName;

/**
 * 顶部导航（标题栏）信息
 * Created by huangyx on 2018/3/12.
 */
public class NavInfo implements java.io.Serializable {

    /**
     * 标题
     */
    private String title;
    /**
     * 是否显示左按钮
     */
    @SerializedName("isdisplay")
    private boolean isDisplay;
    /**
     * 左按钮
     */
    private NavBtnInfo left;
    /**
     * 右按钮
     */
    private NavBtnInfo right;
    /**
     * 导航样式
     */
    private NavStyleInfo style;

    /**
     * //导航的类型 （1 单一文本导航，2 左侧+中间文本 ，3 右侧+中间文本，4 左侧+中间文本+右侧）
     */
    @SerializedName("navtype")
    private String navType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDisplay() {
        return isDisplay;
    }

    public void setDisplay(boolean display) {
        isDisplay = display;
    }

    public NavBtnInfo getLeft() {
        return left;
    }

    public void setLeft(NavBtnInfo left) {
        this.left = left;
    }

    public NavBtnInfo getRight() {
        return right;
    }

    public void setRight(NavBtnInfo right) {
        this.right = right;
    }

    public NavStyleInfo getStyle() {
        return style;
    }

    public void setStyle(NavStyleInfo style) {
        this.style = style;
    }

    public String getNavType() {
        return navType;
    }

    public void setNavType(String navType) {
        this.navType = navType;
    }
}
