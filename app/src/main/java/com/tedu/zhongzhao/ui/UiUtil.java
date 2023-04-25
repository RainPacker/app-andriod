package com.tedu.zhongzhao.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.tedu.base.util.LogUtil;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.activity.BaseActivity;
import com.tedu.zhongzhao.activity.MapActivity;
import com.tedu.zhongzhao.activity.PlayVideoActivity;
import com.tedu.zhongzhao.activity.TabActivity;
import com.tedu.zhongzhao.activity.WebActivity;
import com.tedu.zhongzhao.service.ServiceFactory;
import com.tedu.zhongzhao.utils.ImageUtil;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.zhongzhao.utils.TempIntentData;
import com.tedu.zhongzhao.widget.TitleBarView;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 页面工具
 * Created by huangyx on 2018/3/12.
 */
public class UiUtil {

    private static final String TAG = UiUtil.class.getSimpleName();
    /**
     * 内置图片路径
     */
    private static final String INNER_IMAGE_PATH = "images/";

    /**
     * 应用入口数据
     */
    private static EntranceInfo sEntranceInfo;

    /**
     * 设置应用入口数据
     *
     * @param info EntranceInfo
     */
    public static void setEntranceInfo(EntranceInfo info) {
        sEntranceInfo = info;
    }

    /**
     * 获取drawable
     *
     * @param style NavStyleInfo
     * @return Drawable
     */
    public static Drawable getDrawable(NavStyleInfo style) {
        Drawable d = null;
        if (style != null) {
            if (!TextUtils.isEmpty(style.getBgColor()) && style.getBgColor().startsWith("#")) {
                d = new ColorDrawable(Color.parseColor(style.getBgColor()));
            } else if (!TextUtils.isEmpty(style.getBgImg())) {
                d = ImageUtil.bitmap2Drawable(ImageUtil.getAssetImage(INNER_IMAGE_PATH + style.getBgImg()));
            }
        }
        return d;
    }

    /**
     * 获取drawable
     *
     * @param style IconStyleInfo
     * @return Drawable
     */
    public static Drawable getDrawable(IconStyleInfo style) {
        Drawable d = null;
        if (style != null) {
            if (!TextUtils.isEmpty(style.getBgColor()) && style.getBgColor().startsWith("#")) {
                d = new ColorDrawable(Color.parseColor(style.getBgColor()));
            } else if (!TextUtils.isEmpty(style.getBgImg())) {
                d = ImageUtil.bitmap2Drawable(ImageUtil.getAssetImage(INNER_IMAGE_PATH + style.getBgImg()));
            }
        }
        return d;
    }

    /**
     * 获取selector形式的Drawable
     *
     * @param style IconStyleInfo
     * @return StateListDrawable
     */
    public static StateListDrawable getStateListDrawable(IconStyleInfo style) {
        StateListDrawable listDrawable = new StateListDrawable();
        Drawable nor = null, selected = null;
        if (!TextUtils.isEmpty(style.getnIcon())) {
            nor = ImageUtil.bitmap2Drawable(ImageUtil.getAssetImage(INNER_IMAGE_PATH + style.getnIcon()));
        }
        if (!TextUtils.isEmpty(style.getHlcon())) {
            selected = ImageUtil.bitmap2Drawable(ImageUtil.getAssetImage(INNER_IMAGE_PATH + style.getHlcon()));
        }
        listDrawable.addState(new int[]{android.R.attr.state_selected}, selected);
        listDrawable.addState(new int[]{}, nor);
        return listDrawable;
    }

    /**
     * 获取selector形式的Drawable
     *
     * @param style IconStyleInfo
     * @return StateListDrawable
     */
    public static StateListDrawable getStateListDrawable(IconStyleInfo style, int width, int height) {
        StateListDrawable listDrawable = new StateListDrawable();
        Drawable nor = null, selected = null;
        if (!TextUtils.isEmpty(style.getnIcon())) {
            nor = ImageUtil.bitmap2Drawable(ImageUtil.getAssetImage(INNER_IMAGE_PATH + style.getnIcon(), width, height));
        }
        if (!TextUtils.isEmpty(style.getHlcon())) {
            selected = ImageUtil.bitmap2Drawable(ImageUtil.getAssetImage(INNER_IMAGE_PATH + style.getHlcon(), width, height));
        }
        listDrawable.addState(new int[]{android.R.attr.state_selected}, selected);
        listDrawable.addState(new int[]{android.R.attr.state_pressed}, selected);
        listDrawable.addState(new int[]{}, nor);
        return listDrawable;
    }

