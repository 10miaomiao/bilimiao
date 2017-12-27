package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.UpperChannelVideoInfo
import com.a10miaomiao.bilimiao.utils.NumberUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/10/31.
 */
class UpperChannelVideoListAdapter(list : List<UpperChannelVideoInfo.VideoArchives>) :
        BaseQuickAdapter<UpperChannelVideoInfo.VideoArchives, BaseViewHolder>(R.layout.item_uper_video_list,list){
    override fun convert(helper: BaseViewHolder?, item: UpperChannelVideoInfo.VideoArchives?) {
        Glide.with(mContext)
                .load(item?.pic)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper?.getView(R.id.item_img))

        helper?.setText(R.id.item_title,item?.title)
        helper?.setText(R.id.item_play, NumberUtil.converString(item!!.stat.view))
        helper?.setText(R.id.item_review, NumberUtil.converString(item!!.stat.danmaku))
        helper?.setText(R.id.item_ctime, NumberUtil.converCTime(item?.pubdate))
        helper?.setText(R.id.item_duration, NumberUtil.converDuration(item!!.duration))
    }
}