package com.tedu.zhongzhao.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tedu.base.util.AndroidUtils;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.adapter.TabFragmentAdapter;
import com.tedu.zhongzhao.fragment.WebFragment;
import com.tedu.zhongzhao.ui.IconStyleInfo;
import com.tedu.zhongzhao.ui.PageInfo;
import com.tedu.zhongzhao.ui.PageNotifiEvent;
import com.tedu.zhongzhao.ui.PageNotifiManager;
import com.tedu.zhongzhao.ui.UiUtil;
import com.tedu.zhongzhao.utils.TempIntentData;
import com.tedu.zhongzhao.web.WebViewCallback;
import com.tedu.zhongzhao.widget.WorkViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 带底部Tab的Activity
 * Created by huangyx on 2018/3/12.
 */
@SuppressWarnings("deprecation")
public class TabActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private LinearLayout mTabLayout;
    private WorkViewPager mViewPager;
    private TabFragmentAdapter mAdapter;

    private List<WebFragment> mFragments;
    private List<View> mTabViews;
    private LayoutInflater mInflater;

    private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tab_layout);
        mViewPager = (WorkViewPager) findViewById(R.id.tab_content_layout);
        mViewPager.setCanScroll(false);
        mTabLayout = (LinearLayout) findViewById(R.id.tab_foot_layout);
        mTabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                PageNotifiManager.notifi("notify_key_2", 3);
            }
        }, 5000);
        mInflater = LayoutInflater.from(this);

        String pageKey = getPageInfoKey();
        if (!TextUtils.isEmpty(pageKey)) {
            List<PageInfo> pages = (List<PageInfo>) TempIntentData.getData(pageKey);
            if (pages != null && !pages.isEmpty()) {
                mViewPager.setOffscreenPageLimit(pages.size());
                mFragments = new ArrayList<WebFragment>();
                mTabViews = new ArrayList<View>();
                WebFragment fragment;
                PageInfo page;
                for (int i = 0; i < pages.size(); i++) {
                    page = pages.get(i);
                    fragment = WebFragment.newInstance(page, mWebCallback);
                    mFragments.add(fragment);
                    addTabView(page, i);
                }
                mAdapter = new TabFragmentAdapter(getSupportFragmentManager(), mFragments);
                mViewPager.setAdapter(mAdapter);
                mViewPager.setOffscreenPageLimit(mFragments.size());
            }
        }

        mViewPager.addOnPageChangeListener(this);
    }

    /**
     * 添加tab 标签
     */
    private void addTabView(PageInfo page, final int pos) {
        IconStyleInfo style = page.getStyle();
        String name = page.getName();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        View itemView = mInflater.inflate(R.layout.item_wgt_tab_layout, null);
        itemView.setTag(page);
        itemView.setSelected(pos == 0);
        itemView.setId(page.getStyle().hashCode());
        itemView.setBackgroundDrawable(UiUtil.getDrawable(style));
        ImageView iconView = (ImageView) itemView.findViewById(R.id.item_tab_icon_view);
        iconView.setImageDrawable(UiUtil.getStateListDrawable(style));
        TextView txtView = (TextView) itemView.findViewById(R.id.item_tab_txt_view);
        txtView.setText(name);
        TextView tipView = (TextView) itemView.findViewById(R.id.item_tab_point);
        showTipDot(tipView, PageNotifiManager.getTabNotifiCount(page), false);
        mTabLayout.addView(itemView, lp);
        mTabViews.add(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(pos, false);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mTabViews != null) {
            for (int i = 0; i < mTabViews.size(); i++) {
                mTabViews.get(i).setSelected(i == position);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void showTipDot(TextView tv, int msgCount, boolean showNum) {
        if (msgCount > 0) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            if (showNum) {
                lp.width = lp.height = AndroidUtils.dp2px(16);
                tv.setText(String.valueOf(msgCount));
            } else {
                lp.width = lp.height = AndroidUtils.dp2px(6);
                tv.setText("");
            }
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEvent(PageNotifiEvent event) {
        if (event != null) {
            int count = mTabViews.size();
            for (int i = 0; i < count; i++) {
                View tab = mTabViews.get(i);
                PageInfo page = (PageInfo) tab.getTag();
                TextView tv = (TextView) tab.findViewById(R.id.item_tab_point);
                int msgCount = PageNotifiManager.getTabNotifiCount(page);
                showTipDot(tv, msgCount, false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.destroy();
        }
        if (mFragments != null) {
            mFragments.clear();
        }
        if (mTabViews != null) {
            mTabViews.clear();
        }
        mViewPager.removeOnPageChangeListener(this);
    }

    @Override
    protected boolean doBackPressed() {
        WebFragment fragment = mAdapter.getItem(mViewPager.getCurrentItem());
        return fragment.onBackPressed();
    }

    @Override
    protected void doQRResult(int result, String callbackId, String content) {
        mAdapter.getItem(mViewPager.getCurrentItem()).doQRResult(result, callbackId, content);
    }

    private WebViewCallback mWebCallback = new WebViewCallback() {
        @Override
        public void onBack() {
            onBackPressed();
        }

        @Override
        public void onGotTitle(String title) {

        }

        @Override
        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            showWebCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            hideWebCustomView();
        }

        @Override
        public void callback(ValueCallback<Uri> uploadMessage, ValueCallback<Uri[]> uploadCallbackAboveL) {
            mUploadMessage = uploadMessage;
            mUploadCallbackAboveL = uploadCallbackAboveL;
        }

        @Override
        public void callback(Uri uri) {
            imageUri = uri;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else  if (mUploadMessage != null) {
                if(result==null){
                    mUploadMessage.onReceiveValue(imageUri);
                    mUploadMessage = null;
                }else {
                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }
            }
        }
    }

    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1111 || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        if(results!=null){
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }else{
            results = new Uri[]{imageUri};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }
        return;
    }
}
