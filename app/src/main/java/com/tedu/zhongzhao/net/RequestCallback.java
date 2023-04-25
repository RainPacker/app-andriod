package com.tedu.zhongzhao.net;

/**
 * 网络请求回调
 * Created by huangyx on 2018/3/6.
 */
public interface RequestCallback<T> {

    void onResult(int code, T result);
}
