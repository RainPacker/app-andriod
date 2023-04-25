package com.tedu.zhongzhao.task;

import android.text.TextUtils;

import com.tedu.zhongzhao.net.RequestFactory;
import com.tedu.zhongzhao.net.RequestInfo;
import com.tedu.zhongzhao.utils.FileUtils;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.base.util.LogUtil;

import java.util.List;

/**
 * 网络初始化任务
 * Created by huangyx on 2018/3/6.
 */
public class NetWorkInitTask extends BaseTask {

    private final static String TAG = NetWorkInitTask.class.getSimpleName();

    private String mConfigPath;

    public NetWorkInitTask() {
        super();
    }

    @Override
    public void init() {

    }

    @Override
    public void init(String param, boolean isMultiParams) {

    }


    public void initWithNetWorkConfig() {
        init();
        LogUtil.d(TAG, "initWithNetWorkConfig without params:");
    }

    public void initWithNetWorkConfig(String param, boolean isMultiParams) {
        init(param, isMultiParams);
        this.mConfigPath = param;
    }

    @Override
    public void acceptParam(String params) {

    }

    @Override
    public String doTask() {
        if (!TextUtils.isEmpty(mConfigPath)) {
            String json = FileUtils.readAssetFile("config/" + mConfigPath + ".txt");
            if (!TextUtils.isEmpty(json)) {
                RequestList list = JsonUtil.fromJson(json, RequestList.class);
                if (list != null) {
                    RequestFactory.getInstance().init(list.getPrefix(), list.getRequests());
                }
            }
        }
        return null;
    }

    private class RequestList implements java.io.Serializable {
        private String prefix;
        private List<RequestInfo> requests;

        public List<RequestInfo> getRequests() {
            return requests;
        }

        public void setRequests(List<RequestInfo> requests) {
            this.requests = requests;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }
}

