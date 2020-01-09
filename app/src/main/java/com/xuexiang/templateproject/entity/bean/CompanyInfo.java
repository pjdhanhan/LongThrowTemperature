package com.xuexiang.templateproject.entity.bean;

/**
 * created by pjdyu
 * on 2019/12/23
 * 何须浅碧深红色,自是花中第一流
 */
public class CompanyInfo {
     private String CompanyName="";//公司名称缩写
    private String CompanyMoney="";//公司总价值

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getCompanyMoney() {
        return CompanyMoney;
    }

    public void setCompanyMoney(String companyMoney) {
        CompanyMoney = companyMoney;
    }
}
