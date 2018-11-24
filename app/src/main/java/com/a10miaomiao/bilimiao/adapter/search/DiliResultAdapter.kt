package com.a10miaomiao.bilimiao.adapter.search

import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.SearchDiliInfo
import com.a10miaomiao.bilimiao.entity.SearchDiliInfo2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DiliResultAdapter(list : List<SearchDiliInfo>)
    : BaseQuickAdapter<SearchDiliInfo, BaseViewHolder>(R.layout.item_search_bangumi,list) {
    override fun convert(helper: BaseViewHolder, item: SearchDiliInfo) {
        Glide.with(mContext)
                .load(item.suoluetudizhi)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper.getView(R.id.item_img))

        helper.setText(R.id.item_title,item.typename)
        if (item.biaoqian.isEmpty())
            helper.setVisible(R.id.item_count, false)
        else{
            helper.setVisible(R.id.item_count, true)
            helper.setText(R.id.item_count, item.biaoqian)
        }
        helper.setText(R.id.item_details, item.description)
    }
}