package com.xuexiang.templateproject.adapter;

import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.entity.bean.JijinInfo;
import com.xuexiang.xui.utils.ResUtils;

import java.util.Collection;

/**
 * 基于simple_list_item_2简单的适配器
 *
 * @author XUE
 * @since 2019/4/1 11:04
 */
public class JiJinRecyclerAdapter extends SmartRecyclerAdapter<JijinInfo> {

    public JiJinRecyclerAdapter() {
        super(R.layout.item_kuanji);
    }

    public JiJinRecyclerAdapter(Collection<JijinInfo> data) {
        super(data,R.layout.item_kuanji);
    }

    /**
     * 绑定布局控件
     *
     * @param holder
     * @param model
     * @param position
     */
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, JijinInfo model, int position) {
        holder.text(R.id.jijinCode, model.getCODE());
        holder.text(R.id.jijinName, model.getNAME());
        holder.text(R.id.genzongwucha, model.getGenzongwucha());
        holder.text(R.id.tonghangwucha, model.getTonghangwucha());
        holder.text(R.id.guanli, model.getGuanli());
        holder.text(R.id.tuoguan, model.getTuoguan());
        holder.text(R.id.shengou, model.getShengou());
        holder.text(R.id.shuhui, model.getShuhui());
        holder.text(R.id.zichanguimo, model.getAsset()+"亿元");
        holder.text(R.id.chengliriqi, model.getCreateDate());
        holder.textColorId(android.R.id.text2, R.color.xui_config_color_light_blue_gray);
    }
}
