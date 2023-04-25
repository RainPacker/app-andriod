package com.tedu.zhongzhao.event;

/**
 * Created by huangyx on 2018/8/20.
 */
public class AliyShortVideoEvent {

    public static final int RESULT_CANCEL = -1;
    public static final int RESULT_OK = 1;
    public static final int RESULT_FAIL = 2;

    private int result;
    private String path;

    public AliyShortVideoEvent(int result, String path) {
        this.result = result;
        this.path = path;
    }

    public int getResult() {
        return result;
    }

    public String getPath() {
        return path;
    }
}
