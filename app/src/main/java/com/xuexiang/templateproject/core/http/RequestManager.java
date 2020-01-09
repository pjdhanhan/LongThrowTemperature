package com.xuexiang.templateproject.core.http;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import android.webkit.WebSettings;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.xuexiang.templateproject.MyApp;
import com.xuexiang.templateproject.core.http.callback.ApiCallback;
import com.xuexiang.templateproject.core.http.callback.Callback;
import com.xuexiang.xaop.logger.XLogger;


import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * @author Crystal lee
 * @package com.vhd.guisdk.http RequestManager
 * @date 2018 08 01
 * @describe TODO
 */
public class RequestManager {
   private final static String TAG="RequestManager";
    private volatile static RequestManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private String headName="Content-Type";
    private String headValue="application/json";
    private static final String AUTHORIZATION = "Authorization";
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private RequestManager() {
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        mOkHttpClient = getUnsafeOkHttpClient().newBuilder()
                //超时设置，以防报异常
                .connectTimeout(2000000L, TimeUnit.MILLISECONDS)
                .readTimeout(2000000L, TimeUnit.MILLISECONDS)
                .writeTimeout(2000000L, TimeUnit.MILLISECONDS)
//                  .sslSocketFactory(createSSLSocketFactory())
//                .addInterceptor(loggingInterceptor)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                //缓存cookie
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
        mDelivery = new Handler(Looper.getMainLooper());
    }
    //okHttp3添加信任所有证书

    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public static RequestManager getInstance() {
        if (mInstance == null) {
            synchronized (RequestManager.class) {
                if (mInstance == null) {
                    mInstance = new RequestManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * get同步请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    private String _getSync(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .addHeader(headName,headValue)
                .build();
        Call call = mOkHttpClient.newCall(request);
        return call.execute().body().toString();
    }

    /**
     * get异步请求
     *
     * @param url
     * @param callback
     */
    private void _getAsync(String url, Callback callback) {
        final Request request = new Request.Builder()
                .removeHeader("User-Agent").addHeader("User-Agent",getUserAgent())
                .url(url)
                .addHeader(headName,headValue)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * delete异步请求
     *
     * @param url
     * @param callback
     */
    private void _deleteAsync(String url, Callback callback) {
        final Request request = new Request.Builder()
                .removeHeader("User-Agent").addHeader("User-Agent",getUserAgent())
                .url(url)
                .addHeader(headName,headValue)
                .delete()
                .build();
        deliveryResult(callback, request);
    }
    /**
     * put异步请求
     *
     * @param url
     * @param callback
     */
    private void _putJson(String url, Callback callback, JSONObject jsonObject) {
        Request request = buildPutRequest(url, jsonObject);
        deliveryResult(callback, request);
    }
    private String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(MyApp.getContext());
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    /**
     * 异步 JSON数据格式的post请求
     *
     * @param url
     * @param callback
     * @param jsonObject
     */
    private void _postJson(String url, final Callback callback, JSONObject jsonObject) {
        Request request = buildPostRequest(url, jsonObject);
        deliveryResult(callback, request);
    }


    /**
     * 异步 Map数据post 请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final Callback callback, Map<String, String> params, String token) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr, token);
        deliveryResult(callback, request);
    }

    /**
     * 异步 Map数据 post请求
     *
     * @param url
     * @param okHttpCallBack
     * @param params
     * @param token
     */
    private void _postObject(String url, ApiCallback okHttpCallBack, Map<String, String> params, String token) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr, token);
        deliveryResult(okHttpCallBack, request);
    }


    /**
     * 异步基于post的文件上传
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(String url, Callback callback, File[] files, String[] fileKeys, Map<String ,String> params) throws IOException {
        Param[] paramsArr = map2Params(params);
        Request request = buildMultipartFormRequest(url, files, fileKeys, paramsArr);
        deliveryResult(callback, request);
    }
//    private void _postAsyn(String url, Callback2 callback, File[] files, String[] fileKeys, Map<String ,String> params) throws IOException {
//        Param[] paramsArr = map2Params(params);
//        Request request = buildMultipartFormRequest(url, files, fileKeys, paramsArr,callback);
//        deliveryResult(callback, request);
//    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    private void _postAsyn(String url, Callback callback, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    private void _postAsyn(String url, Callback callback, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }


    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void _downloadAsyn(final String url, final String destFileDir, final Callback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                sendFailedStringCallback(call.request(), e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    sendSuccessStringCallback(file.getAbsolutePath(), callback);
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }

            }
        });
    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * 转换成Json Request
     *
     * @param url        请求url
     * @param jsonObject Json对象
     * @return request
     */
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    private Request buildPostRequest(String url, JSONObject jsonObject) {
        RequestBody requestBody = RequestBody.create(JSON, (jsonObject.toString()).replace("\\",""));
        return new Request
                .Builder()
                .removeHeader("User-Agent").addHeader("User-Agent",getUserAgent())
                .url(url)
                .addHeader(headName,headValue)
                .post(requestBody)
                .build();
    }
    private Request buildPutRequest(String url, JSONObject jsonObject) {
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
        return new Request
                .Builder()
                .addHeader(headName,headValue)
                .removeHeader("User-Agent").addHeader("User-Agent",getUserAgent())
                .url(url)
                .put(requestBody)
                .build();
    }


    /**
     * 解析键值对，转换成Request
     *
     * @param url    请求url
     * @param params 参数数组
     * @return Request
     */
    private Request buildPostRequest(String url, Param[] params, String token) {
        if (params == null) {
            params = new Param[0];
        }

        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .addHeader("token", token)
                .addHeader(headName,headValue)
                .url(url)
                .post(requestBody)
                .build();
    }


    /**
     * 网络请求对象回调
     *
     * @param okHttpCallBack 自定义callback
     * @param request        请求体
     */
    private void deliveryResult(ApiCallback okHttpCallBack, Request request) {
        mOkHttpClient.newCall(request).enqueue(okHttpCallBack);
    }

    /**
     * 网络处理
     *
     * @param callback 字符串回调
     * @param request  请求体
     */
    private void deliveryResult(final Callback callback, Request request) {
        mOkHttpClient.
                newCall(request)
                .enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                        sendFailedStringCallback(call.request(), e, callback);
                    }

                    @Override
                    public void onResponse(@NonNull final Call call, @NonNull final Response response) {
                        try {
//
                            sendSuccessStringCallback(response.body().string(), callback);
                        } catch (IOException e) {
                            sendFailedStringCallback(response.request(), e, callback);
                        }

                    }
                });
    }

//    /**
//     * 网络处理
//     *
//     * @param callback 字符串回调
//     * @param request  请求体
//     */
//    private void deliveryResult(final Callback2 callback, Request request) {
//        mOkHttpClient.
//                newCall(request)
//                .enqueue(new okhttp3.Callback() {
//                    @Override
//                    public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
//                        sendFailedStringCallback(call.request(), e, callback);
//                    }
//
//                    @Override
//                    public void onResponse(@NonNull final Call call, @NonNull final Response response) {
//                        try {
//
//                            sendSuccessStringCallback(response.body().string(), callback);
//                        } catch (IOException e) {
//                            sendFailedStringCallback(response.request(), e, callback);
//                        }
//
//                    }
//                });
//    }

