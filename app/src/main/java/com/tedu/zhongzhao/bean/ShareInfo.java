package com.tedu.zhongzhao.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 分享内容
 * Created by huangyx on 2018/4/19.
 */
public class ShareInfo implements java.io.Serializable {

    private String text;
    private String url;
    private String title;
    @SerializedName("sharecontentType")
    private String contentType;
    private List<String> images;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
