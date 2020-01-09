package com.xuexiang.templateproject.entity.bean;

import com.xuexiang.xaop.logger.XLogger;

/**
 * created by pjdyu
 * on 2019/12/25
 * 何须浅碧深红色,自是花中第一流
 */
public class JijinInfo {

    /**
     * _id : 110003
     * CODE : 110003
     * NAME : 易方达上证50指数A
     * STOCKMARKET :
     */

    private String _id;
    private String CODE;
    private String NAME;
    private String STOCKMARKET;
    private String CreateDate;
    private String  Asset;

    private String  Genzongwucha;
    private String  Tonghangwucha;

    private String  guanli;
    private String  tuoguan;
    private String  shengou;

    public String getGuanli() {
        return guanli;
    }

    public void setGuanli(String guanli) {
        this.guanli = guanli;
    }

    public String getTuoguan() {
        return tuoguan;
    }

    public void setTuoguan(String tuoguan) {
        this.tuoguan = tuoguan;
    }

    public String getShengou() {
        return shengou;
    }

    public void setShengou(String shengou) {
        this.shengou = shengou;
    }

    public String getShuhui() {
        return shuhui;
    }

    public void setShuhui(String shuhui) {
        this.shuhui = shuhui;
    }

    private String  shuhui

            ;

    public String getGenzongwucha() {
        return Genzongwucha;
    }

    public void setGenzongwucha(String genzongwucha) {
        Genzongwucha = genzongwucha;
    }

    public String getTonghangwucha() {
        return Tonghangwucha;
    }

    public void setTonghangwucha(String tonghangwucha) {
        Tonghangwucha = tonghangwucha;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getAsset() {
        XLogger.d(Asset);
        if(Asset!=null){
            return   Asset.split("亿元")[0];
        }
        return "1000";
    }

    public void setAsset(String asset) {
        Asset = asset;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getSTOCKMARKET() {
        return STOCKMARKET;
    }

    public void setSTOCKMARKET(String STOCKMARKET) {
        this.STOCKMARKET = STOCKMARKET;
    }
}