    /**
     * 获取drawable
     *
     * @param name 图片名
     * @return Drawable
     */
    public static Drawable getDrawable(String name) {
        Drawable d = null;
        if (!TextUtils.isEmpty(name)) {
            d = ImageUtil.bitmap2Drawable(ImageUtil.getAssetImage(INNER_IMAGE_PATH + name));
        }
        return d;
    }

    /**
     * 获取Url
     *
     * @param page 页面信息
     * @return String
     */
    public static String getWebUrl(PageInfo page) {
        return getWebUrl(page, null);
    }

    /**
     * 获取Url
     *
     * @param page   页面信息
     * @param params 额外参数
     * @return String
     */
    public static String getWebUrl(PageInfo page, HashMap<String, String> params) {
        StringBuffer url = new StringBuffer();
        if (page != null && page.getView() != null) {
            if (page.getView().isRemote()) {
                url.append(page.getView().getUrl());
                //TODO 暂未加param
            } else {
                if (!TextUtils.isEmpty(page.getView().getLocalUrl())) {
                    url.append("file:///android_asset/html/").append(page.getView().getLocalUrl());
                }
            }
            if (params != null && !params.isEmpty()) {
                if (url.indexOf("?") == -1) {
                    url.append("?");
                } else {
                    url.append("&");
                }
                Set<String> keys = params.keySet();
                for (String k : keys) {
                    url.append(k).append("=").append(params.get(k)).append("&");
                }
            }
        }
        String urlstr = url.toString();
        LogUtil.i(TAG, "url:" + urlstr);
        return urlstr;
    }

    /**
     * 进入主页
     *
     * @param context Context
     */
    public static void startMain(Context context) {
        EntranceInfo info = sEntranceInfo;
        if (info == null) {
            LogUtil.e(TAG, "main page info is null");
        } else {
//            jump2TabActivity(context, info.getItems());
            List<PageInfo> pages = info.getItems();
            if (pages != null && !pages.isEmpty()) {
                PageInfo page = pages.get(0);
                if (!page.isShowTab()) {
                    jump2WebActivity(context, page);
                } else {
                    jump2TabActivity(context, info.getItems());
                }
            }

            /*if (info.getAppinfo() == null || UiPageConstans.TEMPLATE_TAB.equalsIgnoreCase(info.getAppinfo().getTemplate())) {
                // 没有appinfo信息，或是tab类型，跳转到tabActivity
                jump2TabActivity(context, info.getItems());
            } else if (UiPageConstans.TEMPLATE_DRAWER.equalsIgnoreCase(info.getAppinfo().getTemplate())) {
                jump2DrawerActivity(context, info.getItems());
            } else {
                PageInfo page = null;
                if (info.getItems() != null && !info.getItems().isEmpty()) {
                    page = info.getItems().get(0);
                }
                doActive(context, page);
            }*/

        }
    }

    /**
     * 跳转Activity
     *
     * @param activity Context
     * @param pageInfo pageInfo
     */
    public static void doActive(BaseActivity activity, PageInfo pageInfo) {
        doActive(activity, pageInfo, null, null, null);
    }

