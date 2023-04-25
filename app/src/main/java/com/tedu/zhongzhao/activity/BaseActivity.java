package com.tedu.zhongzhao.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

import com.aliyun.demo.recorder.AliyunVideoRecorder;
import com.aliyun.struct.common.CropKey;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.dialog.LoadingDialog;
import com.tedu.zhongzhao.event.AliyShortVideoEvent;
import com.tedu.zhongzhao.event.PhotoResultEvent;
import com.tedu.zhongzhao.ui.PageInfo;
import com.tedu.zhongzhao.ui.PageNotifiEvent;
import com.tedu.zhongzhao.widget.TitleBarView;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 基础Activity
 * Created by huangyx on 2018/3/5.
 */
public class BaseActivity extends AppCompatActivity {

    public static final String KEY_PAGE_INFO = "page_info";
    public static final String KEY_PAGE_ACT = "page_act";
    public static final String KEY_PARAMS = "page_params";
    public static final String KEY_CALLBACK_ID = "callback_id";

    public static final int REQ_CODE_CAMERA = 1001;
    public static final int REQ_CODE_PHOTO_ALBUM = 1002;
    public static final int REQ_CODE_CAPTURE_VIDEO = 1003;
    public static final int REQ_CODE_ALIY_SHORT_VIDEO = 1004;
    public static final int REQ_CODE_SCAN = 1005;

