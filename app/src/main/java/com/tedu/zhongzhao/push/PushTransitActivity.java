package com.tedu.zhongzhao.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.tedu.zhongzhao.activity.NormalWebActivity;

import cn.jpush.android.api.JPushInterface;

/**
 * push过渡activity，只要用来做业务跳转，及埋点处理
 * Created by huangyx on 2018/4/10.
 */
public class PushTransitActivity extends AppCompatActivity {

    private static final String EXTRA_KEY = "extra_push";

    public static Intent newIntent(Context context, PushInfo push) {
        Intent intent = new Intent(context, PushTransitActivity.class);
        intent.putExtra(EXTRA_KEY, push);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushInfo push = getIntent().getParcelableExtra(EXTRA_KEY);
        if (push != null) {
            if (!push.isNotify() && !TextUtils.isEmpty(push.getPushId())) {
                // 透传内容（自定义消息），上报极光
                JPushInterface.reportNotificationOpened(this, push.getPushId());
            }
            if (!TextUtils.isEmpty(push.getTargetUrl())) {
                // TODO 根据url 判断跳转对像
                Intent intent = NormalWebActivity.newIntent(this, push.getTargetUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        finish();

    }
}
