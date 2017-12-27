package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.UpperChannelInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/10/29.
 */
class UpperChannelAdapter(list : List<UpperChannelInfo.UperChannelData>) :
    BaseQuickAdapter<UpperChannelInfo.UperChannelData, BaseViewHolder>(R.layout.item_uper_channel,list){
    override fun convert(helper: BaseViewHolder?, item: UpperChannelInfo.UperChannelData?) {
        helper?.setText(R.id.item_title,item!!.name)
        helper?.setText(R.id.item_count,"${item!!.count}个投稿")

        Glide.with(mContext)
                .load(item!!.cover)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper?.getView(R.id.item_img))
    }
}