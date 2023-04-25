package com.tedu.zhongzhao.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tedu.base.util.LogUtil;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.activity.BaseActivity;
import com.tedu.zhongzhao.service.DNEventService;
import com.tedu.zhongzhao.web.ActionCallback;
import com.tedu.zhongzhao.web.WebDelegate;
import com.tedu.zhongzhao.web.WebViewCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 封闭WebView
 * Created by huangyx on 2018/3/7.
 */
public class WorkWebView extends FrameLayout {

    private BaseActivity mActivity;
    private WebView mWebView;
    private Handler mHandler;
    private WebViewCallback mCallback;
    private ImageView mLoadingView;
    private HashMap<String, ActionCallback> mActionCallbacks;
    private WorkWebJs mWebJs;
    private boolean mNeedLoadingAni;

    public WorkWebView(@NonNull Context context) {
        this(context, null);
    }

    public WorkWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WorkWebView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCallback(WebViewCallback callback) {
        this.mCallback = callback;
    }

    private void init(Context context) {
        mNeedLoadingAni = false;
        mActionCallbacks = new HashMap<String, ActionCallback>();
        if (context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            Context tmp;
            while ((tmp = ((ContextThemeWrapper) context).getBaseContext()) != null) {
                if (tmp instanceof BaseActivity) {
                    mActivity = (BaseActivity) tmp;
                    break;
                }
            }
        }
        mHandler = new Handler(Looper.getMainLooper());

        mWebView = new WebView(context);
        mWebView.setBackgroundColor(Color.WHITE);
        addView(mWebView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        initWebView();

        showLoading();
    }

    public void setNeedLoadingAni(boolean need) {
        this.mNeedLoadingAni = need;
    }

    /**
     * 显示loading
     */
    private void showLoading() {
        if (mLoadingView == null && mNeedLoadingAni) {
            mLoadingView = new ImageView(this.getContext());
            mLoadingView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mLoadingView.setBackgroundColor(Color.WHITE);
            addView(mLoadingView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            Glide.with(this.getContext()).load(R.mipmap.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mLoadingView);
        }
    }

    /**
     * 关闭loading
     */
    private void dismissLoading() {
        if (mLoadingView != null) {
            removeView(mLoadingView);
            mLoadingView = null;
        }
    }

    /**
     * 初始化webView
     */
    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        //禁用选择，复制功能
        mWebView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                if (mWebView != null) {
                    WebView.HitTestResult hit = mWebView.getHitTestResult();
                    // 只启用输入框的长按功能
                    if (hit != null && hit.getType() == WebView.HitTestResult.EDIT_TEXT_TYPE) {
                        return false;
                    }
                }
                return true;
            }
        });
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }
        mWebJs = new WorkWebJs(mActivity);
        mWebView.addJavascriptInterface(mWebJs, "stub");
        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(false);
        try {
            // fixed: 联想手机会初始化语音，NullPointerException
            settings.setJavaScriptEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setSavePassword(false);
        // 缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        // 开启 database storage API 功能
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(WorkApplication.getWebCacheDir());
        // 开启 Application Caches 功能
        settings.setAppCacheEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(WorkApplication.getWebCacheDir());

        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccessFromFileURLs(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setSupportZoom(false);
        settings.setSupportMultipleWindows(true);
        settings.setGeolocationEnabled(true);
        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        /*if (android.os.Build.VERSION.SDK_INT >= 16) {
            // 利用反射机制修改允许跨域访问
            try {
                Class<?> clazz = settings.getClass();
                java.lang.reflect.Method method = clazz.getMethod(
                        "setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(settings, true);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.reflect.InvocationTargetException e) {
                e.printStackTrace();
            }
        }*/

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                if (TextUtils.isEmpty(url)) {
                    return false;
                }
                final Uri uri = Uri.parse(url);
                String scheme = uri.getScheme();
                // 自己定义的协议
                if ("calling".equalsIgnoreCase(scheme)) {
                    LogUtil.i("hyongx", "url:" + url);
                    if (mHandler != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                doAction(uri);
                            }
                        });
                    }
                    return true;
                } else if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mActivity.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoading();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mWebView != null) {
                    dismissLoading();
                    super.onPageFinished(view, url);
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mWebView.loadUrl("javascript:document.body.innerHTML=' '");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
                if (handler != null) {
                    handler.proceed();
                }
            }

        });
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mActivity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        mWebView.setWebChromeClient(new MyWebChromeClient());
    }

    private void doAction(Uri uri) {
        String callbackId = uri.getQueryParameter("callback");
        ActionCallback callback = new ActionCallback(callbackId) {
            @Override
            public void onSuccess(String result) {
                loadUrl("javascript:Success('" + callbackId + "','" + result + "');");
            }

            @Override
            public void onFail(String result, String error) {
                loadUrl("javascript:Failed('" + callbackId + "','" + result + "','" + error + "');");
            }

            @Override
            public void onBegin(String result) {
                loadUrl("javascript:Begin('" + callbackId + "', '" + result + "');");
            }

            @Override
            public void onProgress(String result) {
                loadUrl("javascript:Inprocess('" + callbackId + "', '" + result + "');");
            }

            @Override
            public void onCancel() {
                loadUrl("javascript:Cancel('" + callbackId + "', 'cancelled');");
            }

            @Override
            public void onBack() {
                if (mCallback != null) {
                    mCallback.onBack();
                }
            }

            @Override
            public void onNotify(String content) {
                loadUrl("javascript:Notify('" + content + "')");
            }
        };
        if (!TextUtils.isEmpty(callbackId)) {
            mActionCallbacks.put(callbackId, callback);
        }
        WebDelegate.doAction(getContext(), uri, callback);
    }

    public void loadUrl(final String url) {
        if (mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mWebView != null) {
                    mWebView.loadUrl(url);
                }
            }
        });
    }

    public void refresh() {
        if (mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                reloadSafe();
            }
        });
    }

    private void reloadSafe() {
        if (mWebView != null) {
            try {
                mWebView.reload();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onBackPressed() {
        /*if (mWebView.canGoBack()) {
            WebBackForwardList list = mWebView.copyBackForwardList();
            if (list != null) {
                int idx = list.getCurrentIndex();
                if (idx > 0) {
                    int i = idx;
                    String url = null;
                    String tmpUrl = null;
                    // 如果当前为错误页，需要取上一次的url，通过循环往复比较，比同一组（+错误页）的认为是一次浏览记录
                    while (i >= 0) {
                        url = list.getItemAtIndex(i).getUrl();
                        if (tmpUrl == null || url.equals(tmpUrl)) {
                            i--;
                            tmpUrl = url;
                            continue;
                        }
                        break;
                    }
                    url = null;
                    tmpUrl = null;
                    list = null;
                    if (i < 0) { // 一组需要全部退出
                        return false;
                    }
                    mWebView.goBackOrForward(i - idx);
                    return true;
                }
            }
        }*/
        if (mWebJs != null && mWebJs.isInterceptBack()) {
            // 通知H5执行返回操作
            mWebView.loadUrl("javascript:backButtonAction()");
            return true;
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mActivity = null;
        mWebView.stopLoading();
        if (mWebJs != null) {
            mWebJs.onRelease();
        }
        mWebView.removeJavascriptInterface("stub");
        mWebView.destroy();
        mWebView = null;
        mCallback = null;
        mActionCallbacks.clear();
        DNEventService.getInstance().clear(getContext());

        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public void setQRResult(int result, String callbackId, String content) {
        if (!TextUtils.isEmpty(callbackId)) {
            if (mActionCallbacks.containsKey(callbackId)) {
                if (result == 0) {
                    mActionCallbacks.get(callbackId).onFail("取消扫码", "");
                } else {
                    mActionCallbacks.get(callbackId).onSuccess(content);
                }
            }
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mCallback != null) {
                mCallback.onGotTitle(title);
            }
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            if (mCallback != null) {
                mCallback.onShowCustomView(view, callback);
            }
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCallback != null) {
                mCallback.onHideCustomView();
            }
        }

        // For Android 5.0+
        /*
          需加入混淆
         */
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            if (mCallback != null) {
                mCallback.callback(null, valueCallback);
            }
            choosePicture();
            return true;
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType, String capture) {
            if (mCallback != null) {
                mCallback.callback(uploadMsg, null);
            }
            choosePicture();
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType) {
            if (mCallback != null) {
                mCallback.callback(uploadMsg, null);
            }
            choosePicture();
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            if (mCallback != null) {
                mCallback.callback(uploadMsg, null);
            }
            choosePicture();
        }
    }

    private void choosePicture() {
        String cameraName = "a" + System.currentTimeMillis();
        String cameraDir = android.os.Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/app/cache";
        String cameraPath = cameraDir + "/" + cameraName + ".jpg";

        File file = new File(cameraDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        Uri imageUri = Uri.fromFile(new File(cameraPath));
        if (mCallback != null) {
            mCallback.callback(imageUri);
        }
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getContext().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);
        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i, "拍照或选择照片");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        ((Activity) getContext()).startActivityForResult(chooserIntent, 1111);
    }
}
