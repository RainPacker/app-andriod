package com.tedu.zhongzhao.map;

import com.google.gson.annotations.SerializedName;

/**
 * POI信息
 * Created by huangyx on 2018/4/21.
 */
public class PoiInfo implements java.io.Serializable {

    private Double lat;
    private Double lon;
    private String title;
    @SerializedName("subtitle")
    private String subTitle;
    private String tel;
    private String img;
    private String viewType;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLog(Double lon) {
        this.lon = lon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
}
