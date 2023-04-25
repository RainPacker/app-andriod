package com.tedu.zhongzhao.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.aliyun.demo.recorder.AliyunVideoRecorder;
import com.aliyun.struct.common.VideoQuality;
import com.aliyun.struct.recorder.CameraType;
import com.aliyun.struct.recorder.FlashType;
import com.aliyun.struct.snap.AliyunSnapVideoParam;
import com.tedu.base.permission.PermissionDialog;
import com.tedu.base.permission.PermissionUtil;
import com.tedu.zhongzhao.activity.BaseActivity;
import com.tedu.zhongzhao.event.AliyShortVideoEvent;
import com.tedu.zhongzhao.web.ActionCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

/**
 * 阿里短视频Service
 * Created by huangyx on 2018/8/20.
 */
public class DNAliyShortVideoService extends BaseService {

    private static DNAliyShortVideoService sInstance;

    synchronized public static DNAliyShortVideoService getInstance() {
        if (sInstance == null) {
            sInstance = new DNAliyShortVideoService();
        }
        return sInstance;
    }

    private DNAliyShortVideoService() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    private ActionCallback mCallback;

    /**
     * 调用录制短视频
     */
    public void toRecordVideoWithQuality_videoWidth_videoRatio_maxTimeLength_minTimeLength_keyFrame_view_controller_(ActionCallback callback, Context context, Map<String, String> params) {
        this.mCallback = callback;
        int resolution = AliyunSnapVideoParam.RESOLUTION_480P;
        int ratio = AliyunSnapVideoParam.RECORD_MODE_AUTO;
        int maxDuration = 30;
        int minDuration = 3;
        VideoQuality quality = VideoQuality.SD;
        int gop = 5;
        if (params != null) {
            if (params.containsKey("videoWidth")) {
                String width = params.get("videoWidth");
                if ("360".equals(width)) {
                    resolution = AliyunSnapVideoParam.RESOLUTION_360P;
                } else if ("480".equals(width)) {
                    resolution = AliyunSnapVideoParam.RESOLUTION_480P;
                } else if ("540".equals(width)) {
                    resolution = AliyunSnapVideoParam.RESOLUTION_540P;
                } else if ("720".equals(width)) {
                    resolution = AliyunSnapVideoParam.RESOLUTION_720P;
                }
            }
            if (params.containsKey("videoRatio")) {
                try {
                    float f = Float.parseFloat(params.get("videoRatio"));
                    if (f >= 1.3 && f < 1.4) {
                        ratio = AliyunSnapVideoParam.RATIO_MODE_3_4;
                    } else if (f >= 1.7 && f <= 2) {
                        ratio = AliyunSnapVideoParam.RATIO_MODE_9_16;
                    } else if (f == 1) {
                        ratio = AliyunSnapVideoParam.RATIO_MODE_1_1;
                    } else {
                        ratio = AliyunSnapVideoParam.RECORD_MODE_AUTO;
                    }
                } catch (Exception e) {

                }
            }
            if (params.containsKey("max_time_length")) {
                String len = params.get("max_time_length");
                try {
                    maxDuration = Integer.parseInt(len);
                } catch (Exception e) {

                }
            }
            if (params.containsKey("min_time_length")) {
                String len = params.get("min_time_length");
                try {
                    minDuration = Integer.parseInt(len);
                } catch (Exception e) {

                }
            }
            if (params.containsKey("f")) {
                String f = params.get("f");
                if (!TextUtils.isEmpty(f)) {
                    switch (f) {
                        case "0":
                            quality = VideoQuality.SSD;
                            break;
                        case "1":
                            quality = VideoQuality.HD;
                            break;
                        case "2":
                            quality = VideoQuality.SD;
                            break;
                        case "3":
                            quality = VideoQuality.LD;
                            break;
                        case "4":
                            quality = VideoQuality.PD;
                            break;
                        case "5":
                            quality = VideoQuality.EPD;
                            break;
                    }
                }
            }
            if (params.containsKey("key_frame")) {
                try {
                    gop = Integer.parseInt(params.get("key_frame"));
                } catch (Exception e) {

                }
            }
        }
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };
        int[] permissionResult = PermissionUtil.isPermissionGranted(context, permissions);
        if (permissionResult == null || permissionResult.length != permissions.length) {
            PermissionDialog dialog = PermissionDialog.newInstance("相机、录音", "录像");
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
            dialog.show((Activity) context);
        } else {
            AliyunSnapVideoParam recordParam = new AliyunSnapVideoParam.Builder()
                    //设置录制分辨率，目前支持360p，480p，540p，720p
                    .setResulutionMode(resolution)
                    //设置视频比例，目前支持1:1,3:4,9:16
                    .setRatioMode(ratio)
                    .setRecordMode(AliyunSnapVideoParam.RECORD_MODE_PRESS) //设置录制模式，目前支持按录，点录和混合模式
//                .setFilterList(eff_dirs) //设置滤镜地址列表,具体滤镜接口接收的是一个滤镜数组
//                .setBeautyLevel(80) //设置美颜度
                    .setBeautyStatus(true) //设置美颜开关
                    .setCameraType(CameraType.BACK) //设置前后置摄像头
                    .setFlashType(FlashType.AUTO) // 设置闪光灯模式
                    .setNeedClip(false) //设置是否需要支持片段录制
                    .setMaxDuration(maxDuration * 1000) //设置最大录制时长 单位毫秒
                    .setMinDuration(minDuration * 1000) //设置最小录制时长 单位毫秒
                    .setVideQuality(quality) //设置视频质量
                    .setGop(gop) //设置关键帧间隔
                    .setSortMode(AliyunSnapVideoParam.SORT_MODE_VIDEO)//设置导入相册过滤选择视频
                    .build();
            AliyunVideoRecorder.startRecordForResult((Activity) context, BaseActivity.REQ_CODE_ALIY_SHORT_VIDEO, recordParam);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThreadEvent(AliyShortVideoEvent event) {
        if (event != null && mCallback != null) {
            if (event.getResult() == AliyShortVideoEvent.RESULT_OK) {
                sendSuccess(mCallback, event.getPath());
            } else if (event.getResult() == AliyShortVideoEvent.RESULT_CANCEL) {
                sendCancel(mCallback);
            } else {
                sendFail(mCallback, "false", "");
            }
        }
    }
}