    protected int mExitAni = -1;
    protected TitleBarView mTitleBarView;
    private LoadingDialog mLoading;
    // web-native交互时用到的callback id
    protected String mCallbackId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarTool.setTranslucentStatus(this, getStatusColor(), getSpareColor());
        WorkApplication.addActivity(this);
        EventBus.getDefault().register(this);
        mCallbackId = getIntent().getStringExtra(KEY_CALLBACK_ID);
    }

    /**
     * 状态栏颜色
     *
     * @return
     */
    protected int getStatusColor() {
        return getResources().getColor(R.color.title_bg);
    }

    /**
     * 当状态栏设定的颜色为Color.TRANSPARENT时，需要使用该属性来设定状态栏中图标和文字颜色
     *
     * @return
     */
    protected int getSpareColor() {
        return -1;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mTitleBarView = (TitleBarView) findViewById(R.id.title_bar);
        if (mTitleBarView != null) {
            mTitleBarView.setLeftListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    onLeftBtnClicked();
                }
            });
        }
    }

    /**
     * 获取页面信息Key
     *
     * @return String
     */
    protected String getPageInfoKey() {
        return getIntent().getStringExtra(KEY_PAGE_INFO);
    }

    protected void onLeftBtnClicked() {
        onBackPressed();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mTitleBarView != null) {
            mTitleBarView.setTitle(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        if (mTitleBarView != null) {
            mTitleBarView.setTitle(titleId);
        }
    }

    public void showLoading() {
        if (mLoading == null) {
            mLoading = LoadingDialog.newInstance();
        }
        if (!mLoading.isShowing()) {
            mLoading.show(this);
        }
    }

    public void dismissLoading() {
        if (mLoading != null) {
            if (mLoading.isShowing()) {
                mLoading.dismiss();
            }
            mLoading = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PageNotifiEvent event) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        WorkApplication.removeActivity(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        if (mExitAni != -1) {
            overridePendingTransition(R.anim.anim_act_keep, mExitAni);
        }
    }

    final void setExitAni(String actId) {
        if (!TextUtils.isEmpty(actId)) {
            switch (actId) {
                case PageInfo.ACT_BACK_2:
                    mExitAni = R.anim.anim_act_bottom_out;
                    break;
                default:
                    mExitAni = R.anim.anim_act_right_out;
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_SCAN:
                    if (data != null) {
                        doQRResult(1, data.getStringExtra("callbackId"), data.getStringExtra(Constant.CODED_CONTENT));
                    }
                    break;
                case REQ_CODE_CAMERA: {
                    PhotoResultEvent event = new PhotoResultEvent(PhotoResultEvent.TYPE_PHOTO, PhotoResultEvent.RESULT_OK);
                    EventBus.getDefault().post(event);
//                        Intent intent = new Intent("com.android.camera.action.CROP");
//                        intent.setDataAndType(imageUri, "image/*");
//                        intent.putExtra("scale", true);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                        // 启动裁剪
//                        startActivityForResult(intent, SHOW_PHOTO);
                    break;
                }
                case REQ_CODE_CAPTURE_VIDEO: {
                    PhotoResultEvent event = new PhotoResultEvent(PhotoResultEvent.TYPE_VIDEO, PhotoResultEvent.RESULT_OK);
                    EventBus.getDefault().post(event);
                    break;
                }
//                    case SHOW_PHOTO:
//                        try {
//                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                            // 将裁剪后的照片显示出来
//                            iv_photo.setImageBitmap(bitmap);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        break;
                case REQ_CODE_PHOTO_ALBUM: {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    String picturePath = null;
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                    }
                    PhotoResultEvent event = new PhotoResultEvent(PhotoResultEvent.TYPE_ALBUM, PhotoResultEvent.RESULT_OK);
                    event.path = picturePath;
                    EventBus.getDefault().post(event);
//                        Uri uri = data.getData();
//                        Intent intent = new Intent("com.android.camera.action.CROP");
//                        intent.setDataAndType(uri, "image/*");
//                        intent.putExtra("scale", true);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                        // 启动裁剪
//                        startActivityForResult(intent, SHOW_PHOTO_ALBUM);
                    break;
                }
                case REQ_CODE_ALIY_SHORT_VIDEO: {
                    if (data != null) {
                        int type = data.getIntExtra(AliyunVideoRecorder.RESULT_TYPE, 0);
                        if (type == AliyunVideoRecorder.RESULT_TYPE_RECORD) {
                            String path = data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH);
                            AliyShortVideoEvent event = new AliyShortVideoEvent(AliyShortVideoEvent.RESULT_OK, path);
                            EventBus.getDefault().post(event);
                        } else if (type == AliyunVideoRecorder.RESULT_TYPE_CROP) {
                            String path = data.getStringExtra(CropKey.RESULT_KEY_CROP_PATH);
                            AliyShortVideoEvent event = new AliyShortVideoEvent(AliyShortVideoEvent.RESULT_OK, path);
                            EventBus.getDefault().post(event);
                        }
                    }
                    break;
                }
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        } else {
            switch (requestCode) {
                case REQ_CODE_SCAN:
                    if (data != null) {
                        doQRResult(0, data.getStringExtra("callbackId"), null);
                    }
                    break;
                case REQ_CODE_CAMERA: {
                    PhotoResultEvent event = new PhotoResultEvent(PhotoResultEvent.TYPE_PHOTO, PhotoResultEvent.RESULT_CANCEL);
                    EventBus.getDefault().post(event);
                    break;
                }
                case REQ_CODE_PHOTO_ALBUM: {
                    PhotoResultEvent event = new PhotoResultEvent(PhotoResultEvent.TYPE_ALBUM, PhotoResultEvent.RESULT_CANCEL);
                    EventBus.getDefault().post(event);
                    break;
                }
                case REQ_CODE_CAPTURE_VIDEO: {
                    PhotoResultEvent event = new PhotoResultEvent(PhotoResultEvent.TYPE_VIDEO, PhotoResultEvent.RESULT_CANCEL);
                    EventBus.getDefault().post(event);
                    break;
                }
                case REQ_CODE_ALIY_SHORT_VIDEO:
                    AliyShortVideoEvent event = new AliyShortVideoEvent(AliyShortVideoEvent.RESULT_CANCEL, null);
                    EventBus.getDefault().post(event);
                    break;
            }
        }

    }

    /**
     * 横竖屏切换监听
     */
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    /**
     * 扫码回调处理
     *
     * @param result     0取消，1成功
     * @param callbackId 回调Id
     * @param content    扫码结果内容
     */

    protected void doQRResult(int result, String callbackId, String content) {

    }

    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private View mCustomView;

    protected void showWebCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        FrameLayout rootView = (FrameLayout) getWindow().getDecorView();
        if (mCustomView != null && mCustomView.getParent() != null) {
            rootView.removeView(mCustomView);
            mCustomView = null;
        }
        if (view != null && callback != null) {
            mCustomView = view;
            mCustomViewCallback = callback;
            rootView.addView(mCustomView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            //设置横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    protected boolean hideWebCustomView() {
        if (mCustomView == null || mCustomView.getParent() == null) {
            return false;
        }
        ((FrameLayout) getWindow().getDecorView()).removeView(mCustomView);
        mCustomViewCallback.onCustomViewHidden();
        mCustomViewCallback = null;
        mCustomView = null;
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return true;
    }

    @Override
    final public void onBackPressed() {
        if (!hideWebCustomView()) {
            if (!doBackPressed()) {
                super.onBackPressed();
            }
        }
    }

    protected boolean doBackPressed() {
        return false;
    }
}
