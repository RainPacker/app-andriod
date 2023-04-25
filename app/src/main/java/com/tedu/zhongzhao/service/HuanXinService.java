package com.tedu.zhongzhao.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;
import com.tedu.chat.ui.ChatActivity;
import com.tedu.chat.util.ChatHelper;
import com.tedu.base.util.AndroidUtils;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.zhongzhao.web.ActionCallback;

import java.util.Map;

/**
 * 环信Service
 * Created by huangyx on 2018/5/2.
 */
public class HuanXinService extends BaseService {

    private static HuanXinService sInstance;

    synchronized public static HuanXinService getInstance() {
        if (sInstance == null) {
            sInstance = new HuanXinService();
        }
        return sInstance;
    }

    private HuanXinService() {

    }

    /**
     * 添加好友
     *
     * @param callback ActionCallback
     * @param context  Context
     * @param params   参数
     */
    public void doAddFriendWithId_andMessage_(ActionCallback callback, Context context, Map<String, String> params) {
        if (!AndroidUtils.isOnline()) {
            sendFail(callback, "e001", null);
            return;
        }
        String msg = null;
        String contact = null;
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("contact")) {
                contact = params.get("contact");
            }
            if (params.containsKey("msg")) {
                msg = params.get("msg");
            }
        }
        if (!TextUtils.isEmpty(contact)) {
            try {
//                EMClient.getInstance().contactManager().setContactListener();
                EMClient.getInstance().contactManager().addContact(contact, msg);
                sendSuccess(callback, "true");
            } catch (HyphenateException e) {
                e.printStackTrace();
                sendSuccess(callback, "false");
            }
        } else {
            sendSuccess(callback, "false");
        }
    }

    /**
     * 调起环信聊天
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void doCallingHuanXinClientWithId_view_controller_(ActionCallback callback, Context context, Map<String, String> params) {
        String user = null;
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("contact")) {
                user = params.get("contact");
            }
        }
        if (!TextUtils.isEmpty(user)) {
            Intent intent = new Intent(context, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(EaseConstant.EXTRA_USER_ID, user);
            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else {
            sendSuccess(callback, "false");
        }
    }

    /**
     * 拉起环信好友列表
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void doGetFriendList(ActionCallback callback, Context context, Map<String, String> params) {
        if (!AndroidUtils.isOnline()) {
            sendFail(callback, "e001", null);
            return;
        }
        Map<String, EaseUser> contacts = ChatHelper.getInstance().getContactList();
        if (contacts != null) {
            sendSuccess(callback, JsonUtil.toJson(contacts.keySet()));
        } else {
            sendSuccess(callback, "false");
        }

    }

    /**
     * 同意添加好友
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void doAgreeFriendWithId_(ActionCallback callback, Context context, Map<String, String> params) {
        if (!AndroidUtils.isOnline()) {
            sendFail(callback, "e001", null);
            return;
        }
        String user = null;
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("contact")) {
                user = params.get("contact");
            }
        }
        if (!TextUtils.isEmpty(user)) {
            ChatHelper.getInstance().acceptInvitation(user);
            sendSuccess(callback, "true");
        } else {
            sendSuccess(callback, "false");
        }
    }

    /**
     * 拒绝添加好友
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void doDenyFriendWithId_(ActionCallback callback, Context context, Map<String, String> params) {
        if (!AndroidUtils.isOnline()) {
            sendFail(callback, "e001", null);
            return;
        }
        String user = null;
        if (params != null && !params.isEmpty()) {
            if (params.containsKey("contact")) {
                user = params.get("contact");
            }
        }
        if (!TextUtils.isEmpty(user)) {
            ChatHelper.getInstance().declineInvitation(user);
            sendSuccess(callback, "true");
        } else {
            sendSuccess(callback, "false");
        }
    }

    /**
     * 拒绝添加好友
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void loginToHuanxinWithUserName_andPwd_(final ActionCallback callback, Context context, Map<String, String> params) {
        if (!AndroidUtils.isOnline()) {
            sendFail(callback, "e001", null);
            return;
        }
        if (params != null && !params.isEmpty()) {
            String userName = null;
            String pwd = null;
            if (params.containsKey("username")) {
                userName = params.get("username");
            }
            if (params.containsKey("pwd")) {
                pwd = params.get("pwd");
            }
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(pwd)) {
                ChatHelper.getInstance().login(userName, pwd, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().chatManager().loadAllConversations();
                        EMClient.getInstance().groupManager().loadAllGroups();
                        sendSuccess(callback, "true");
                    }

                    @Override
                    public void onError(int code, String error) {
                        sendSuccess(callback, "false");
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        sendSuccess(callback, "false");
                    }
                });
            } else {
                sendSuccess(callback, "false");
            }
        }
    }

    /**
     * 拒绝添加好友
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void emSignOut(final ActionCallback callback, Context context, Map<String, String> params) {
        if (!AndroidUtils.isOnline()) {
            sendFail(callback, "e001", null);
            return;
        }
       ChatHelper.getInstance().logout(true, new EMCallBack() {
           @Override
           public void onSuccess() {
               sendSuccess(callback, "true");
           }

           @Override
           public void onError(int code, String error) {
               sendSuccess(callback, "false");
           }

           @Override
           public void onProgress(int progress, String status) {
               sendSuccess(callback, "false");
           }
       });
    }
}
