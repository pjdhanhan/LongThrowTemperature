package com.xuexiang.templateproject.fragment.news.huobi;

import android.view.View;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.fragment.news.JiJinFreagment;
import com.xuexiang.templateproject.fragment.news.NewsFragment;
import com.xuexiang.templateproject.fragment.news.zhaiquan.ZhaiquanFragment;
import com.xuexiang.templateproject.utils.Utils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;

import butterknife.BindView;

/**
 * created by pjdyu
 * on 2019/12/26
 * 何须浅碧深红色,自是花中第一流
 */
@Page(name = "货币基金")
public class HuobiFragment extends BaseFragment {
    @BindView(R.id.about_list)
    XUIGroupListView mAboutGroupListView;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_jijin;
    }

    @Override
    protected void initViews() {
        XUIGroupListView.newSection(getContext())
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.huobi_list)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.goWeb(getContext(), "http://fund.eastmoney.com/data/hbxfundranking.html#t;c0;r;sSYL_Y;ddesc;pn50;mg;os1");
                    }
                })
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.huobi_select)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PageOption.to(HuobiSelectFragment.class) //跳转的fragment
                                .setAnim(CoreAnim.present) //页面跳转动画
                                .setRequestCode(100) //请求码，用于返回结果
                                .setAddToBackStack(true) //是否加入堆栈
                                .setNewActivity(true) //是否使用新的Activity打开
//                        .putBoolean(DateReceiveFragment.KEY_IS_NEED_BACK, true) //传递的参数
                                .open(HuobiFragment.this); //打开页面进行跳转
                    }
                })
                .addTo(mAboutGroupListView);
    }
}
