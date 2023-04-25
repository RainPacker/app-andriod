package com.tedu.zhongzhao.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.tedu.base.permission.PermissionDialog;
import com.tedu.base.permission.PermissionUtil;
import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.activity.BaseActivity;
import com.tedu.zhongzhao.dialog.SelectPicDialog;
import com.tedu.zhongzhao.event.PhotoResultEvent;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.zhongzhao.web.ActionCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by huangyx on 2018/4/21.
 */
public class DNPhotoAndCameraService extends BaseService {

    private static DNPhotoAndCameraService sInstance;

    synchronized public static DNPhotoAndCameraService getInstance() {
        if (sInstance == null) {
            sInstance = new DNPhotoAndCameraService();
        }
        return sInstance;
    }

    private ActionCallback callback;
    // 结果地址（录像）
    private String resultPath;

    public DNPhotoAndCameraService() {

    }

    /**
     * 拍照、录像等
     *
     * @param context Context
     * @param params  参数
     */
    public void doTakePhone_andView_andContainer_(final ActionCallback callback, final Context context, Map<String, String> params) {
        this.callback = callback;
        if (params != null) {
            if (params.containsKey("cameraconfig")) {
                String text = params.get("cameraconfig");
                if (!TextUtils.isEmpty(text)) {
                    final CameraConfig config = JsonUtil.fromJson(text, CameraConfig.class);
                    if (config != null) {
                        if (config.getMediatype() == null || config.getMediatype().length == 0) {
                            // 进入相册
                            FunctionConfig galleryConfig = new FunctionConfig.Builder()
                                    .setEnableEdit(true).setEnableCrop(true).setCropSquare(true).setForceCrop(true).build();
                            GalleryFinal.openGallerySingle(1001, galleryConfig, new GalleryFinal.OnHanlderResultCallback() {
                                @Override
                                public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                                    if (resultList != null && resultList.size() > 0) {
                                        sendSuccess(callback, resultList.get(0).getPhotoPath());
                                    } else {
                                        sendSuccess(callback, "false");
                                    }
                                }

                                @Override
                                public void onHanlderFailure(int requestCode, String errorMsg) {
                                    sendSuccess(callback, "false");
                                }
                            });
                        } else {
                            if (config.getMediatype().length == 1) {
                                if ("1".endsWith(config.getMediatype()[0])) {
                                    openTakeVideo(callback, context, config);
                                } else if ("2".equals(config.getMediatype()[0])) {
                                    openCarma(callback, context);
                                }

                            } else if (config.getMediatype().length == 2) {
                                SelectPicDialog.newInstance(0, new SelectPicDialog.SelectPicDialogListener() {
                                    @Override
                                    public void toTakePhoto(int reqCode) {
                                        openCarma(callback, context);
                                    }

                                    @Override
                                    public void toTakeVideo(int reqCode) {
                                        openTakeVideo(callback, context, config);
                                    }
                                }).show((Activity) context);
                            }
                        }
                        return;
                    }
                }
            }
        }
        sendSuccess(callback, "false");
    }

    /**
     * 进入录像
     *
     * @param callback ActionCallback
     * @param context  Context
     */
    private void openTakeVideo(ActionCallback callback, Context context, CameraConfig config) {
        if (!PermissionUtil.isPermissionGranted(context, Manifest.permission.CAMERA)) {
            PermissionDialog dialog = PermissionDialog.newInstance("相机", "录像");
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
            dialog.show((Activity) context);
        } else {
            EventBus.getDefault().register(this);
            // 录像
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            resultPath = WorkApplication.getImageDir() + System.currentTimeMillis() + ".mp4";
            File file = new File(resultPath);
            Uri fileUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
            int quality = config.getQuality();
            if (quality > 1) {
                quality = 1;
            } else if (quality < 0) {
                quality = 0;
            }
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality); // set the video image quality to high
            // start the Video Capture Intent
            ((Activity) context).startActivityForResult(intent, BaseActivity.REQ_CODE_CAPTURE_VIDEO);
        }
    }

    /**
     * 打开进行拍照
     *
     * @param callback ActionCallback
     */
    private void openCarma(final ActionCallback callback, Context context) {
        // 兼容部分手机、主动弹出申请相机权限
        if (!PermissionUtil.isPermissionGranted(context, Manifest.permission.CAMERA)) {
            try {
                Camera camera = Camera.open();
                camera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!PermissionUtil.isPermissionGranted(context, Manifest.permission.CAMERA)) {
            PermissionDialog dialog = PermissionDialog.newInstance("相机", "拍照");
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
            dialog.show((Activity) context);
        } else {
            FunctionConfig config = new FunctionConfig.Builder()
                    .setEnableEdit(true).setEnableCrop(true).setCropSquare(true).setForceCrop(true).build();
            GalleryFinal.openCamera(20001, config, new GalleryFinal.OnHanlderResultCallback() {
                @Override
                public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                    if (resultList != null && resultList.size() > 0) {
                        sendSuccess(callback, resultList.get(0).getPhotoPath());
                    } else {
                        sendFail(callback, "拍照失败", "");
                    }
                }

                @Override
                public void onHanlderFailure(int requestCode, String errorMsg) {
                    sendFail(callback, "拍照失败", errorMsg);
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThreadEvent(PhotoResultEvent event) {
        EventBus.getDefault().unregister(this);
        if (event.result == PhotoResultEvent.RESULT_CANCEL) {
            sendCancel(callback);
        } else if (event.result == PhotoResultEvent.RESULT_OK) {
            if (event.type == PhotoResultEvent.TYPE_PHOTO) {
                if (resultPath != null) {
                    File f = new File(resultPath);
                    if (f.exists()) {
                        sendSuccess(callback, resultPath);
                        return;
                    }
                }
                sendSuccess(callback, "false");
            } else if (event.type == PhotoResultEvent.TYPE_ALBUM) {
                if (!TextUtils.isEmpty(event.path)) {
                    sendSuccess(callback, event.path);
                } else {
                    sendSuccess(callback, "false");
                }
            } else if (event.type == PhotoResultEvent.TYPE_VIDEO) {
                if (resultPath != null) {
                    File f = new File(resultPath);
                    if (f.exists()) {
                        sendSuccess(callback, resultPath);
                        return;
                    }
                }
                sendSuccess(callback, "false");
            }
        }
    }

    /**
     * 照片多选
     *
     * @param context Context
     * @param params  参数
     */
    public void doTakeMultiPhotoWithCount_view_andContainer_(final ActionCallback callback, final Context context, Map<String, String> params) {
        this.callback = callback;
        int count = 1;
        if (params.containsKey("count")) {
            try {
                count = Integer.parseInt(params.get("count"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 进入相册
        FunctionConfig config = new FunctionConfig.Builder().setEnableEdit(false).setEnableCrop(false).setMutiSelectMaxSize(count).build();
        GalleryFinal.openGalleryMuti(1001, config, new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                if (resultList != null && resultList.size() > 0) {
                    List<String> photos = new ArrayList<String>();
                    for (PhotoInfo p : resultList) {
                        photos.add(p.getPhotoPath());
                    }
                    sendSuccess(callback, JsonUtil.toJson(photos));
                } else {
                    sendSuccess(callback, "false");
                }
            }

            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {
                sendSuccess(callback, "false");
            }
        });
    }


    private class CameraConfig implements java.io.Serializable {
        private String source;
        private String[] mediatype;
        private String capturemode;
        private int quality;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String[] getMediatype() {
            return mediatype;
        }

        public void setMediatype(String[] mediatype) {
            this.mediatype = mediatype;
        }

        public String getCapturemode() {
            return capturemode;
        }

        public void setCapturemode(String capturemode) {
            this.capturemode = capturemode;
        }

        public int getQuality() {
            return quality;
        }

        public void setQuality(int quality) {
            this.quality = quality;
        }
    }
}