    /**
     * 失败回调转UI线程
     *
     * @param request  返回请求
     * @param e        错误异常
     * @param callback 字符串回调
     */
    private void sendFailedStringCallback(final Request request, final IOException e, final Callback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onErrorResponse(e);
            }
        });
    }
//    private void sendFailedStringCallback(final Request request, final IOException e, final Callback2 callback) {
//        mDelivery.post(new Runnable() {
//            @Override
//            public void run() {
//                if (callback != null)
//                    callback.onErrorResponse(e);
//            }
//        });
//    }

    /**
     * 成功回调转UI线程
     *
     * @param string   成功返回字符串
     * @param callback 字符串回调
     */
    private void sendSuccessStringCallback(final String string, final Callback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    try {
                       XLogger.d(TAG+"返回："+string);
                        callback.onResponse(string);
                    } catch (Exception e) {
                        XLogger.d(TAG+""+e.toString());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

//    private void sendSuccessStringCallback(final String string, final Callback2 callback) {
//        mDelivery.post(new Runnable() {
//            @Override
//            public void run() {
//                if (callback != null) {
//                    try {
//                        callback.onResponse(string);
//                    } catch (Exception e) {
//                        LogUtil.logWrite(TAG,""+e.toString());
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }


    private Request buildMultipartFormRequest(String url, File[] files, String[] fileKeys, Param[] params) {
        params = validateParam(params);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""), fileBody);
            }
        }

        RequestBody requestBody =builder.build();



        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }
