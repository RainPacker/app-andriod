package com.tedu.zhongzhao.net;

import java.io.Serializable;

/**
 * 网络请求响应结果
 * Created by huangyx on 2018/3/7.
 */
public class ResponseData<T> implements Serializable {
    /**
     * 逻辑名
     */
    private String method;
    /**
     * 错误码
     */
    private String code;
    /**
     * 错误信息
     */
    private String msg;
    /**
     * 访问令牌
     */
    private String token;
    /**
     * 验证码
     */
    private String hmac;
    /**
     * 业务数据
     */
    private T data;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