    /**
     * 跳转Activity
     *
     * @param activity Context
     * @param pageInfo pageInfo
     * @param act      自定义动作
     * @param params   额外参数
     */
    public static void doActive(BaseActivity activity, PageInfo pageInfo, String act, HashMap<String, String> params, String callbackId) {
        if (pageInfo != null && pageInfo.getView() != null) {
            if (TextUtils.isEmpty(act)) {
                act = pageInfo.getAct();
            }
            if (PageInfo.ACT_OPEN_APP.equals(act)) {
                // 打开指定app
                jumpOut(activity, pageInfo.getView().getUrl());
            } else if (PageInfo.ACT_SERVICE.equals(act)) {
                // 打开内置服务
                if (!TextUtils.isEmpty(pageInfo.getSid())) {
                    // TODO 此处应用回调，如何处理?
                    ServiceFactory.getInstance().doService(activity, pageInfo.getSid(), null, params);
                }
            } else if (PageInfo.ACT_BACK.equals(act) || PageInfo.ACT_BACK_2.equals(act)) {
                activity.finish();
            } else {
                int enterAni = R.anim.anim_act_right_in;
                if (!TextUtils.isEmpty(act)) {
                    switch (act) {
                        case PageInfo.ACT_PRESENT:
                            enterAni = R.anim.anim_act_bottom_in;
                            break;
                        case PageInfo.ACT_PUSH:
                        default:
                            enterAni = R.anim.anim_act_right_in;
                            break;
                    }
                }
                /*if (UiPageConstans.TEMPLATE_TAB.equalsIgnoreCase(pageInfo.getTemplates())) {
                    jump2TabActivity(activity, pageInfo.getItems());
                } else if (UiPageConstans.TEMPLATE_DRAWER.equalsIgnoreCase(pageInfo.getTemplates())) {
                    jump2DrawerActivity(activity, pageInfo.getItems());
                } else {
                    jump2Activity(activity, pageInfo);
                }*/
                jump2Activity(activity, pageInfo, act, params, callbackId);
                activity.overridePendingTransition(enterAni, R.anim.anim_act_keep);
            }
        }
    }

    /**
     * 通过path id进行跳转
     *
     * @param activity   BaseActivity
     * @param pathId     path id
     * @param act        自定义动作
     * @param params     额外参数
     * @param callbackId web-native交互callback id
     */
    public static void doActiveFromWeb(BaseActivity activity, String pathId, String act, HashMap<String, String> params, String callbackId) {
        PageInfo page = getPageInfo(pathId);
        if (page != null) {
            doActive(activity, page, act, params, callbackId);
        }
    }

    /**
     * 通过Path id 获取 页面信息
     *
     * @param pathId path id
     * @return PageInfo
     */
    public static PageInfo getPageInfo(String pathId) {
        PageInfo page = null;
        if (!TextUtils.isEmpty(pathId) && sEntranceInfo != null) {
            page = getPageInfo(pathId, sEntranceInfo.getItems());
        }
        return page;
    }

    private static PageInfo getPageInfo(String pathId, List<PageInfo> pages) {
        PageInfo page = null;
        if (!TextUtils.isEmpty(pathId) && pages != null && !pages.isEmpty()) {
            for (PageInfo p : pages) {
                if (pathId.equals(p.getPathId())) {
                    page = p;
                    break;
                } else {
                    page = getPageInfo(pathId, p.getItems());
                    if (page == null) {
                        if (p.getNavinfo() != null) {
                            if (p.getNavinfo().getLeft() != null) {
                                page = getPageInfo(pathId, p.getNavinfo().getLeft().getItems());
                            }
                            if (page == null) {
                                if (p.getNavinfo().getRight() != null) {
                                    page = getPageInfo(pathId, p.getNavinfo().getRight().getItems());
                                }
                            }
                        }
                    }
                    if (page != null) {
                        break;
                    }
                }
            }
        }
        return page;
    }

    /**
     * 跳转到TabActivity
     *
     * @param context Context
     * @param pages   子页面列表
     */
    private static void jump2TabActivity(Context context, List<PageInfo> pages) {
        String key = "key:" + System.currentTimeMillis();
        TempIntentData.putData(key, pages);
        Intent intent = new Intent(context, TabActivity.class);
        intent.putExtra(BaseActivity.KEY_PAGE_INFO, key);
        context.startActivity(intent);
    }

    /**
     * 跳转到DrawerActivity
     *
     * @param context Context
     * @param pages   子页面列表
     */
    private static void jump2DrawerActivity(Context context, List<PageInfo> pages) {
        LogUtil.e(TAG, "can not found DrawerActivity!!!");
    }

