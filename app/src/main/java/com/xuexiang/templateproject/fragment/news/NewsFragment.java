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

package com.xuexiang.templateproject.fragment.news;

import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.fragment.SettingsFragment;
import com.xuexiang.templateproject.fragment.news.jijin.CelueFragment;
import com.xuexiang.templateproject.fragment.news.jijin.HaiwaiFragment;
import com.xuexiang.templateproject.fragment.news.jijin.KuanjiFragment;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import butterknife.OnClick;

/**
 * @author xuexiang
 * @since 2019-10-30 00:15
 */
@Page(name = "指数基金（股票型和混合型）")
public class NewsFragment extends BaseFragment {

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

    }

    @SingleClick
    @OnClick({R.id.kuanjizhishu, R.id.celuezhishu, R.id.haiwaizhishu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.kuanjizhishu:
                PageOption.to(KuanjiFragment.class) //跳转的fragment
                        .setAnim(CoreAnim.present) //页面跳转动画
                        .setRequestCode(100) //请求码，用于返回结果
                        .setAddToBackStack(true) //是否加入堆栈
                        .setNewActivity(true) //是否使用新的Activity打开
//                        .putBoolean(DateReceiveFragment.KEY_IS_NEED_BACK, true) //传递的参数
                        .open(this); //打开页面进行跳转
                break;
            case R.id.celuezhishu:
                PageOption.to(CelueFragment.class) //跳转的fragment
                        .setAnim(CoreAnim.fade) //页面跳转动画
                        .setRequestCode(100) //请求码，用于返回结果
                        .setAddToBackStack(true) //是否加入堆栈
                        .setNewActivity(true) //是否使用新的Activity打开
//                        .putBoolean(DateReceiveFragment.KEY_IS_NEED_BACK, true) //传递的参数
                        .open(this); //打开页面进行跳转
                break;
            case R.id.haiwaizhishu:
                PageOption.to(HaiwaiFragment.class) //跳转的fragment
                        .setAnim(CoreAnim.zoom) //页面跳转动画
                        .setRequestCode(100) //请求码，用于返回结果
                        .setAddToBackStack(true) //是否加入堆栈
                        .setNewActivity(true) //是否使用新的Activity打开
//                        .putBoolean(DateReceiveFragment.KEY_IS_NEED_BACK, true) //传递的参数
                        .open(this); //打开页面进行跳转
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
