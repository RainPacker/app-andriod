package com.tedu.zhongzhao.net;

import com.google.gson.annotations.SerializedName;

/**
 * 上传的文件信息
 * Created by huangyx on 2018/5/13.
 */
public class UploadInfo implements java.io.Serializable {

    private String os;
    @SerializedName("file_id")
    private String fileId;
    @SerializedName("file_type")
    private String fileType;
    @SerializedName("file_size")
    private String fileSize;
    @SerializedName("file_name")
    private String fileName;
    @SerializedName("osversion")
    private String osVersion;
    @SerializedName("logic_type")
    private String logicType;

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getLogicType() {
        return logicType;
    }

    public void setLogicType(String logicType) {
        this.logicType = logicType;
    }
}
