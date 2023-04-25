package com.tedu.zhongzhao.service;

import android.content.Context;

import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.pay.wx.WxConstants;
import com.tedu.zhongzhao.pay.wx.WxPayResultEvent;
import com.tedu.base.util.LogUtil;
import com.tedu.zhongzhao.web.ActionCallback;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 微信支付
 * Created by huangyx on 2018/5/16.
 */
public class WeiXinPayService extends BaseService {

    private static WeiXinPayService sInstance;

    synchronized public static WeiXinPayService getInstance() {
        if (sInstance == null) {
            sInstance = new WeiXinPayService();
        }
        return sInstance;
    }

    private ActionCallback mCallback;

    public void doWxPayWithSHID_andPreayId_andNonceStr_(ActionCallback callback, Context context, Map<String, String> params) {
        // TODO
        if (params != null && params.containsKey("partnerid") && params.containsKey("preayId") && params.containsKey("nonceStr")
                /*&& params.containsKey("timeStamp") && params.containsKey("sign")*/) {
            this.mCallback = callback;
            String partnerId = params.get("partnerid");
            String prepayId = params.get("preayId");
            String nonceStr = params.get("nonceStr");
//            String timeStamp = params.get("timeStamp");
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String sign = params.get("sign");
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            IWXAPI api = WXAPIFactory.createWXAPI(context, WorkApplication.getApplication().getWxAppId());
            PayReq request = new PayReq();
            request.appId = WorkApplication.getApplication().getWxAppId();
            request.partnerId = partnerId;
            request.prepayId = prepayId;
            request.packageValue = "Sign=WXPay";
            request.nonceStr = nonceStr;
            request.timeStamp = timeStamp;
            request.sign = sign;
            if (api.sendReq(request)) {
//                sendSuccess(callback, "true");
            }
        } else {
            this.mCallback = null;
            sendSuccess(callback, "false");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThreadEvent(WxPayResultEvent event) {
        if (mCallback != null) {
            if (event.getResultCode() == WxConstants.PAY_SUCC) {
                sendSuccess(mCallback, "支付成功");
            } else if (event.getResultCode() == WxConstants.PAY_CANCEL) {
                sendCancel(mCallback);
            } else {
                sendSuccess(mCallback, "false");
            }
        }
        EventBus.getDefault().unregister(this);
    }
}
