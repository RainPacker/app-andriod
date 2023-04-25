package com.tedu.zhongzhao.net;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.ByteString;

/**
 * 上传Callback
 * Created by huangyx on 2018/5/15.
 */
public interface UploadCallback extends RequestCallback<String> {

    void onProgress(long completed, long length, boolean isDone);

}
