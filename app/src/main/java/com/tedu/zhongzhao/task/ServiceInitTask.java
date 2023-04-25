package com.tedu.zhongzhao.task;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.tedu.zhongzhao.service.ServiceFactory;
import com.tedu.zhongzhao.service.ServiceInfo;
import com.tedu.zhongzhao.utils.FileUtils;
import com.tedu.zhongzhao.utils.JsonUtil;

import java.util.List;

/**
 * 业务逻辑处理
 * Created by huangyx on 2018/3/14.
 */
public class ServiceInitTask extends BaseTask {
    private String mConfigPath;

    @Override
    public void init() {
        init("serviceconfig", false);
    }

    @Override
    public void init(String param, boolean isMultiParams) {
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
                ServiceList list = JsonUtil.fromJson(json, ServiceList.class);
                if (list != null) {
                    ServiceFactory.getInstance().setServices(list.getServices());
                }
            }
        }
        return null;
    }

    private class ServiceList implements java.io.Serializable {
        private List<ServiceDefine> servicedefine;
        private List<ServiceInfo> services;

        public List<ServiceInfo> getServices() {
            return services;
        }

        public void setServices(List<ServiceInfo> services) {
            this.services = services;
        }

        public List<ServiceDefine> getServicedefine() {
            return servicedefine;
        }

        public void setServicedefine(List<ServiceDefine> servicedefine) {
            this.servicedefine = servicedefine;
        }
    }

    private class ServiceDefine implements java.io.Serializable {

        @SerializedName("service_class_name")
        private String className;
        @SerializedName("is_singleton")
        private String isSingle;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getIsSingle() {
            return isSingle;
        }

        public void setIsSingle(String isSingle) {
            this.isSingle = isSingle;
        }
    }
}
