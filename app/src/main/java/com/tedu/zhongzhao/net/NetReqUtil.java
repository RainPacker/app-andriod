package com.tedu.zhongzhao.net;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.bean.UserInfo;
import com.tedu.base.encrypt.HMacMD5;
import com.tedu.zhongzhao.service.DNUserService;
import com.tedu.base.util.AndroidUtils;
import com.tedu.zhongzhao.utils.BeanUtil;
import com.tedu.zhongzhao.utils.DateUtil;
import com.tedu.zhongzhao.utils.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

/**
 * 网络请求封装
 * Created by huangyx on 2018/3/7.
 */
public class NetReqUtil {

    private static final String TAG = NetReqUtil.class.getSimpleName();

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    private NetReqUtil() {

    }

    /**
     * 基础header
     *
     * @return Map<String                                                                                                                               ,                                                                                                                                                                                                                                                               String>
     */
    public static Map<String, String> getBaseHeader() {
        Map<String, String> headers = null;
        String sessionId = "";
        String token = "";
        UserInfo usr = DNUserService.getInstance().getUser();
        if (usr != null) {
            headers = new HashMap<String, String>();
            if (!TextUtils.isEmpty(usr.getSessionId())) {
                sessionId = usr.getSessionId();
            }
            if (!TextUtils.isEmpty(usr.getToken())) {
                token = usr.getToken();
            }
            headers.put("JSESSIONID", sessionId);
            headers.put("token", token);
        }
        return headers;
    }

    private static OkHttpClient httpClient;
    private static Handler handler;

