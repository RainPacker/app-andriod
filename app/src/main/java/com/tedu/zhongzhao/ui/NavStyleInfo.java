package com.tedu.zhongzhao.ui;

import com.google.gson.annotations.SerializedName;

/**
 * 导航（标题栏）样式
 * Created by huangyx on 2018/3/12.
 */
public class NavStyleInfo implements java.io.Serializable {

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
