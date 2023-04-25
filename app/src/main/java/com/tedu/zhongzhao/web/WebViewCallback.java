package com.tedu.zhongzhao.web;

import android.net.Uri;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

/**
 * Web view 回调
 * Created by huangyx on 2018/3/29.
 */
public interface WebViewCallback {
    void onBack();

    void onGotTitle(String title);

    void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback);

    void onHideCustomView();

    void callback(ValueCallback<Uri> uploadMessage, ValueCallback<Uri[]> uploadCallbackAboveL);

    void callback(Uri imageUri);
}
