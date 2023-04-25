package com.tedu.zhongzhao.task;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.tedu.zhongzhao.utils.FileUtils;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.base.util.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 任务处理
 * Created by huangyx on 2018/3/5.
 */
public class TaskExecutor {

    private static final String TAG = TaskExecutor.class.getSimpleName();
    private static TaskExecutor sInstance;

    synchronized public static TaskExecutor getInstance() {
        if (sInstance == null) {
            sInstance = new TaskExecutor();
        }
        return sInstance;
    }

    private ExecutorService mService;
    /**
     * 任务列表
     */
    private List<TaskInfo> mTasks;
    private boolean isRunning;
    /**
     * 任务队列
     */
    private BlockingQueue<TaskInfo> mQueue;

    private TaskExecutor() {
        mTasks = new ArrayList<TaskInfo>();
        mQueue = new ArrayBlockingQueue<TaskInfo>(20);
        mService = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r, "TaskExecutor");
                thread.setPriority(Thread.NORM_PRIORITY - 1);
                return thread;
            }
        });
    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            mService.execute(new Runnable() {
                @Override
                public void run() {
                    TaskInfo task = null;
                    while (true) {
                        try {
                            task = mQueue.take();
                            executeTask(task);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            if (task != null) {
                                mQueue.remove(task);
                            }
                        }

                    }
                }
            });
            String json = FileUtils.readAssetFile("config/taskconfig.txt");
            if (!TextUtils.isEmpty(json)) {
                TaskList list = JsonUtil.fromJson(json, TaskList.class);
                if (list != null) {
                    putTasks(list.getSystemTasks());
                    putTasks(list.getCustomTasks());
                }
            }
        }
    }

    /**
     * 添加任务列表
     *
     * @param tasks List<TaskInfo>
     */

    private void putTasks(List<TaskInfo> tasks) {
        if (tasks != null && !tasks.isEmpty()) {
//            Collections.sort(tasks, new SortTask());
            for (TaskInfo t : tasks) {
                mQueue.offer(t);
                mTasks.add(t);
            }
        }
    }

    /**
     * 执行任务
     *
     * @param task TaskInfo
     */
    private void executeTask(TaskInfo task) {
        if (task == null || TextUtils.isEmpty(task.getTaskName())) {
            if (task != null) {
                task.setExecuted(true);
            }
            return;
        }
        boolean dependOn = !TextUtils.isEmpty(task.getDependontaskresult());
        TaskInfo depend = null;
        if (dependOn) {
            // 先执行依赖的
            depend = getTask(task.getDependontaskresult());
            if (depend != null) {
                mQueue.remove(depend);
                if (!depend.isExecuted()) {
                    executeTask(depend);
                }
            }
        }

        try {
            Class clazz = Class.forName("com.tedu.zhongzhao.task." + task.getTaskName());
            if (clazz == null) {
                task.setExecuted(true);
                return;
            }
            String constructor = task.getConstructor();
            if (TextUtils.isEmpty(constructor)) {
                constructor = "init";
            } else {
                constructor = constructor.replace(":", "");
            }
            boolean needParam = "1".equals(task.getNeedparam());
            Object obj = clazz.newInstance();
            if (needParam) {
                boolean isMultiParam = !TextUtils.isEmpty(task.getMultiparam());
                String params = isMultiParam ? task.getMultiparam() : task.getInitparam();
                Method method = clazz.getDeclaredMethod(constructor, String.class, boolean.class);
                method.invoke(obj, params, isMultiParam);
            } else {
                Method method = clazz.getDeclaredMethod(constructor);
                method.invoke(obj);
            }
            BaseTask doTask = (BaseTask) obj;
            if (dependOn && depend != null) {
                doTask.acceptParam(depend.getTaskResult());
            }
            String result = doTask.doTask();
            task.setTaskResult(result);
            task.setExecuted(true);
            LogUtil.i(TAG, "do task:" + task.getTaskName() + ", result:" + result);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据任务名获取任务
     *
     * @param taskName 任务名
     * @return TaskInfo
     */
    private TaskInfo getTask(String taskName) {
        if (TextUtils.isEmpty(taskName) || mTasks == null) {
            return null;
        }
        TaskInfo result = null;
        for (TaskInfo t : mTasks) {
            if (taskName.equals(t.getTaskName())) {
                result = t;
                break;
            }
        }
        return result;
    }

    /**
     * 资源回收
     */
    public void release() {
        if (mTasks != null) {
            mTasks.clear();
            mTasks = null;
        }
        if (!isRunning) {
            isRunning = false;
        }
        if (mService != null && !mService.isShutdown()) {
            mService.shutdown();
        }
        sInstance = null;
    }

    private class TaskList implements java.io.Serializable {

        @SerializedName("systemtaskitems")
        private List<TaskInfo> systemTasks;
        @SerializedName("customtaskitems")
        private List<TaskInfo> customTasks;

        public List<TaskInfo> getSystemTasks() {
            return systemTasks;
        }

        public void setSystemTasks(List<TaskInfo> systemTasks) {
            this.systemTasks = systemTasks;
        }

        public List<TaskInfo> getCustomTasks() {
            return customTasks;
        }

        public void setCustomTasks(List<TaskInfo> customTasks) {
            this.customTasks = customTasks;
        }

        public void release() {
            if (systemTasks != null) {
                systemTasks.clear();
                systemTasks = null;
            }
            if (customTasks != null) {
                customTasks.clear();
                customTasks = null;
            }
        }
    }

    private class SortTask implements Comparator<TaskInfo> {
        @Override
        public int compare(TaskInfo lhs, TaskInfo rhs) {

            if (lhs.getTaskOrder() < rhs.getTaskOrder()) {
                return -1;
            } else if (lhs.getTaskOrder() > rhs.getTaskOrder()) {
                return 1;
            }
            return 0;
        }
    }
}
