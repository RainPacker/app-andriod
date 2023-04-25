package com.tedu.zhongzhao.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.bean.PlayerInfo;
import com.tedu.zhongzhao.utils.JsonUtil;

/**
 * 播放视频/直播/点播
 * Created by huangyx on 2018/7/14.
 */
public class PlayVideoActivity extends BaseActivity {

    private AliyunVodPlayerView mPlayerView;

    private PlayerInfo mPlayerInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.act_play_video_layout);

        String params = getIntent().getStringExtra("params");
        if (!TextUtils.isEmpty(params)) {
            mPlayerInfo = JsonUtil.fromJson(params, PlayerInfo.class);
        }
        mPlayerView = (AliyunVodPlayerView) findViewById(R.id.video_player_view);
        mPlayerView.setVisibility(View.GONE);
        mPlayerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayerView.setVisibility(View.VISIBLE);
                if (mPlayerInfo != null && !isFinishing()) {
                    if (mPlayerInfo.getPlayType() == 0) {
                        AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
                        alsb.setSource(mPlayerInfo.getUrl());
                        AliyunLocalSource localSource = alsb.build();
                        mPlayerView.setLocalSource(localSource);
                    } else if (mPlayerInfo.getPlayType() == 1) {
                        AliyunVidSts vidSts = new AliyunVidSts();
                        vidSts.setVid(mPlayerInfo.getVid());
                        vidSts.setAcId(mPlayerInfo.getAccessKeyId());
                        vidSts.setAkSceret(mPlayerInfo.getAccessKeySecret());
                        vidSts.setSecurityToken(mPlayerInfo.getSecurityToken());
                        mPlayerView.setVidSts(vidSts);

                    }
                }
            }
        }, 300);
        mPlayerView.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                if (!isFinishing()) {
                    mPlayerView.start();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayerView.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.onDestroy();
    }
}
