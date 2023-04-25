package com.tedu.zhongzhao.net;

import android.text.TextUtils;

import com.tedu.zhongzhao.utils.SpreadUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络请求工厂
 * Created by huangyx on 2018/3/6.
 */
public class RequestFactory {

    private static final String TAG = RequestFactory.class.getSimpleName();

    private static RequestFactory sInstance;

    synchronized public static RequestFactory getInstance() {
        if (sInstance == null) {
            sInstance = new RequestFactory();
        }
        return sInstance;
    }

    /**
     * 内置请求信息
     */
    private Map<String, RequestInfo> mInitRequests;
    private String mPrefix;

    private RequestFactory() {
        mInitRequests = new HashMap<String, RequestInfo>();
    }


    /**
     * 初始化内置请求信息
     *
     * @param requests List<RequestInfo>
     */
    public void init(String prefix, List<RequestInfo> requests) {
        this.mPrefix = prefix;
        mInitRequests.clear();
        if (requests != null && !requests.isEmpty()) {
            for (RequestInfo r : requests) {
                mInitRequests.put(r.getReqId(), r);
            }
        }
    }

    /**
     * 通过请求ID，获取对应请求信息
     *
     * @param reqId 请求ID
     * @return RequestInfo
     */
    public RequestInfo getRequestInfo(String reqId) {
        if (TextUtils.isEmpty(reqId)) {
            return null;
        }
        if (mInitRequests.containsKey(reqId)) {
            return mInitRequests.get(reqId);
        }
        return null;
    }

    /**
     * 发起网络请求
     *
     * @param info     网络请求信息
     * @param reqData  参数
     * @param callback 回调
     */
    public <T> void request(RequestInfo info, RequestData reqData, RequestCallback<T> callback, Class<T> clazz) {
        if (info != null) {
            // 正常表单提交
            if (RequestConstants.isNormal(info.getReqType())) {
                if (RequestConstants.isUpload(info.getAction())) {
                    // TODO 不在此处处理上传
                } else if (RequestConstants.isDownload(info.getAction())) {
                    // TODO 不在此处处理下载
                } else {
                    if (RequestConstants.isGet(info.getMethod())) {
                        NetReqUtil.doGet(getUrl(info), reqData, null, callback, clazz);
                    } else {
                        NetReqUtil.doPost(getUrl(info), reqData, false, null, callback, clazz);
                    }
                }
            } else {
                NetReqUtil.doPost(getUrl(info), reqData, true, null, callback, clazz);
            }
        }
    }

    /**
     * 资源回收
     */
    public void release() {

    }

    /**
     * 组装URL
     *
     * @param info RequestInfo
     * @return url地址
     */
    public String getUrl(RequestInfo info) {
        String tmpUrl = info.getReqUrl();
        if (!TextUtils.isEmpty(tmpUrl)) {
            if (!tmpUrl.contains("://")) {
                if (!TextUtils.isEmpty(mPrefix)) {
                    if (mPrefix.endsWith("/") || tmpUrl.startsWith("/")) {
                        tmpUrl = mPrefix + tmpUrl;
                    } else {
                        tmpUrl = mPrefix + "/" + tmpUrl;
                    }
                }
            }
        }
        return tmpUrl;
    }
}
