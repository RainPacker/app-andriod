package com.tedu.zhongzhao.task;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.tedu.chat.util.ChatHelper;
import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.utils.FileUtils;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.base.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 环信初始化Task
 * Created by huangyx on 2018/4/16.
 */
public class HuanxinInitTask extends BaseTask {

    private static final String TAG = HuanxinInitTask.class.getSimpleName();

    @Override
    public void init() {

    }

    @Override
    public void init(String param, boolean isMultiParams) {

    }

    @Override
    public void acceptParam(String params) {

    }

    @Override
    public String doTask() {
        String txt = FileUtils.readAssetFile("config/hxconfig.txt");
        if (!TextUtils.isEmpty(txt)) {
            try {
                JSONObject json = new JSONObject(txt);
                final String key = JsonUtil.getString(json, "hxkey");
                final String user = JsonUtil.getString(json, "user");
                final String pwd = JsonUtil.getString(json, "pwd");
                if (!TextUtils.isEmpty(key)) {
                    ChatHelper.getInstance().init(WorkApplication.getApplication(), key);
                    /*EMClient.getInstance().login(user, pwd, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            EMClient.getInstance().chatManager().loadAllConversations();
                            EMClient.getInstance().groupManager().loadAllGroups();
                        }

                        @Override
                        public void onError(int code, String error) {
                            LogUtil.e(TAG, "huanxin login error: " + code + ", error");
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }
                    });*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
