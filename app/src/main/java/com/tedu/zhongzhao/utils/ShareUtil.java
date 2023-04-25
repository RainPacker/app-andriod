package com.tedu.zhongzhao.utils;

import android.content.Context;
import android.text.TextUtils;

import com.tedu.zhongzhao.bean.ShareInfo;

import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 分享工具类
 * Created by huangyx on 2018/4/5.
 */
public class ShareUtil {

    /**
     * 分享内容
     *
     * @param context  Context
     * @param info     分享内容
     * @param callback 平台回调
     */
    public static void share(Context context, ShareInfo info, PlatformActionListener callback) {
        share(context, null, info, callback);
    }

    /**
     * 指定平台进行分享
     *
     * @param context  Context
     * @param platform 分享平台
     * @param info     分享内容
     * @param callback 平台回调
     */
    public static void share(Context context, SharePlatform platform, ShareInfo info, PlatformActionListener callback) {
        if (info == null) {
            return;
        }
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        if (!TextUtils.isEmpty(info.getTitle())) {
            // title标题，微信、QQ和QQ空间等平台使用
            oks.setTitle(info.getTitle());
        }
        // text是分享文本，所有平台都需要这个字段
        oks.setText(info.getText());
        if (info.getImages() != null && !info.getImages().isEmpty()) {
            String[] images = new String[info.getImages().size()];
            for (int i = 0; i < info.getImages().size(); i++) {
                images[i] = info.getImages().get(i);
            }
            oks.setImageArray(images);
            oks.setImageUrl(images[0]);
        }
        if (!TextUtils.isEmpty(info.getUrl())) {
            // url在微信、微博，Facebook等平台中使用
            oks.setUrl(info.getUrl());
        }
        String pname = null;
        if (platform != null) {
            switch (platform) {
                case SINA:
//                    pname = SinaWeibo.NAME;
                    break;
                case WECHAT:
                    pname = Wechat.NAME;
                    break;
                case WECHAT_MOMENTS:
                    pname = WechatMoments.NAME;
                    break;
                case EMAIL:
//                    pname = Email.NAME;
                    break;
                case SMS:
//                    pname = ShortMessage.NAME;
                    break;
            }
        }
        // 指定要分享的平台
        if (!TextUtils.isEmpty(pname)) {
            oks.setPlatform(pname);
        }
        oks.setCallback(callback);
        // 启动分享GUI
        oks.show(context);
    }

    /**
     * 分享
     *
     * @param context Context
     * @param content 分享文本
     * @param url     文本链接URL： 微信、微博等平台使用
     */
    public static void show(Context context, String content, String url) {
        show(context, null, null, null, content, url, null);
    }

    /**
     * 分享
     *
     * @param context Context
     * @param content 分享文本
     * @param url     文本链接URL： 微信、微博等平台使用
     */
    public static void show(Context context, SharePlatform platform, String content, String url) {
        show(context, platform, null, null, content, url, null);
    }

    /**
     * 分享
     *
     * @param context    Context
     * @param title      标题： 微信、QQ、QQ空间平台使用
     * @param titleUrl   标题跳转Url: QQ和QQ空间使用
     * @param content    分享文本
     * @param contentUrl 文本链接URL： 微信、微博等平台使用
     * @param image      分享的图片（本地图片）
     */
    public static void show(Context context, String title, String titleUrl, String content,
                            String contentUrl, String image) {
        show(context, null, title, titleUrl, content, contentUrl, image);
    }


    /**
     * 分享
     *
     * @param context    Context
     * @param platform   使用平台 SharePlatform
     * @param title      标题： 微信、QQ、QQ空间平台使用
     * @param titleUrl   标题跳转Url: QQ和QQ空间使用
     * @param content    分享文本
     * @param contentUrl 文本链接URL： 微信、微博等平台使用
     * @param image      分享的图片（本地图片）
     */
    public static void show(Context context, SharePlatform platform, String title, String titleUrl,
                            String content, String contentUrl, String image) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        if (!TextUtils.isEmpty(title)) {
            // title标题，微信、QQ和QQ空间等平台使用
            oks.setTitle(title);
        }
        if (!TextUtils.isEmpty(titleUrl)) {
            // titleUrl QQ和QQ空间跳转链接
            oks.setTitleUrl(titleUrl);
        }
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        if (!TextUtils.isEmpty(image)) {
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            oks.setImagePath(image);//确保SDcard下面存在此张图片
        }
        if (!TextUtils.isEmpty(contentUrl)) {
            // url在微信、微博，Facebook等平台中使用
            oks.setUrl(contentUrl);
        }
        String pname = null;
        if (platform != null) {
            switch (platform) {
                case SINA:
//                    pname = SinaWeibo.NAME;
                    break;
                case WECHAT:
                    pname = Wechat.NAME;
                    break;
                case WECHAT_MOMENTS:
                    pname = WechatMoments.NAME;
                    break;
                case EMAIL:
//                    pname = Email.NAME;
                    break;
                case SMS:
//                    pname = ShortMessage.NAME;
                    break;
            }
        }
        // 指定要分享的平台
        if (!TextUtils.isEmpty(pname)) {
            oks.setPlatform(pname);
        }
        // 启动分享GUI
        oks.show(context);
    }

    public enum SharePlatform {
        SINA,
        WECHAT,
        WECHAT_MOMENTS,
        EMAIL,
        SMS
    }
}
