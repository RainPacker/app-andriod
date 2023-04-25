package com.tedu.zhongzhao.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.ui.PageInfo;
import com.tedu.zhongzhao.ui.UiPageConstans;
import com.tedu.zhongzhao.ui.UiUtil;
import com.tedu.zhongzhao.utils.TempIntentData;
import com.tedu.zhongzhao.web.WebViewCallback;
import com.tedu.zhongzhao.widget.WorkWebView;

import java.util.HashMap;

/**
 * Web Activity
 * Created by huangyx on 2018/3/13.
 */
public class WebActivity extends BaseActivity {

    private PageInfo mPageInfo;
    private WorkWebView mWebView;

    private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_web_layout);
        mWebView = (WorkWebView) findViewById(R.id.web_view);
        mWebView.setCallback(mCallback);

        mPageInfo = (PageInfo) TempIntentData.getData(getPageInfoKey());
        if (mPageInfo == null) {
            finish();
            return;
        }
        String act = getIntent().getStringExtra(BaseActivity.KEY_PAGE_ACT);
        if (TextUtils.isEmpty(act)) {
            act = mPageInfo.getAct();
        }
        setExitAni(act);
        findViewById(R.id.root_view).setBackgroundDrawable(UiUtil.getDrawable(mPageInfo.getStyle()));


        UiUtil.initTitleBar(mTitleBarView, mPageInfo);
        HashMap<String, String> params = (HashMap<String, String>) getIntent().getSerializableExtra(BaseActivity.KEY_PARAMS);
        String url = null;
        if (params != null) {
            if (UiPageConstans.VIEW_WEB_2.equalsIgnoreCase(mPageInfo.getControl())) {
                url = params.get("url");
                if (!TextUtils.isEmpty(params.get("navtitle"))) {
                    mTitleBarView.setTitle(params.get("navtitle"));
                }
                if ("true".equalsIgnoreCase(params.get("navshow"))) {
                    mTitleBarView.setVisibility(View.VISIBLE);
                } else {
                    mTitleBarView.setVisibility(View.GONE);
                }
            }
        }
        if (TextUtils.isEmpty(url)) {
            url = UiUtil.getWebUrl(mPageInfo, params);
        }
        if (mPageInfo.getView() != null) {
            mWebView.setNeedLoadingAni(mPageInfo.getView().isNeedLoadingAni());
        }
        mWebView.loadUrl(url);
    }

    private WebViewCallback mCallback = new WebViewCallback() {
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
    protected boolean doBackPressed() {
        return mWebView != null && mWebView.onBackPressed();
    }


    @Override
    protected void doQRResult(int result, String callbackId, String content) {
        if (mWebView != null) {
            mWebView.setQRResult(result, callbackId, content);
        }
    }

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
