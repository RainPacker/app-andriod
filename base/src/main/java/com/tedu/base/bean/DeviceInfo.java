package com.tedu.base.bean;

import java.io.Serializable;

/**
 * 设备信息
 * Created by huangyx on 2018/4/5.
 */
public class DeviceInfo implements Serializable {

    /**
     * 系统
     */
    private String osPlatform;
    /**
     * 系统版本
     */
    private String osVersion;
    /**
     * 手机型号
     */
    private String model;
    /**
     * 分辨率
     */
    private String screen;
    /**
     * 网络类型
     */
    private String net;
    /**
     * 运营商
     */
    private String operator;
    /**
     * 唯一标识
     */
    private String unique;

    public String getOsPlatform() {
        return osPlatform;
    }

    public void setOsPlatform(String osPlatform) {
        this.osPlatform = osPlatform;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }
}
