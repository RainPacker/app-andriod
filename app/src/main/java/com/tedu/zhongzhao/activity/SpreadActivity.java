package com.tedu.zhongzhao.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.bumptech.glide.Glide;
import com.tedu.base.permission.PermissionsManager;
import com.tedu.base.permission.PermissionsResultAction;
import com.tedu.base.util.AndroidUtils;
import com.tedu.base.util.LogUtil;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.bean.SpreadInfo;
import com.tedu.zhongzhao.task.TaskExecutor;
import com.tedu.zhongzhao.ui.UiUtil;
import com.tedu.zhongzhao.utils.SpreadUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 开屏Activity
 * Created by huangyx on 2018/3/5.
 */
public class SpreadActivity extends BaseActivity {

    private FrameLayout mLayout;
    private ViewPager mViewPager;
    private List<ImageView> mGuideViews;
    private List<ImageView> mIndicatorViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_spread_layout);
        TaskExecutor.getInstance().start();

        int width = ScreenUtils.getWidth(this);
        int height = ScreenUtils.getHeight(this);
        mLayout = (FrameLayout) findViewById(R.id.spread_fragment);

        requestPermissions();

    }

    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, permissionsResultAction);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        LogUtil.w("hyongx", "permission result=");
    }

    private void doStartCompleted() {
        // 显示新手引导
        SpreadInfo info = SpreadUtil.getGuideInfo();
        if (info != null && info.getUrlList() != null && !info.getUrlList().isEmpty()) {
            mLayout.removeAllViews();
            mGuideViews = new ArrayList<>();
            mIndicatorViews = new ArrayList<>();
            LinearLayout indicatorView = new LinearLayout(this);
            indicatorView.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < info.getUrlList().size(); i++) {
                ImageView iv = new ImageView(this);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(this).load(info.getUrlList().get(i)).asBitmap().override(ScreenUtils.getWidth(this),
                        ScreenUtils.getHeight(this)).into(iv);
                mGuideViews.add(iv);
                if (i == info.getUrlList().size() - 1) {
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UiUtil.startMain(SpreadActivity.this);
                            finish();
                        }
                    });
                }
                ImageView ic = new ImageView(this);
                ic.setBackgroundResource(R.drawable.selector_guide_indicator);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(AndroidUtils.dp2px(8), AndroidUtils.dp2px(8));
                lp.leftMargin = AndroidUtils.dp2px(2);
                lp.rightMargin = AndroidUtils.dp2px(2);
                indicatorView.addView(ic, lp);
                mIndicatorViews.add(ic);
                if (mIndicatorViews.size() == 1) {
                    ic.setSelected(true);
                }
            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mViewPager = new ViewPager(this);
            mLayout.addView(mViewPager, lp);
            mViewPager.setAdapter(new GuideAdapter());
            lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            lp.bottomMargin = AndroidUtils.dp2px(16);
            mLayout.addView(indicatorView, lp);
            mViewPager.addOnPageChangeListener(pageChangeListener);
            SpreadUtil.setGuideShowed(info);
        } else {
            UiUtil.startMain(SpreadActivity.this);
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(pageChangeListener);
        }
        PermissionsManager.getInstance().removePendingAction(permissionsResultAction);
        super.onDestroy();
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mIndicatorViews != null) {
                for (int i = 0; i < mIndicatorViews.size(); i++) {
                    if (position == i) {
                        mIndicatorViews.get(i).setSelected(true);
                    } else {
                        mIndicatorViews.get(i).setSelected(false);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private PermissionsResultAction permissionsResultAction = new PermissionsResultAction() {
        @Override
        public void onGranted() {

        }

        @Override
        public void onDenied(String permission) {

        }

        @Override
        public void onEnd() {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    doStartCompleted();
                }
            }, 3000);
        }
    };

    private class GuideAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = mGuideViews.get(position);
            if (iv.getParent() != null) {
                ((ViewGroup) iv.getParent()).removeView(iv);
            }
            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mGuideViews == null ? 0 : mGuideViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
