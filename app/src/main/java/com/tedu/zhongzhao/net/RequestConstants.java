package com.tedu.zhongzhao.net;

/**
 * 请求类的常用常量
 * Created by huangyx on 2018/3/6.
 */
public class RequestConstants {
    /**
     * 请求结果：成功
     */
    public static final int RESULT_OK = 200;
    /**
     * 请求结果：失败
     */
    public static final int RESULT_FAIL = -1;
    /**
     * 请求结果：网络异常
     */
    public static final int RESULT_NET_ERROR = -2;

    /**
     * GET方法提交
     */
    public static final String METHOD_GET = "GET";
    /**
     * POST方法提交
     */
    public static final String METHOD_POST = "POST";
    /**
     * 提交类型：json
     */
    public static final String REQ_TYPE_JSON = "json";
    /**
     * 提交类型：普通表单
     */
    public static final String REQ_TYPE_NORMAL = "normal";
    /**
     * 普通表单情况下上传文件
     */
    public static final String FILE_ACTION_UPLOAD = "0";
    /**
     * 普通表单情况下下载文件
     */
    public static final String FILE_ACTION_DOWNLOAD = "1";

    /**
     * 判断是不是GET提交
     *
     * @param method 请求方式
     * @return true/false
     */
    public static boolean isGet(String method) {
        return !METHOD_POST.equals(method);
    }

    /**
     * 判断是不是普通表单提交
     *
     * @param reqType 提交类型
     * @return true/false
     */
    public static boolean isNormal(String reqType) {
        return !REQ_TYPE_JSON.equals(reqType);
    }

    /**
     * 判断是否为下载文件
     *
     * @param action
     * @return true/false
     */
    public static boolean isDownload(String action) {
        return FILE_ACTION_DOWNLOAD.equals(action);
    }

    /**
     * 判断是否为上传文件
     *
     * @param action 上传
     * @return
     */
    public static boolean isUpload(String action) {
        return FILE_ACTION_UPLOAD.equals(action);
    }
}
