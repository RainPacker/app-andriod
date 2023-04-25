package com.tedu.zhongzhao.task;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

/**
 * Task基本信息
 * Created by huangyx on 2018/3/5.
 */
public class TaskInfo implements java.io.Serializable {

    /**
     * 任务名（类名）
     */
    private String taskName;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 任务返回类型
     */
    private String taskReturnType;
    /**
     * 任务同步类型？
     */
    private String taskSynType;
    /**
     * 是否需要参数
     */
    private String needparam;
    /**
     * 构造方法
     */
    private String constructor;
    /**
     * 初始化参数
     */
    private String initparam;
    /**
     * 多参数
     */
    private String multiparam;
    /**
     * 依赖任务返回结果(指的是依赖的任务名)
     */
    private String dependontaskresult;
    /**
     * 排序
     */
    private int taskOrder;
    /**
     * 任务执行结果
     */
    @Expose
    private String taskResult;
    /**
     * 是否已经执行
     */
    @Expose
    private boolean isExecuted;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskReturnType() {
        return taskReturnType;
    }

    public void setTaskReturnType(String taskReturnType) {
        this.taskReturnType = taskReturnType;
    }

    public String getTaskSynType() {
        return taskSynType;
    }

    public void setTaskSynType(String taskSynType) {
        this.taskSynType = taskSynType;
    }

    public String getNeedparam() {
        return needparam;
    }

    public void setNeedparam(String needparam) {
        this.needparam = needparam;
    }

    public String getConstructor() {
        return constructor;
    }

    public void setConstructor(String constructor) {
        this.constructor = constructor;
    }

    public String getInitparam() {
        return initparam;
    }

    public void setInitparam(String initparam) {
        this.initparam = initparam;
    }

    public String getMultiparam() {
        return multiparam;
    }

    public void setMultiparam(String multiparam) {
        this.multiparam = multiparam;
    }

    public String getDependontaskresult() {
        return dependontaskresult;
    }

    public void setDependontaskresult(String dependontaskresult) {
        this.dependontaskresult = dependontaskresult;
    }

    public int getTaskOrder() {
        return taskOrder;
    }

    public void setTaskOrder(int taskOrder) {
        this.taskOrder = taskOrder;
    }

    public String getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(String taskResult) {
        this.taskResult = taskResult;
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    public void setExecuted(boolean executed) {
        isExecuted = executed;
    }

}
