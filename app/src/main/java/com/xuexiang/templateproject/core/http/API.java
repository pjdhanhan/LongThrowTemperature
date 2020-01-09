package com.xuexiang.templateproject.core.http;

/**
 * created by pjdyu
 * on 2019/12/23
 * 何须浅碧深红色,自是花中第一流
 */
public class API {

    public static String CompareRak="http://fund.eastmoney.com/Company/home/gspmlist?fundType=0";//1，查看基金排名 返回网页（筛选出1千亿以上的）
    public static String jijinSearch="http://fundsuggest.eastmoney.com/FundSearch/api/FundSearchPageAPI.ashx?&key=%s&pageindex=0&pagesize=10000&m=1"; //搜索上证50的基金（筛选出公司属于1中的）
    public static String jijinDetails="http://fund.eastmoney.com/f10/jbgk_%s.html"; //搜索上证50的基金（筛选出公司属于1中的）
    public static String jijinGenzongwucha="http://fundf10.eastmoney.com/tsdata_%s.html"; //获取某只基金的跟踪误差
    public static String huobijijin="http://api.fund.eastmoney.com/FundRank/GetHbRankList?intCompany=0&MinsgType=a&IsSale=1&strSortCol=Dwjz&orderType=desc&pageIndex=1&pageSize=50"; //获取货币基金排名//
}
