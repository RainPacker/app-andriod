package com.tedu.zhongzhao.task;

import com.tedu.zhongzhao.WorkApplication;

public class ChangeStartPageTask extends BaseTask {

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
        WorkApplication.getApplication().setShowLoadStartPage(true);
        return null;
    }
}
