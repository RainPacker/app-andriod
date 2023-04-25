package com.tedu.zhongzhao.event;

/**
 * 拍照结果Event
 * Created by huangyx on 2018/4/21.
 */
public class PhotoResultEvent {

    public static final int RESULT_OK = 1;
    public static final int RESULT_CANCEL = 2;

    public static final int TYPE_PHOTO = 2;
    public static final int TYPE_ALBUM = 3;
    public static final int TYPE_VIDEO = 4;

    public int type;
    public int result;
    public String path;

    public PhotoResultEvent(int type, int result) {
        this.type = type;
        this.result = result;
    }
}
