package com.tedu.zhongzhao.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.ui.PageInfo;
import com.tedu.zhongzhao.ui.PageNotifiEvent;
import com.tedu.zhongzhao.ui.PageNotifiManager;
import com.tedu.zhongzhao.ui.UiUtil;
import com.tedu.zhongzhao.web.WebViewCallback;
import com.tedu.zhongzhao.widget.TitleBarView;
import com.tedu.zhongzhao.widget.WorkWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 用于显示网页的WebView
 * Created by huangyx on 2018/3/12.
 */
public class WebFragment extends Fragment {

    public static WebFragment newInstance(PageInfo pageInfo, WebViewCallback callback) {
        WebFragment fragment = new WebFragment();
        fragment.setPageInfo(pageInfo);
        fragment.setCallback(callback);
        return fragment;
    }

    private ViewGroup mRootVIew;
    // 是否已经准备好
    private boolean isPrepared = false;
    // 是否第一次加载
    private boolean isFirst = true;

    public WebFragment() {

    }

    private PageInfo mPageInfo;

    private TitleBarView mTitleBar;
    private WorkWebView mWebView;

    private WebViewCallback mCallback;

    private void setCallback(WebViewCallback callback) {
        this.mCallback = callback;
    }

    private void setPageInfo(PageInfo info) {
        this.mPageInfo = info;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        if (mRootVIew == null) {
            mRootVIew = (ViewGroup) inflater.inflate(R.layout.frg_web_layout, null);
            initView();
        }
        return mRootVIew;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        mRootVIew.setBackgroundDrawable(UiUtil.getDrawable(mPageInfo.getStyle()));
        mTitleBar = (TitleBarView) mRootVIew.findViewById(R.id.title_bar);
        mTitleBar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mWebView = (WorkWebView) mRootVIew.findViewById(R.id.web_view);

        if (mPageInfo != null && mPageInfo.getView() != null) {
            mWebView.setNeedLoadingAni(mPageInfo.getView().isNeedLoadingAni());
        }
        mWebView.setCallback(mCallback);
        UiUtil.initTitleBar(mTitleBar, mPageInfo);
        showTitleDot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PageNotifiEvent event) {
        if (event != null && mPageInfo != null && mPageInfo.getNavinfo() != null) {
            showTitleDot();
        }
    }

    /**
     * 显示标题拦按钮的消息提醒
     */
    private void showTitleDot() {
        mTitleBar.showLeftDot(PageNotifiManager.getNavNotifiCount(mPageInfo.getNavinfo().getLeft()), false);
        mTitleBar.showRightDot(PageNotifiManager.getNavNotifiCount(mPageInfo.getNavinfo().getRight()), false);
    }

    /**
     * 懒加载
     */
    private void lazyLoad() {
        if (getUserVisibleHint() && isPrepared && isFirst) {
            isFirst = false;
            mWebView.loadUrl(UiUtil.getWebUrl(mPageInfo));
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    @Override
    final public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            lazyLoad();
        }
    }

    public void doQRResult(int result, String callbackId, String content) {
        if (mWebView != null) {
            mWebView.setQRResult(result, callbackId, content);
        }
    }

    public boolean onBackPressed() {
        if (mWebView != null) {
            return mWebView.onBackPressed();
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mPageInfo = null;
    }
}