    /**
     * 跳转到WebActivity
     *
     * @param context Context
     * @param page    页面信息
     * @param params  额外参数
     */
    private static void jump2Activity(Context context, PageInfo page, String act, HashMap<String, String> params, String callbackId) {
        if (page != null) {
            if (UiPageConstans.VIEW_WEB.equalsIgnoreCase(page.getControl())
                    || UiPageConstans.VIEW_WEB_2.equalsIgnoreCase(page.getControl())) {
                // 普通web activity
                jump2WebActivity(context, page, act, params);
            } else if (UiPageConstans.VIEW_QR.equalsIgnoreCase(page.getControl())) {
                // 扫码activity
                jump2QRActivity(context, page, callbackId);
            } else if (UiPageConstans.VIEW_CAMERA.equals(page.getControl())) {
                // TODO 进入相机， 目前没有业务逻辑支持
                /*File outputImage = new File(WorkApplication.getImageDir(), System.currentTimeMillis() + ".jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //隐式意图启动相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));
                // 启动相机程序
                ((Activity) context).startActivityForResult(intent, BaseActivity.REQ_CODE_CAMERA);*/
            } else if (UiPageConstans.VIEW_PHOTO.equals(page.getControl())) {
                // TODO 进入相册， 目前没有业务逻辑支持
                /*File outputImage = new File(WorkApplication.getImageDir(), "temp.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image*//*");
                //允许裁剪
                intent.putExtra("crop", true);
                //允许缩放
                intent.putExtra("scale", true);
                //图片的输出位置
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));
                ((Activity) context).startActivityForResult(intent, BaseActivity.REQ_CODE_PHOTO_ALBUM);*/
            } else if (isMapControl(page.getControl())) {
                String key = "key:" + System.currentTimeMillis();
                TempIntentData.putData(key, page);
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra(BaseActivity.KEY_PAGE_INFO, key);
                if (UiPageConstans.VIEW_MAP_TYPE.equals(page.getControl())
                        || UiPageConstans.VIEW_MAP_POI.equals(page.getControl())) {
                    if (params != null && !params.isEmpty()) {
                        Set<String> keys = params.keySet();
                        for (String k : keys) {
                            intent.putExtra(k, params.get(k));
                        }
                    }
                }
                context.startActivity(intent);
            } else if (UiPageConstans.VIEW_VIDEO_PLAY.equals(page.getControl())) {
                String key = "key:" + System.currentTimeMillis();
                TempIntentData.putData(key, page);
                Intent intent = new Intent(context, PlayVideoActivity.class);
                intent.putExtra(BaseActivity.KEY_PAGE_INFO, key);
                if (params != null) {
                    intent.putExtra("params", JsonUtil.toJson(params));
                }
                context.startActivity(intent);
            } else {
                LogUtil.e(TAG, "the view type " + page.getView().getViewType() + " is not supported!!!");
            }
        }
    }

    /**
     * 判断是不是要显示地图
     *
     * @param control View Control
     * @return true/false
     */
    private static boolean isMapControl(String control) {
        return UiPageConstans.VIEW_MAP.equals(control)
                || UiPageConstans.VIEW_MAP_LOCATION.equals(control)
                || UiPageConstans.VIEW_MAP_POI.equals(control)
                || UiPageConstans.VIEW_MAP_TYPE.equals(control)
                || UiPageConstans.VIEW_MAP_ROUTE.equals(control)
                || UiPageConstans.VIEW_MAP_INNER.equals(control);
    }

    /**
     * 跳转入扫码
     *
     * @param context Context
     * @param page    页面信息
     */
    private static void jump2QRActivity(Context context, PageInfo page) {
        jump2QRActivity(context, page, null);
    }


