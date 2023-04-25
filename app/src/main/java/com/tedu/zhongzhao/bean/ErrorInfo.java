package com.tedu.zhongzhao.bean;

/**
 * 错误信息
 * Created by huangyx on 2018/6/25.
 */
public class ErrorInfo implements java.io.Serializable {

    private String eid;
    private String alert;

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }
}
