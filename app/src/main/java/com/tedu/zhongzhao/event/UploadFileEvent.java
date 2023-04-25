package com.tedu.zhongzhao.event;

import com.tedu.zhongzhao.bean.ImageSize;
import com.tedu.zhongzhao.net.UploadInfo;
import com.tedu.zhongzhao.web.ActionCallback;

import java.io.File;

/**
 * 上传文件event
 * Created by huangyx on 2018/7/1.
 */
public class UploadFileEvent {

    private ActionCallback callback;
    private File file;
    private ImageSize size;
    private String logicType;

    public UploadFileEvent(String logicType, File file, ActionCallback callback) {
        this.callback = callback;
        this.file = file;
        this.logicType = logicType;
    }

    public ActionCallback getCallback() {
        return callback;
    }

    public String getLogicType() {
        return logicType;
    }

    public File getFile() {
        return file;
    }

    public ImageSize getSize() {
        return size;
    }

    public void setSize(ImageSize size) {
        this.size = size;
    }
}
