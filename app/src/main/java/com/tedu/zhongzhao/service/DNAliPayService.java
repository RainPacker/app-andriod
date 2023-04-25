package com.tedu.zhongzhao.service;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.tedu.zhongzhao.pay.ali.OrderInfoUtil2_0;
import com.tedu.zhongzhao.pay.ali.PayResult;
import com.tedu.base.util.LogUtil;
import com.tedu.zhongzhao.web.ActionCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 支付宝支付
 * Created by huangyx on 2018/5/16.
 */
public class DNAliPayService extends BaseService {

    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2014100900013222";

    private static DNAliPayService sInstance;

    synchronized public static DNAliPayService getInstance() {
        if (sInstance == null) {
            sInstance = new DNAliPayService();
        }
        return sInstance;
    }

    /**
     * 调起支付宝进行支付
     *
     * @param callback 回调
     * @param context  Context
     * @param params   参数
     */
    public void doPayWithKey_body_subject_orderId_amount_(final ActionCallback callback, final Context context, Map<String, String> params) {
        // TODO 获取支付定单参数 -- 服务端完整生成
        if (params != null && !params.isEmpty()) {
            String key = params.get("key");
            String body = params.get("body");
            String subject = params.get("subject");
            String orderId = params.get("orderId");
            String amount = params.get("amount");
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(body)
                    && !TextUtils.isEmpty(subject) && !TextUtils.isEmpty(orderId) && !TextUtils.isEmpty(amount)) {
                // 封装订单参数
                Map<String, String> keyValues = OrderInfoUtil2_0.buildOrderParamMap(APPID, subject, body, amount, orderId);
                String orderParam = buildOrderParam(keyValues);
                String sign = OrderInfoUtil2_0.getSign(keyValues, key);
                final String orderInfo = orderParam + "&" + sign;
                new AsyncTask<Void, Void, Map<String, String>>() {

                    @Override
                    protected Map<String, String> doInBackground(Void[] params) {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask((Activity) context);
                        LogUtil.d("PayManager", "order info:" + orderInfo);
                        if (!TextUtils.isEmpty(orderInfo)) {
                            return alipay.payV2(orderInfo, true);
                        }
                        return null;

                    }

                    @Override
                    protected void onPostExecute(Map<String, String> result) {
                        super.onPostExecute(result);
                        if (result != null) {
                            PayResult payResult = new PayResult(result);
                            /**
                             对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                             */
                            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                            String resultStatus = payResult.getResultStatus();
                            // 判断resultStatus 为9000则代表支付成功
                            if (TextUtils.equals(resultStatus, "9000")) {
                                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                                sendSuccess(callback, "true");
                            } else {
                                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                                sendSuccess(callback, "false");
                            }
                        } else {
                            sendSuccess(callback, "false");
                        }
                    }
                }.execute();
                return;
            }
        }
        sendSuccess(callback, "false");
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }


    /**
     * 构造支付订单参数信息
     *
     * @param map 支付订单参数
     * @return
     */
    public String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<String>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }
}
