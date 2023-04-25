package com.tedu.zhongzhao.net;

/**
 * 下载回调
 * Created by huangyx on 2018/3/8.
 */
public interface DownloadCallback {

    void onProgress(long completed, long length);

    void onFail(int code, String msg);

    void onCompleted(String path);
}
