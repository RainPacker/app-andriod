package com.tedu.zhongzhao.task;


import com.tedu.zhongzhao.utils.SpreadUtil;

public class FetchLeadingPageTask extends BaseTask {

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
        SpreadUtil.loadSpreadGuideImages();
        return null;
    }
}
