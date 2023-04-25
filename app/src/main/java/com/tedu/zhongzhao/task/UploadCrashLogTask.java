package com.tedu.zhongzhao.task;

import android.os.Build;
import android.text.TextUtils;

import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.net.NetReqUtil;
import com.tedu.zhongzhao.net.RequestConstants;
import com.tedu.zhongzhao.net.RequestData;
import com.tedu.zhongzhao.net.RequestFactory;
import com.tedu.zhongzhao.net.RequestInfo;
import com.tedu.zhongzhao.net.UploadCallback;
import com.tedu.zhongzhao.net.UploadInfo;
import com.tedu.zhongzhao.utils.FileUtils;
import com.tedu.base.util.LogUtil;
import com.tedu.zhongzhao.utils.ZipUtil;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 上传崩溃日志task
 * Created by huangyx on 2018/4/16.
 */
public class UploadCrashLogTask extends BaseTask {

    @Override
    public void init() {

    }

    @Override
    public void init(String param, boolean isMultiParams) {

    }

    @Override
    public void acceptParam(String params) {

    }

    @Override
    public String doTask() {
        File file = new File(WorkApplication.getCrashDir());
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    boolean isCrash = name.endsWith(".crash");
                    LogUtil.i("hyx", dir + ", " + isCrash);
                    return isCrash;
                }
            });
            if (files != null && files.length > 0) {
                for (File s : files) {
                    uploadCrash(s);
                }
            }
        }
        return null;
    }

    /**
     * 上传crash文件
     *
     * @param file crash文件
     */
    private void uploadCrash(final File file) {
        UploadInfo info = new UploadInfo();
        info.setFileType("log");
        info.setLogicType("1");
        info.setOsVersion(Build.VERSION.RELEASE);
        info.setOs("Android");
        RequestData<UploadInfo> reqData = NetReqUtil.createRequestData("", "", info);
        reqData.setData(info);
        final String zipDst = ZipUtil.zip(file, reqData);
        if (!TextUtils.isEmpty(zipDst)) {
            RequestInfo reqInfo = RequestFactory.getInstance().getRequestInfo("r005");
            if (reqInfo != null) {
                String url = RequestFactory.getInstance().getUrl(reqInfo);
                NetReqUtil.upload(url, reqData, null, zipDst, new UploadCallback() {
                    @Override
                    public void onProgress(long completed, long length, boolean isDone) {

                    }

                    @Override
                    public void onResult(int code, String result) {
                        FileUtils.deleteFile(new File(zipDst));
                        if (code == RequestConstants.RESULT_OK) {
                            FileUtils.deleteFile(file);
                        }
                    }
                });
            }
        }
    }
}
