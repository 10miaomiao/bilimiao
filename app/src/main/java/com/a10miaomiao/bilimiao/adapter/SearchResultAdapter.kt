package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.SearchArchiveInfo
import com.a10miaomiao.bilimiao.utils.NumberUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/11/7.
 */
class SearchResultAdapter(list : List<SearchArchiveInfo.DataBean.ItemsBean.ArchiveBean>)
    : BaseQuickAdapter<SearchArchiveInfo.DataBean.ItemsBean.ArchiveBean, BaseViewHolder>(R.layout.item_region_details,list) {
    override fun convert(helper: BaseViewHolder?, item: SearchArchiveInfo.DataBean.ItemsBean.ArchiveBean?) {
        Glide.with(mContext)
                .load(item?.cover)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper?.getView(R.id.item_img))

        helper?.setText(R.id.item_title,item?.title)
        helper?.setText(R.id.item_play, NumberUtil.converString(item?.play!!))
        helper?.setText(R.id.item_review, NumberUtil.converString(item?.danmaku!!))
        helper?.setText(R.id.item_user_name,item?.author)
        helper?.setText(R.id.item_duration, item?.duration!!)
    }
}