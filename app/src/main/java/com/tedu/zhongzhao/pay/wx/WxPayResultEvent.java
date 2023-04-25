package com.tedu.zhongzhao.pay.wx;

/**
 * 微信支付回调Event
 * Created by huangyx on 2018/5/16.
 */
public class WxPayResultEvent {

    private int resultCode;
    private String resultString;

    public WxPayResultEvent(int resultCode, String resultString) {
        this.resultCode = resultCode;
        this.resultString = resultString;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getResultString() {
        return resultString;
    }
}
