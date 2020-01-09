package com.xuexiang.templateproject.core.http.callback;

import org.json.JSONException;

import java.io.IOException;

/**
 * @author Crystal lee
 * @package com.vhd.guisdk.http.inter Callback
 * @date 2018 08 01
 * @describe T字符串回调接口
 */
public interface Callback {
    void onResponse(Object response) throws JSONException;
    void onErrorResponse(IOException e);
}
