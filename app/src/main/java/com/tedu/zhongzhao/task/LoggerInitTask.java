package com.tedu.zhongzhao.task;

import com.tedu.base.util.LogUtil;

/**
 * 初始化logger的task
 * <p>
 * Created by huangyx on 2018/3/5.
 */
public class LoggerInitTask extends BaseTask {

    private static final String TAG = LoggerInitTask.class.getSimpleName();

    private String mParams;

    public LoggerInitTask() {
        super();
    }

    @Override
    public void init() {
        LogUtil.d(TAG, "init without params");
    }

    @Override
    public void init(String params, boolean isMultiParams) {
        this.mParams = params;
        LogUtil.d(TAG, "init with params:" + params + ", isMulti:" + isMultiParams);
    }

    @Override
    public void acceptParam(String params) {
        this.mParams = params;
        LogUtil.d(TAG, "acceptParam:" + params);
    }

    @Override
    public String doTask() {
        return "LoggerInitTask completed:" + mParams;
    }

}