    /**
     * 初始化
     */
    synchronized private static void init() {
        if (httpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(HttpsUtil.createSSLSocketFactory());
            builder.hostnameVerifier(new HttpsUtil.TrustAllHttpsVerifier());
            builder.connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS);
            httpClient = builder.build();
        }
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
    }

    /**
     * 添加通用参数到header中
     *
     * @param builder Request.Builder
     */
    private static void addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (headers != null && builder != null) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> e : entries) {
                headers.put(e.getKey(), e.getValue());
            }
        }
    }

    /**
     * 发起GET请求
     *
     * @param url      请求的URL
     * @param reqData  请求参数
     * @param headers  header参数
     * @param callback 请求结果回调
     * @param clz      结果返回的对象class
     * @param <T>      泛类型
     */
    public static <T> void doGet(String url, RequestData reqData, Map<String, String> headers, final RequestCallback<T> callback, final Class<T> clz) {
        if (AndroidUtils.isOnline()) {
            init();
            if (reqData != null) {
                Map<String, Object> params = BeanUtil.bean2Map(reqData);
                StringBuilder tmpParams = new StringBuilder();
                Set<String> keys = params.keySet();
                int pos = 0;
                for (String k : keys) {
                    Object v = params.get(k);
                    // RequestData中基本字段类型基本为String,除了data，将data转换成json
                    if (v instanceof Map) {
                        v = JsonUtil.toJson(v);
                    }
                    if (v != null) {
                        if (pos > 0) {
                            tmpParams.append("&");
                        }
                        tmpParams.append(k).append("=").append(encodeValue(v.toString()));
                        pos++;
                    }
                }
                if (url.contains("?")) {
                    url += "&" + tmpParams.toString();
                } else {
                    url += "?" + tmpParams.toString();
                }
            }
            Request.Builder builder = new Request.Builder().url(url);
            addHeaders(builder, headers);
            Call call = httpClient.newCall(builder.build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    sendResult(callback, RequestConstants.RESULT_FAIL, null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (callback != null)
                        if (response.isSuccessful() && response.body() != null) {
                            String result = response.body().string();
                            T obj = JsonUtil.fromJson(result, clz);
                            sendResult(callback, RequestConstants.RESULT_OK, obj);
                        } else {
                            sendResult(callback, RequestConstants.RESULT_FAIL, "");
                        }
                }
            });
        } else {
            sendResult(callback, RequestConstants.RESULT_NET_ERROR, null);
        }
    }


    /**
     * 发起GET请求
     *
     * @param url      请求的URL
     * @param reqData  请求参数
     * @param postJson 参数是否以json形式提交
     * @param headers  header参数
     * @param callback 请求结果回调
     * @param clz      结果返回的对象class
     * @param <T>      泛类型
     */
    public static <T> void doPost(String url, RequestData reqData, boolean postJson, Map<String, String> headers, final RequestCallback<T> callback, final Class<T> clz) {
        if (AndroidUtils.isOnline()) {
            init();
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            if (reqData != null) {
                RequestBody body;
                if (postJson) {
                    String json = JsonUtil.toJson(reqData);
                    body = FormBody.create(JSON_TYPE, json);
                } else {
                    // 表单提交
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    Map<String, Object> params = BeanUtil.bean2Map(reqData);
                    Set<String> keys = params.keySet();
                    for (String k : keys) {
                        Object v = params.get(k);
                        if (v instanceof Map) {
                            v = JsonUtil.toJson(v);
                        }
                        if (v != null) {
                            formBuilder.add(k, v.toString());
                        }
                    }
                    body = formBuilder.build();
                }
                builder.post(body);
            }
            addHeaders(builder, headers);
            Call call = httpClient.newCall(builder.build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    sendResult(callback, RequestConstants.RESULT_FAIL, null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (callback != null)
                        if (response.isSuccessful() && response.body() != null) {
                            String result = response.body().string();
                            T obj = JsonUtil.fromJson(result, clz);
                            sendResult(callback, RequestConstants.RESULT_OK, obj);
                        } else {
                            sendResult(callback, RequestConstants.RESULT_FAIL, "");
                        }
                }
            });
        } else {
            sendResult(callback, RequestConstants.RESULT_NET_ERROR, null);
        }
    }

    /**
     * 下载
     *
     * @param url      下载Url
     * @param callback 下载回调
     */
    public static void download(String url, final DownloadCallback callback) {
        DownloadManager.getInstance().download(url, callback);
    }

    /**
     * 上传文件
     *
     * @param url      url
     * @param reqData  参数
     * @param headers  header参数
     * @param filePath 文件路径
     * @param callback 结果回调
     * @param <T>      泛类型
     */
    public static <T> void upload(String url, RequestData reqData, Map<String, String> headers, String filePath, final UploadCallback callback) {
        if (AndroidUtils.isOnline()) {
            init();
            MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Map<String, Object> params = BeanUtil.bean2Map(reqData);
            Set<String> keys = params.keySet();
            for (String k : keys) {
                Object v = params.get(k);
                if (v instanceof Map) {
                    v = JsonUtil.toJson(v);
                }
                if (v != null) {
                    body.addFormDataPart(k, v.toString());
                }
            }
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                if (file.isFile() && file.exists()) {
                    body.addFormDataPart("file", file.getName(), createUploadBody(RequestBody.create(MediaType.parse("application/octet-stream"), file), callback));
                }
            }
            Request.Builder builder = new Request.Builder().url(url).post(body.build());
            addHeaders(builder, headers);
            httpClient.newCall(builder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    sendResult(callback, RequestConstants.RESULT_FAIL, "");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        sendResult(callback, RequestConstants.RESULT_OK, result);
                    } else {
                        sendResult(callback, RequestConstants.RESULT_FAIL, "");
                    }
                }
            });
        } else {
            sendResult(callback, RequestConstants.RESULT_NET_ERROR, "");
        }
    }

    /**
     * 生成上传文件的RequestBody
     *
     * @param requestBody 要上传的RequestBody
     * @param callback    回调
     * @return RequestBody
     */
    private static RequestBody createUploadBody(final RequestBody requestBody, final UploadCallback callback) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() throws IOException {
                return requestBody.contentLength();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {

                ForwardingSink countBufferedSink = new ForwardingSink(sink) {
                    long writeCount = 0;
                    long lastTime = 0;

                    @Override
                    public void write(Buffer source, long byteCount) throws IOException {
                        super.write(source, byteCount);
                        long time = System.currentTimeMillis();
                        writeCount += byteCount;
                        if (time - lastTime >= 1000) {
                            lastTime = time;
                            sendUploadProgress(callback, writeCount, contentLength());
                        }
                    }
                };
                BufferedSink sink1 = Okio.buffer(countBufferedSink);
                requestBody.writeTo(sink1);
                sink1.flush();
            }
        };
    }

    /**
     * 主线程中回调上传结果
     *
     * @param callback  回调
     * @param completed 已上传的长度
     * @param length    总长度
     */
    private static void sendUploadProgress(final UploadCallback callback, final long completed, final long length) {
        if (handler != null && callback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onProgress(completed, length, length == completed);
                }
            });
        }
    }

    /**
     * 主线程中回调请求结果
     *
     * @param callback RequestCallback
     * @param code     结果标识
     * @param result   请求结果
     */
    private static <T> void sendResult(final RequestCallback callback, final int code, final T result) {
        if (handler != null && callback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(code, result);
                }
            });
        }
    }


    /**
     * URL编码
     *
     * @param value 要编码的内容
     * @return 编码后内容
     */
    private static String encodeValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        } else {
            String result;
            try {
                result = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                result = "";
            }
            return result;
        }
    }

    /**
     * 生成请求数据
     *
     * @param wid  界面ID
     * @param pid  父界面ID
     * @param data 业务数据
     * @param <T>  业务数据实体类型
     * @return RequestData<T>
     */
    public static <T> RequestData<T> createRequestData(String wid, String pid, T data) {
        RequestData<T> rd = new RequestData<T>();
        rd.setData(data);
        rd.setApp(WorkApplication.getApplication().getApp());
        rd.setCid(AndroidUtils.getUniqueId());
        rd.setTime(DateUtil.toDefaultDate(Calendar.getInstance().getTime()));
        rd.setVer(WorkApplication.getApplication().getVersion());
        rd.setUid(DNUserService.getInstance().getUid());
        rd.setPid(pid);
        rd.setWid(wid);
        String hmac = HMacMD5.getHmacMd5Str(JsonUtil.toJson(rd));
        rd.setHmac(hmac);
        return rd;
    }
}
