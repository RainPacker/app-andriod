package com.tedu.zhongzhao.ui;

import com.google.gson.annotations.SerializedName;

/**
 * 应用入口APP信息
 * Created by huangyx on 2018/3/12.
 */
public class EntranceAppInfo implements java.io.Serializable {

    /**
     * 显示的模板
     */
    @SerializedName("templates")
    private String template;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
