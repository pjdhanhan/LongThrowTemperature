package com.xuexiang.templateproject.core.http.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * @author Crystal lee
 * @package com.vhd.vhdgui.https.callback ApiBaseParser
 * @date 2018 07 20
 * @describe 使用策略设计模式，负责对返回值进行解析
 */
public abstract class ApiBaseParser<T> {
    protected int code;

    protected abstract T parse(Response response) throws IOException;

    T parseResponse(Response response) throws IOException {
        code = response.code();
        return parse(response);
    }

    public int getCode() {
        return code;
    }

}

