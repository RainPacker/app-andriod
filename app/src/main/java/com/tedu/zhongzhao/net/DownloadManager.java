package com.tedu.zhongzhao.net;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.tedu.base.util.AndroidUtils;
import com.tedu.zhongzhao.WorkApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载管理
 * Created by huangyx on 2018/3/12.
 */
public class DownloadManager {

    private static DownloadManager sInstance;

    synchronized public static DownloadManager getInstance() {
        if (sInstance == null) {
            sInstance = new DownloadManager();
        }
        return sInstance;
    }

    private ExecutorService mService;
    private OkHttpClient mClient;
    private Map<String, DownloadCallable> mItems;
    private Map<String, Future<Boolean>> mFutures;
    private Handler mHandler;

    private DownloadManager() {
        mService = Executors.newScheduledThreadPool(5);
        mClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS).writeTimeout(5, TimeUnit.SECONDS).build();
        mItems = new HashMap<String, DownloadCallable>();
        mFutures = new HashMap<String, Future<Boolean>>();
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 下载
     *
     * @param url      下载链接
     * @param callback 下载回调
     */
    public void download(String url, DownloadCallback callback) {
        if (!TextUtils.isEmpty(url)) {
            // 不在下载列表中的才能进行下载
            if (!mItems.containsKey(url)) {
                DownloadCallable callable = new DownloadCallable(url, callback);
                mItems.put(url, callable);
                Future<Boolean> future = mService.submit(callable);
                mFutures.put(url, future);
            }
        }
    }

    /**
     * 暂停下载
     *
     * @param url
     */
    public void pause(String url) {
        if (!TextUtils.isEmpty(url)) {
            DownloadCallable callable = mItems.get(url);
            if (callable != null) {
                callable.pause();
            }
            Future<Boolean> future = mFutures.get(url);
            if (future != null) {
                future.cancel(true);
            }
        }
    }

    /**
     * 主线程中回调下载失败
     *
     * @param callback DownloadCallback
     * @param code     失败编码
     * @param msg      失败原因
     */
    private void sendDownloadFail(final DownloadCallback callback, final int code, final String msg) {
        if (mHandler != null && callback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFail(code, msg);
                }
            });
        }
    }

    /**
     * 主线程中回调下载进度
     *
     * @param callback  DownloadCallback
     * @param completed 已下载大小
     * @param length    总大小
     */
    private void sendDownload(final DownloadCallback callback, final long completed, final long length) {
        if (mHandler != null && callback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onProgress(completed, length);
                }
            });
        }
    }

    /**
     * 主线程中回调下载失败
     *
     * @param callback DownloadCallback
     * @param path     下载之后文件存储路径
     */
    private void sendDownloadCompleted(final DownloadCallback callback, final String path) {
        if (mHandler != null && callback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onCompleted(path);
                }
            });
        }
    }


    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (!TextUtils.isEmpty(dispositionHeader)) {
            String[] strings = dispositionHeader.split(";");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
            return "";
        }
        return "";
    }

    private class DownloadCallable implements Callable<Boolean> {
        private String url;
        private DownloadCallback callback;
        private DownloadStatus status;

        DownloadCallable(String url, DownloadCallback callback) {
            this.url = url;
            this.callback = callback;
            this.status = DownloadStatus.UNSTART;
        }

        public void pause() {
            this.status = DownloadStatus.PAUSED;
        }

        @Override
        public Boolean call() throws Exception {
            if (mClient != null && AndroidUtils.isOnline() && status == DownloadStatus.UNSTART) {
                status = DownloadStatus.DOWNLOADING;
                Response response = mClient.newCall(new Request.Builder().url(url).build()).execute();
                if (status == DownloadStatus.DOWNLOADING && response != null && response.isSuccessful()
                        && response.body() != null) {
                    long length = response.body().contentLength();
                    String fileName = getHeaderFileName(response);
                    /**
                     *如果服务器没有返回的话,使用自定义的文件名字
                     */
                    if (TextUtils.isEmpty(fileName)) {
                        fileName = System.currentTimeMillis() + ".un";
                    }
                    File file = new File(WorkApplication.getDownloadDir(), fileName);
                    if (file.isFile() && file.exists()) {
                        file.delete();
                    }
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = response.body().byteStream();
                        fos = new FileOutputStream(file);
                        int len, completed = 0;
                        byte[] buf = new byte[2048];
                        while (status == DownloadStatus.DOWNLOADING && (len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            completed += len;
                            sendDownload(callback, completed, length);
                        }
                        if (status == DownloadStatus.DOWNLOADING) {
                            status = DownloadStatus.COMPLETED;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (fos != null) {
                            try {
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (status == DownloadStatus.COMPLETED) {
                        sendDownloadCompleted(callback, file.getAbsolutePath());
                    }
                } else {
                    sendDownloadFail(callback, RequestConstants.RESULT_FAIL, "");
                }
            }
            mItems.remove(url);
            mFutures.remove(url);
            return true;
        }
    }

    /**
     * 下载状态
     */
    public enum DownloadStatus {
        PAUSED,
        DOWNLOADING,
        STOPED,
        UNSTART,
        COMPLETED
    }
}
