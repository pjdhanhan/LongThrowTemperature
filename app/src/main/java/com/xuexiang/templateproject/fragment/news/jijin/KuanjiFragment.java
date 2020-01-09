/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.templateproject.fragment.news.jijin;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import com.xuexiang.constant.TimeConstants;
import com.xuexiang.rxutil2.rxjava.DisposablePool;
import com.xuexiang.rxutil2.rxjava.RxJavaUtils;
import com.xuexiang.templateproject.DemoDataProvider;
import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.adapter.JiJinRecyclerAdapter;
import com.xuexiang.templateproject.adapter.SimpleRecyclerAdapter;
import com.xuexiang.templateproject.adapter.dropdownmenu.ListDropDownAdapter;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.core.http.API;
import com.xuexiang.templateproject.core.http.RequestManager;
import com.xuexiang.templateproject.core.http.TestApi;
import com.xuexiang.templateproject.core.http.callback.Callback;
import com.xuexiang.templateproject.core.http.callback.TipCallBack;
import com.xuexiang.templateproject.core.http.request.CustomAPIResult;
import com.xuexiang.templateproject.core.http.subscriber.TipRequestSubscriber;
import com.xuexiang.templateproject.core.webview.AgentWebActivity;
import com.xuexiang.templateproject.entity.bean.CompanyInfo;
import com.xuexiang.templateproject.entity.bean.JijinInfo;
import com.xuexiang.templateproject.widget.CustomRefreshFooter;
import com.xuexiang.templateproject.widget.CustomRefreshHeader;
import com.xuexiang.xaop.annotation.IOThread;
import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xaop.enums.ThreadType;
import com.xuexiang.xaop.logger.XLogger;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.XHttpProxy;
import com.xuexiang.xhttp2.callback.CallBack;
import com.xuexiang.xhttp2.callback.CallBackProxy;
import com.xuexiang.xhttp2.callback.CallClazzProxy;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.subsciber.ProgressLoadingSubscriber;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xui.widget.spinner.DropDownMenu;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.app.AppUtils;
import com.xuexiang.xutil.data.DateUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import butterknife.BindView;
import okhttp3.ResponseBody;

/**
 * @author pjdyu
 * @since 2019-10-30 00:02
 */
@Page(name = "宽基指数")
public class KuanjiFragment extends BaseFragment {

//    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
//    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;


    @BindView(R.id.ddm_content)
    DropDownMenu mDropDownMenu;

    private String[] mHeaders = {"宽基类型"};//{"城市", "年龄", "性别", "星座"};
    private List<View> mPopupViews = new ArrayList<>();

//    private CityDropDownAdapter mCityAdapter;
//    private ListDropDownAdapter mAgeAdapter;
    private ListDropDownAdapter mKuanjiAdapter;
//    private ConstellationAdapter mConstellationAdapter;
    private String[] mKuanjis;
    private String kuanji="上证50";
    View view;




    private JiJinRecyclerAdapter mAdapter;

    private CustomRefreshHeader mRefreshHeader;
    private List<CompanyInfo> companys=new ArrayList<>();
   private List<JijinInfo> jijins=new ArrayList<>();