//    private Request buildMultipartFormRequest(String url, File[] files, String[] fileKeys, Param[] params,Callback2 callback2) {
//        params = validateParam(params);
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        builder.setType(MultipartBody.FORM);
//        for (Param param : params) {
//            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
//                    RequestBody.create(null, param.value));
//        }
//        if (files != null) {
//            RequestBody fileBody = null;
//            for (int i = 0; i < files.length; i++) {
//                File file = files[i];
//                String fileName = file.getName();
//                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
//                //TODO 根据文件名设置contentType
//                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""), fileBody);
//            }
//        }
//
//        RequestBody requestBody = ProgressHelper.withProgress(builder.build(), new ProgressUIListener() {
//
//            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
//            @Override
//            public void onUIProgressStart(long totalBytes) {
//                super.onUIProgressStart(totalBytes);
//                LogUtil.logWrite(TAG, "onUIProgressStart:" + totalBytes);
//            }
//
//            @Override
//            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
//                String info="数据:" + numBytes + " bytes" + "\n总大小:" + totalBytes + " bytes" + "\n进度:" + Math.round(percent * 100) + " %" + "\n速度:" + Math.round(speed * 1000 / 1024 / 1024) + "  MB/秒";
//                callback2.onProgress((int) (100 * percent),info);
////                uploadProgress.setProgress((int) (100 * percent));
////                uploadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + "  MB/秒");
//            }
//
//            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
//            @Override
//            public void onUIProgressFinish() {
//                super.onUIProgressFinish();
//                callback2.onFinish();
//                LogUtil.logWrite(TAG, "onUIProgressFinish:");
////                Toast.makeText(getApplicationContext(), "结束上传", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//
//
//
//        return new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//    }

    /**
     * 文件上传类型字符串
     *
     * @param path 文件路径
     * @return contentTypeFor
     */
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else
            return params;
    }

    /**
     * map集合转Param数组
     *
     * @param params map集合
     * @return 数组
     */
    private Param[] map2Params(Map<String, String> params) {
        if (params == null)
            return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    private class Param {
        private String key;
        private String value;

        private Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

    }
    /**
     * TODO============================================================================================================================================================================
     */
    /***
     * get同步请求，外部调用
     * @param url
     * @return
     */
    public static void getSyncRequest(String url) throws IOException {
        getInstance()._getSync(url);
    }

    /**
     * get异步请求，外部调用
     *
     * @param url
     * @param callback
     */
    public static void getAsyncRequest(String url, Callback callback,String value) {
       XLogger.d(""+url+value);
        getInstance()._getAsync(url+value, callback);
    }
    /**
     * delete异步请求，外部调用
     *
     * @param url
     * @param callback
     */
    public static void deleteAsyncRequest(String url, Callback callback,String value) {
        XLogger.d(""+url+value);
        getInstance()._deleteAsync(url+value, callback);
    }
    /**
     * put异步请求，外部调用
     *
     * @param url
     * @param callback
     */
    public static void putJson(String url, Callback callback, JSONObject jsonObject) {
        XLogger.d(url+"   "+jsonObject.toString());
        getInstance()._putJson(url, callback, jsonObject);
    }

    /**
     * Post 异步请求，外部调用
     *
     * @param url
     * @param callback
     * @param jsonObject
     */
    public static void postJson(String url, Callback callback, JSONObject jsonObject) {

        XLogger.d(url+"   "+jsonObject.toString());
        getInstance()._postJson(url, callback, jsonObject);
    }

    /**
     * 异步下载文件 外部调用
     *
     * @param url      下载地址
     * @param destDir  本地文件存储的文件夹
     * @param callback 数据回调
     */
    public static void downloadAsyn(String url, String destDir, Callback callback) {
        getInstance()._downloadAsyn(url, destDir, callback);
    }

    /**
     * 异步基于post的文件上传 外部调用
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @param params
     * @throws IOException
     */

    public static void postAsyn(String url, Callback callback, File[] files, String[] fileKeys, Map<String,String> params) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, params);
    }
//    public static void postAsyn(String url, Callback2 callback, File[] files, String[] fileKeys, Map<String,String> params) throws IOException {
//        getInstance()._postAsyn(url, callback, files, fileKeys, params);
//    }


    /**
     * 异步基于post的文件上传，单文件不带参数上传 外部调用
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    public static void postAsyn(String url, Callback callback, File file, String fileKey) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    public static void postAsyn(String url, Callback callback, File file, String fileKey,Param[] params) throws IOException {

        getInstance()._postAsyn(url, callback, file, fileKey, params);
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }


}
