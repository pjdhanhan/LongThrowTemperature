package com.xuexiang.templateproject.fragment.news.zhaiquan;

import androidx.appcompat.widget.AppCompatImageView;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;

import butterknife.BindView;

/**
 * created by pjdyu
 * on 2019/12/26
 * 何须浅碧深红色,自是花中第一流
 */
@Page(name = "纯债基金筛选")
public class ChunzhaiSelectFragment extends BaseFragment {
    @BindView(R.id.iv_content)
    AppCompatImageView ivContent;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_huobiselect;
    }

    @Override
    protected void initViews() {
  ivContent.setImageResource(R.mipmap.chunzhai);

    }
}