    /**
     * 跳转入扫码
     *
     * @param context    Context
     * @param page       页面信息
     * @param callbackId web-native交互callback id
     */
    private static void jump2QRActivity(Context context, PageInfo page, String callbackId) {
        String key = "key:" + System.currentTimeMillis();
        TempIntentData.putData(key, page);
        List<String> types = new ArrayList<String>();
        Collection<String> cs = IntentIntegrator.ONE_D_CODE_TYPES;
        if (cs != null && !cs.isEmpty()) {
            types.addAll(cs);
        }
        cs = IntentIntegrator.QR_CODE_TYPES;
        if (cs != null && !cs.isEmpty()) {
            types.addAll(cs);
        }
//        new IntentIntegrator((Activity) context)
//                .setCaptureActivity(QRActivity.class)
//                .setDesiredBarcodeFormats(types)// 扫码的类型,可选：一维码，二维码，一/二维码
//                .setPrompt("请对准二维码/条码")// 设置提示语
//                .setCameraId(0)// 选择摄像头,可使用前置或者后置
//                .setBeepEnabled(false)// 是否开启声音,扫完码之后会"哔"的一声
//                .setBarcodeImageEnabled(true)// 扫完码之后生成二维码的图片
//                .addExtra(BaseActivity.KEY_PAGE_INFO, key)
//                .addExtra(BaseActivity.KEY_CALLBACK_ID, callbackId == null ? "" : callbackId)
//                .initiateScan();// 初始化扫码
        Intent intent = new Intent(context, CaptureActivity.class);
        /*ZxingConfig是配置类
         *可以设置是否显示底部布局，闪光灯，相册，
         * 是否播放提示音  震动
         * 设置扫描框颜色等
         * 也可以不传这个参数
         * */
        ZxingConfig config = new ZxingConfig();
        config.setPlayBeep(true);//是否播放扫描声音 默认为true
        config.setShake(true);//是否震动  默认为true
        config.setDecodeBarCode(true);//是否扫描条形码 默认为true
        config.setReactColor(R.color.white);//设置扫描框四个角的颜色 默认为白色
        config.setFrameLineColor(R.color.transparent);//设置扫描框边框颜色 默认无色
        config.setScanLineColor(R.color.white);//设置扫描线的颜色 默认白色
        config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        config.setShowAlbum(false);
        config.setCallbackId(callbackId);
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        ((Activity) context).startActivityForResult(intent, BaseActivity.REQ_CODE_SCAN);
    }

    /**
     * 跳转到WebActivity
     *
     * @param context Context
     * @param page    页面信息
     */
    private static void jump2WebActivity(Context context, PageInfo page) {
        String key = "key:" + System.currentTimeMillis();
        TempIntentData.putData(key, page);
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(BaseActivity.KEY_PAGE_INFO, key);
        context.startActivity(intent);
    }

    /**
     * 跳转到WebActivity
     *
     * @param context Context
     * @param page    页面信息
     * @param act     自定义动作
     * @param params  额外参数
     */
    private static void jump2WebActivity(Context context, PageInfo page, String act, HashMap<String, String> params) {
        String key = "key:" + System.currentTimeMillis();
        TempIntentData.putData(key, page);
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(BaseActivity.KEY_PAGE_INFO, key);
        if (!TextUtils.isEmpty(act)) {
            intent.putExtra(BaseActivity.KEY_PAGE_ACT, act);
        }
        if (params != null && !params.isEmpty()) {
            intent.putExtra(BaseActivity.KEY_PARAMS, params);
        }
        context.startActivity(intent);
    }

    /**
     * 初始化标题栏
     *
     * @param titleBar TitleBarView
     * @param page     PageInfo
     */
    public static void initTitleBar(TitleBarView titleBar, PageInfo page) {
        if (titleBar != null && page != null) {
            if (page.getNavinfo() != null && page.getNavinfo().isDisplay()) {
                String title = null;
                NavStyleInfo naviStyle = null;
                NavBtnInfo leftBtn = null, rightBtn = null;
                if (page.getNavinfo().isDisplay()) {
                    switch (page.getNavinfo().getNavType()) {
                        case "2":
                            leftBtn = page.getNavinfo().getLeft();
                            break;
                        case "3":
                            rightBtn = page.getNavinfo().getRight();
                            break;
                        case "4":
                            leftBtn = page.getNavinfo().getLeft();
                            rightBtn = page.getNavinfo().getRight();
                            break;
                    }
                    title = page.getNavinfo().getTitle();
                    naviStyle = page.getNavinfo().getStyle();
                }
                if (TextUtils.isEmpty(title)) {
                    title = page.getName();
                }
                titleBar.setTitle(title, naviStyle);
                titleBar.setLeftBtn(leftBtn);
                titleBar.setRightBtn(rightBtn);
            } else {
                titleBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 跳转（应用外）
     *
     * @param context Context
     * @param url     要跳转的url
     */
    public static void jumpOut(Context context, String url) {
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }


    /**
     * 打开第三方应用
     *
     * @param context     Context
     * @param packageName 应用包名
     * @return true/false
     */
    public static boolean openApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
}
