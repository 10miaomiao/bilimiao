package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.HomeRegionInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/9/16.
 */
class HomeRegionItemAdapter(list: List<HomeRegionInfo>)
    : BaseQuickAdapter<HomeRegionInfo, BaseViewHolder>(R.layout.item_home_region, list) {

    override fun convert(helper: BaseViewHolder, item: HomeRegionInfo) {
        helper.setImageResource(R.id.item_icon, item.icon!!)
        helper.setText(R.id.item_title, item.name)
    }
    
}