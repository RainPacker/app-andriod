package com.tedu.zhongzhao.bean;

import java.util.List;

/**
 * 启动页信息
 */
public class SpreadInfo implements java.io.Serializable {

    private int updateFlag;
    private int versionId;
    private List<String> urlList;

    public int getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(int updateFlag) {
        this.updateFlag = updateFlag;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }
}
