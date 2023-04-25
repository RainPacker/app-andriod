package com.tedu.zhongzhao.task;

/**
 * Task执行基类
 * Created by huangyx on 2018/3/5.
 */
public abstract class BaseTask implements java.io.Serializable {

    public BaseTask() {

    }

    abstract public void init();


    abstract public void init(String param, boolean isMultiParams);

    abstract public void acceptParam(String params);

    abstract public String doTask();

}
