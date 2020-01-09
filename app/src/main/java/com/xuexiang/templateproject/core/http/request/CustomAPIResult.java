package com.xuexiang.templateproject.core.http.request;

import com.xuexiang.xhttp2.model.ApiResult;

/**
 * created by pjdyu
 * on 2019/12/23
 * 何须浅碧深红色,自是花中第一流
 */
public class CustomAPIResult<T> extends ApiResult<T> {
    private int ErrCode;
    private String ErrMsg;
    private T Datas;
    public CustomAPIResult<T> setResult(T result) {
        this.Datas = result;
        return this;
    }
    public T getResult() {
        return Datas;
    }

    @Override
    public String toString() {
        return "CustomAPIResult{" +
                "ErrCode=" + ErrCode +
                ", ErrMsg='" + ErrMsg + '\'' +
                ", Datas=" + Datas +
                '}';
    }

    public int getErrCode() {
        return ErrCode;
    }

    public void setErrCode(int errCode) {
        ErrCode = errCode;
    }

    public String getErrMsg() {
        return ErrMsg;
    }

    public void setErrMsg(String errMsg) {
        ErrMsg = errMsg;
    }

    public T getDatas() {
        return Datas;
    }

    public void setDatas(T datas) {
        Datas = datas;
    }

    @Override
    public int getCode() {
        return ErrCode;
    }

    @Override
    public String getMsg() {
        return ErrMsg;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public T getData() {
        return Datas;
    }
}
