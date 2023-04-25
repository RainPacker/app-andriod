package com.tedu.zhongzhao.tracking;

import android.content.Context;

import com.tedu.zhongzhao.net.NetReqUtil;
import com.tedu.zhongzhao.net.RequestData;
import com.tedu.zhongzhao.net.RequestFactory;
import com.tedu.zhongzhao.net.RequestInfo;
import com.tedu.zhongzhao.utils.DateUtil;

import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 埋点事件管理
 * Created by huangyx on 2018/4/2.
 */
public class TrackingManager {

    private static final int LIMIT = 10;
    private static final int TYPE_POST_SERVER = 1;
    private static final int TYPE_POST_SERVER_NOW = 2;

    private static TrackingManager sInstance;

    synchronized public static TrackingManager getInstance() {
        if (sInstance == null) {
            sInstance = new TrackingManager();
        }
        return sInstance;
    }

    private ExecutorService mService;
    private BlockingQueue<RequestData<TrackData>> mQueue;
    private boolean isInited;
    private boolean isRunning;

    private TrackingManager() {
        isInited = false;
    }

    public void init(Context context) {
        if (isInited) {
            return;
        }
        mQueue = new ArrayBlockingQueue<RequestData<TrackData>>(10);
        mService = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "tracking-log-service#save");
                thread.setPriority(Thread.NORM_PRIORITY - 1);
                return thread;
            }
        });
        isRunning = true;
        mService.execute(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    RequestData<TrackData> item = null;
                    try {
                        item = mQueue.take();
                        doAction(item);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (item != null) {
                            mQueue.remove(item);
                        }
                    }
                }
            }
        });

        isInited = true;
    }

    private void doAction(RequestData<TrackData> action) {
        if (action != null) {
            RequestInfo info = RequestFactory.getInstance().getRequestInfo("r004");
            if (info != null) {
                RequestFactory.getInstance().request(info, action, null, null);
            }
        }
    }


    /**
     * 埋点上报
     *
     * @param info 埋点信息
     */
    private void addTracking(RequestData<TrackData> info) {
        if (info != null) {
            mQueue.offer(info);
        }
    }


    /**
     * 埋点上报
     *
     * @param wid         界面ID
     * @param pid         界面父ID
     * @param interact_id 交互ID
     * @param page_id     页面ID
     * @param act_id      页面操作ID，服务端定义
     * @param param       调用参数
     */
    public static void tracking(String wid, String pid, String interact_id, String page_id,
                                String act_id, String param) {
        String time = DateUtil.toDefaultDate(Calendar.getInstance().getTime());
        tracking(wid, pid, interact_id, page_id, act_id, time, time, param);
    }


    /**
     * 埋点上报
     *
     * @param wid         界面ID
     * @param pid         界面父ID
     * @param interact_id 交互ID
     * @param page_id     页面ID
     * @param act_id      页面操作ID，服务端定义
     * @param beigintime  触发时间 yyyy-MM-dd HH:mm:ss
     * @param endtime     结束时间  yyyy-MM-dd HH:mm:ss
     * @param param       调用参数
     */
    public static void tracking(String wid, String pid, String interact_id, String page_id,
                                String act_id, String beigintime, String endtime, String param) {

        TrackData data = new TrackData();
        data.setInteract_id(interact_id);
        data.setPage_id(page_id);
        data.setAct_id(act_id);
        data.setBeigintime(beigintime);
        data.setEndtime(endtime);
        data.setParam(param);

        TrackingManager.getInstance().addTracking(NetReqUtil.createRequestData(wid, pid, data));
    }
}
