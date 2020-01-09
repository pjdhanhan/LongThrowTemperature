package com.xuexiang.templateproject.core.http.callback;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Crystal lee
 * @package com.vhd.guisdk.http.callback ApiCallback
 * @date 2018 08 01
 * @describe TODO
 */
public abstract class ApiCallback<T> implements Callback {
    private static final String TAG = "HttpCallBack";
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private ApiBaseParser<T> mParser;


    public ApiCallback(ApiBaseParser<T> parser) {
        if (mParser == null) {
            throw new IllegalArgumentException("Parser can't be null");
        }
        mParser = parser;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        onFailure(call.request(), e);
    }

    private void onFailure(Request request, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: ==" + "Network connection failure");
                onErrorResponse(e);
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        onResponse(response);
    }

    private void onResponse(Response response) {

        final int code = mParser.getCode();
        try {
            T t = mParser.parseResponse(response);
            if (response.isSuccessful() && t != null) {

            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onErrorResponse(new Exception(Integer.toString(code)));
                    }
                });
            }
        } catch (final IOException e) {
            Log.e(TAG, "onResponse: " + e.getMessage());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onErrorResponse(e);
                }
            });
        }
    }

    public abstract void onErrorResponse(Throwable e);


}
