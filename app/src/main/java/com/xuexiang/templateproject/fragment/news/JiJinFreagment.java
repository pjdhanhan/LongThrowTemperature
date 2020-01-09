package com.xuexiang.templateproject.fragment.news;

import android.view.View;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.fragment.news.huobi.HuobiFragment;
import com.xuexiang.templateproject.fragment.news.jijin.CelueFragment;
import com.xuexiang.templateproject.fragment.news.jijin.HaiwaiFragment;
import com.xuexiang.templateproject.fragment.news.jijin.KuanjiFragment;
import com.xuexiang.templateproject.fragment.news.zhaiquan.ZhaiquanFragment;
import com.xuexiang.templateproject.utils.Utils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * created by pjdyu
 * on 2019/12/26
 * 何须浅碧深红色,自是花中第一流
 */
@Page(anim = CoreAnim.none)
public class JiJinFreagment extends BaseFragment {
    @BindView(R.id.about_list)
    XUIGroupListView mAboutGroupListView;
    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_jijin;
    }

    @Override
    protected void initViews() {
        XUIGroupListView.newSection(getContext())
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.jiji_huobi)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PageOption.to(HuobiFragment.class) //跳转的fragment
                                .setAnim(CoreAnim.present) //页面跳转动画
                                .setRequestCode(100) //请求码，用于返回结果
                                .setAddToBackStack(true) //是否加入堆栈
                                .setNewActivity(true) //是否使用新的Activity打开
//                        .putBoolean(DateReceiveFragment.KEY_IS_NEED_BACK, true) //传递的参数
                                .open(JiJinFreagment.this); //打开页面进行跳转

//                        Utils.goWeb(getContext(), "http://fund.eastmoney.com/data/hbxfundranking.html#t;c0;r;sSYL_Y;ddesc;pn50;mg;os1");
                    }
                })
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.jiji_zhaiquan)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PageOption.to(ZhaiquanFragment.class) //跳转的fragment
                                .setAnim(CoreAnim.present) //页面跳转动画
                                .setRequestCode(100) //请求码，用于返回结果
                                .setAddToBackStack(true) //是否加入堆栈
                                .setNewActivity(true) //是否使用新的Activity打开
//                        .putBoolean(DateReceiveFragment.KEY_IS_NEED_BACK, true) //传递的参数
                                .open(JiJinFreagment.this); //打开页面进行跳转
                    }
                })
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.jiji_zhishu)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PageOption.to(NewsFragment.class) //跳转的fragment
                                .setAnim(CoreAnim.zoom) //页面跳转动画
                                .setRequestCode(100) //请求码，用于返回结果
                                .setAddToBackStack(true) //是否加入堆栈
                                .setNewActivity(true) //是否使用新的Activity打开
//                        .putBoolean(DateReceiveFragment.KEY_IS_NEED_BACK, true) //传递的参数
                                .open(JiJinFreagment.this); //打开页面进行跳转
                    }
                })
                .addTo(mAboutGroupListView);

    }

}