    private int progress = 0;
    Document doc;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_kuanji_wai;
    }

    @Override
    protected void initViews() {
         view= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_kuanji,null);
        mRecyclerView=view.findViewById(R.id.recyclerView);
        mRefreshLayout=view.findViewById(R.id.refreshLayout);
        WidgetUtils.initRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter = new JiJinRecyclerAdapter());

        mRefreshLayout.setRefreshHeader(mRefreshHeader = new CustomRefreshHeader(getContext()));

        mRefreshLayout.setRefreshFooter(new CustomRefreshFooter(getContext()));
        //init kuanji menu
        final ListView kuanjiView = new ListView(getContext());
        kuanjiView.setDividerHeight(0);
        mKuanjis = ResUtils.getStringArray(R.array.kuanji_entry);
        mKuanjiAdapter = new ListDropDownAdapter(getContext(), mKuanjis);
        kuanjiView.setAdapter(mKuanjiAdapter);
        kuanjiView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mKuanjiAdapter.setSelectPosition(position);
                mDropDownMenu.setTabMenuText( mKuanjis[position]);
                mDropDownMenu.closeMenu();
                kuanji=mKuanjis[position];
              mRefreshLayout.autoRefresh();
            }
        });
        mPopupViews.add(kuanjiView);
        mDropDownMenu.setDropDownMenu(mHeaders, mPopupViews, view);
        getJiCompareRak();

    }


    /**
     * 1,筛选出基金公司排名，超过一亿的；
     * 2，筛选出可过滤的上证50，中证500，沪深300，创业板指数
     * 3，再筛选基金规模高于两亿，成立年限高于3年
     * 4，少于同行误差
     * 5，计算长投温度
     */
    @IOThread(ThreadType.Single)
    private void getDatas(String type) {

        jijins.clear();
        getClassfiy(type);

    }


    /**
     * 2，筛选出可过滤的上证50，中证500，沪深300，创业板指数
     */
    private void getClassfiy(String type) {
        XHttp.get(String.format(API.jijinSearch,type))
                .execute(new CallBackProxy<CustomAPIResult<List<JijinInfo>>, List<JijinInfo>>(new TipCallBack<List<JijinInfo>>() {
                    @Override
                    public void onSuccess(List<JijinInfo> response) throws Throwable {
                       for(int i=0;i<companys.size();i++){
                           for(int j=0;j<response.size();j++){
                               if(companys.get(i).getCompanyName().split("基金")[0].equals(response.get(j).getNAME().split(type)[0])){
                                   jijins.add(response.get(j));
                                   break;
                               }
                           }

                       }
                       getJijinDetails();


                    }
                }){});  //千万注意，这里的{}一定不能去掉，否则解析错误


    }

    /**
     * 3，查看单个基金的详细情况（筛选出基金规模高于2亿的，成立年限高于3年的）
     */
    @IOThread(ThreadType.Single)
    private void getJijinDetails() {
        try {
            for(int i=0;i<jijins.size();i++){
                doc = Jsoup.connect(String.format(API.jijinDetails,jijins.get(i).getCODE()))
                        .data("query", "Java")
                        .userAgent("Mozilla")
                        .cookie("auth", "token")
                        .timeout(3000)
                        .post();
                XLogger.d("doc.nodeName()"+doc.nodeName());
                Element body = doc.body();
                Element div=body.select("div.bs_gl").first();
               Elements labels= div.select("label");
                 for(int j=0;j<labels.size();j++){
                     if(labels.get(j).text().contains("成立日期")){
                         jijins.get(i).setCreateDate(labels.get(j).select("span").text());
                     }
                     if(labels.get(j).text().contains("资产规模")){
                         jijins.get(i).setAsset(labels.get(j).select("span").text());
                     }
                 }
                Elements tr=body.select("div.txt_in").first().select("table").select("tr");
                 for(int j=0;j<tr.size();j++){
                     if(tr.get(j).select("th").first().text().equals("管理费率")){
                             jijins.get(i).setGuanli(tr.get(j).select("td").get(0).text());
                         jijins.get(i).setTuoguan(tr.get(j).select("td").get(1).text());
                     }
                     if(tr.get(j).select("th").first().text().equals("最高申购费率")){
                         jijins.get(i).setShengou(tr.get(j).select("td").get(0).text());
                         jijins.get(i).setShuhui(tr.get(j).select("td").get(1).text());
                     }
                 }

            }

            getGenzongwucha();
            List<JijinInfo> temp=new ArrayList<>();
            for(int i=0;i<jijins.size();i++){
                if ((DateUtils.getTimeSpan(DateUtils.string2Date(jijins.get(i).getCreateDate(), DateUtils.yyyyMMdd.get()), DateUtils.getNowDate(), TimeConstants.DAY)/365)>3
                        &&Float.parseFloat(jijins.get(i).getAsset()) > 2
                        &&Float.parseFloat(jijins.get(i).getGenzongwucha().split("%")[0])<=Float.parseFloat(jijins.get(i).getTonghangwucha().split("%")[0])
                        &&!jijins.get(i).getNAME().contains("联接")
                        &&!jijins.get(i).getNAME().contains("LOF")
                        &&!jijins.get(i).getNAME().contains("增强")){
                    temp.add(jijins.get(i));
                }

            }
            jijins.clear();
            jijins.addAll(temp);
          setData();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void getGenzongwucha() {
        try {
            for(int i=0;i<jijins.size();i++){
                doc = Jsoup.connect(String.format(API.jijinGenzongwucha,jijins.get(i).getCODE()))
                        .data("query", "Java")
                        .userAgent("Mozilla")
                        .cookie("auth", "token")
                        .timeout(3000)
                        .post();
                XLogger.d("doc.nodeName()"+doc.nodeName());
                Element body = doc.body();
                Element table=body.getElementById("jjzsfj").select("table.fxtb").last();
                Elements td= table.select("tr").select("td");
                String genzongwucha=td.get(1).text();
                String tonghangwucha=td.get(2).text();
                jijins.get(i).setGenzongwucha(genzongwucha);
                jijins.get(i).setTonghangwucha(tonghangwucha);
//               22qqqqqqqqqqqqqqqqqqqqqqq

            }
            List<JijinInfo> temp=new ArrayList<>();
            for(int i=0;i<jijins.size();i++){
                if ((DateUtils.getTimeSpan(DateUtils.string2Date(jijins.get(i).getCreateDate(), DateUtils.yyyyMMdd.get()), DateUtils.getNowDate(), TimeConstants.DAY)/365)>3&&Float.parseFloat(jijins.get(i).getAsset()) > 2){
                    temp.add(jijins.get(i));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @MainThread
    private void setData() {
        mAdapter.refresh(jijins);
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();

    }


    /**
     * 1,筛选出基金公司排名，超过一亿的；
     */
    @IOThread(ThreadType.Single)
    private void getJiCompareRak() {

        try {
            doc = Jsoup.connect(API.CompareRak)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .post();
        } catch (IOException e) {
            e.printStackTrace();
        }
        XLogger.d("doc.nodeName()"+doc.nodeName());
        Elements conpanys = doc.getElementsByTag("tr");

        for (Element conpay : conpanys) {
            Elements trS = conpay.getElementsByTag("td");
            CompanyInfo companyInfo=new CompanyInfo();
            for (Element td : trS){
                if(td.hasClass("td-align-left")){
                    String   conpanyName=td.text();//td.attr("data-sortvalue");
                    companyInfo.setCompanyName(conpanyName);
                }
                if(td.hasClass("scale number ")){
                    String  companyMoney=td.attr("data-sortvalue");
                    companyInfo.setCompanyMoney(companyMoney);
                }
            }
            if (!TextUtils.isEmpty(companyInfo.getCompanyMoney())&&Float.parseFloat(companyInfo.getCompanyMoney())>1000){//有错误int转化补鞥呢直接有float转为int
                companys.add(companyInfo);
            }

        }
    }


    @Override
    protected void initListeners() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                handleRefresh();
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDatas(kuanji);
//                        mAdapter.refresh(jijins);
//                        refreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });
        mRefreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
        mRefreshLayout.setEnableLoadMore(false);

    }


    private void handleRefresh() {
        getDatas(kuanji);
//        getDatas();
//        progress = 0;
//        DisposablePool.get().add(RxJavaUtils.polling(0, 50, TimeUnit.MILLISECONDS)
//                .subscribe(aLong -> {
//                    if (progress <= 100) {
//                        updateProgress(progress++);
//                    } else {
//                        mAdapter.refresh(jijins);
//                        if (mRefreshLayout != null) {
//                            mRefreshLayout.finishRefresh(true);
//                        }
//
//                        DisposablePool.get().remove("refresh_polling");
//                    }
//
//                }), "refresh_polling");
    }

    @MainThread
    private void updateProgress(int progress) {
        if (mRefreshHeader != null) {
            mRefreshHeader.refreshMessage("正在同步数据（" + progress + "%）");
        }
    }

    @Override
    public void onDestroyView() {
        DisposablePool.get().remove("refresh_polling");
        super.onDestroyView();
    }

}
