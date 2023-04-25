package com.tedu.zhongzhao.task;

import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.utils.SpreadUtil;

public class FetchStarupPageTask extends BaseTask {
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
        if (WorkApplication.getApplication().isShowLoadStartPage()) {
            SpreadUtil.loadSpreadImage();
        }
        return null;
    }
}
