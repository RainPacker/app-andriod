package com.tedu.zhongzhao.ui;

import com.google.gson.annotations.SerializedName;

/**
 * 页面风格样式定义
 * Created by huangyx on 2018/3/12.
 */
public class IconStyleInfo implements java.io.Serializable {
    /**
     * 高亮图标
     */
    @SerializedName("hl_icon")
    private String hlcon;
    /**
     * 正常图标
     */
    @SerializedName("nl_icon")
    private String nIcon;

    /**
     * 背景颜色 #FFFFFF
     */
    @SerializedName("bgcolor")
    private String bgColor;

    /**
     * 背景图片
     */
    @SerializedName("bgimg")
    private String bgImg;

    public String getHlcon() {
        return hlcon;
    }

    public void setHlcon(String hlcon) {
        this.hlcon = hlcon;
    }

    public String getnIcon() {
        return nIcon;
    }

    public void setnIcon(String nIcon) {
        this.nIcon = nIcon;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }
}
