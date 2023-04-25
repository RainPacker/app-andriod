package com.tedu.zhongzhao.ui;

import android.text.TextUtils;

import com.tedu.zhongzhao.utils.SettingUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 界面提示红点管理类
 * Created by huangyx on 2018/4/3.
 */
public class PageNotifiManager {

    private static final String NOTI_KEY = "page_noti:";

    /**
     * 通知界面红点
     *
     * @param key   消息KEY
     * @param count 消息数量
     */
    public static void notifi(String key, int count) {
        if (!TextUtils.isEmpty(key)) {
            SettingUtil.putInt(NOTI_KEY + key, count);
            EventBus.getDefault().post(new PageNotifiEvent(key));
        }
    }

    public static void clear(String key) {
        if (!TextUtils.isEmpty(key)) {
            SettingUtil.putInt(NOTI_KEY + key, 0);
        }
    }

    /**
     * 获取消息数量
     *
     * @param key 消息KEY
     * @return 消息数量
     */
    public static int getNotifiCount(String key) {
        if (!TextUtils.isEmpty(key)) {
            return SettingUtil.getInt(NOTI_KEY + key, 0);
        }
        return 0;
    }

    /**
     * 获取tab标签的消息数量
     *
     * @param page PageInfo
     * @return 消息数量
     */
    public static int getTabNotifiCount(PageInfo page) {
        int count = 0;
        if (page != null) {
            // 1. 本身的消息
            count += getShelfNotifiCount(page);
            // 2. 标题栏左右按钮的消息
            if (page.getNavinfo() != null) {
                count += getNavNotifiCount(page.getNavinfo().getLeft());
                count += getNavNotifiCount(page.getNavinfo().getRight());
            }
            // 3. 子页面的消息
            if (page.getItems() != null && !page.getItems().isEmpty()) {
                for (PageInfo p : page.getItems()) {
                    count += getShelfNotifiCount(p);
                }
            }
        }
        return count;
    }

    /**
     * 本身的消息数量
     *
     * @param page PageInfo
     * @return 消息数量
     */
    public static int getShelfNotifiCount(PageInfo page) {
        int count = 0;
        if (page != null) {
            List<PageNotifi> notifies = page.getNotifies();
            if (notifies != null && !notifies.isEmpty()) {
                for (PageNotifi n : notifies) {
                    count += getNotifiCount(n.getKey());
                }
            }
        }
        return count;
    }

    /**
     * 获取标题栏上按钮消息提醒数量
     *
     * @param nav NavBtnInfo
     * @return 消息数量
     */
    public static int getNavNotifiCount(NavBtnInfo nav) {
        int count = 0;
        if (nav != null) {
            List<PageNotifi> notifies = nav.getNotifies();
            if (notifies != null && !notifies.isEmpty()) {
                for (PageNotifi n : notifies) {
                    count += getNotifiCount(n.getKey());
                }
            }
            if (nav.getItems() != null && !nav.getItems().isEmpty()) {
                for (PageInfo p : nav.getItems()) {
                    count += getShelfNotifiCount(p);
                }
            }
        }
        return count;
    }


    public static PageNotifi matchingNotifi(PageInfo page, String key) {
        if (page == null || TextUtils.isEmpty(key)) {
            return null;
        }

        List<PageNotifi> notifis = page.getNotifies();
        if (notifis != null && !notifis.isEmpty()) {
            for (PageNotifi n : notifis) {
                if (key.equals(n.getKey())) {
                    return n;
                }
            }
        }

        return null;
    }

    public static PageNotifi matchingNotifi(NavBtnInfo info, String key) {
        if (info == null || TextUtils.isEmpty(key)) {
            return null;
        }
        List<PageNotifi> notifis = info.getNotifies();
        if (notifis != null && !notifis.isEmpty()) {
            for (PageNotifi n : notifis) {
                if (key.equals(n.getKey())) {
                    return n;
                }
            }
        }
        PageNotifi n = null;
        if (UiPageConstans.NAV_MENU.equals(info.getType())) {
            if (info.getItems() != null && !info.getItems().isEmpty()) {
                for (PageInfo p : info.getItems()) {
                    n = matchingNotifi(p, key);
                    if (n != null) {
                        break;
                    }
                }
            }
        }
        return n;
    }
}
