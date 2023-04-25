package com.tedu.zhongzhao.tracking;

/**
 * 埋点数据
 * Created by huangyx on 2018/5/13.
 */
class TrackData implements java.io.Serializable {
    // 交互ID
    private String interact_id;
    // 页面ID
    private String page_id;
    // 页面操作ID，服务端定义
    private String act_id;
    // 触发时间
    private String beigintime;
    // 结束时间
    private String endtime;
    // 调用参数
    private String param;

    public String getInteract_id() {
        return interact_id;
    }

    public void setInteract_id(String interact_id) {
        this.interact_id = interact_id;
    }

    public String getPage_id() {
        return page_id;
    }

    public void setPage_id(String page_id) {
        this.page_id = page_id;
    }

    public String getAct_id() {
        return act_id;
    }

    public void setAct_id(String act_id) {
        this.act_id = act_id;
    }

    public String getBeigintime() {
        return beigintime;
    }

    public void setBeigintime(String beigintime) {
        this.beigintime = beigintime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
