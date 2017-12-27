package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.SubmitVideosInfo
import com.a10miaomiao.bilimiao.utils.NumberUtil
import com.a10miaomiao.bilimiao.utils.NumberUtil.converCTime
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/10/30.
 */
class UpperVideoListAdapter(list : List<SubmitVideosInfo.VideoInfo>) :
        BaseQuickAdapter<SubmitVideosInfo.VideoInfo, BaseViewHolder>(R.layout.item_uper_video_list,list){
    override fun convert(helper: BaseViewHolder?, item: SubmitVideosInfo.VideoInfo?) {
        Glide.with(mContext)
                .load("http:${item?.pic}")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper?.getView(R.id.item_img))

        helper?.setText(R.id.item_title,item?.title)
        helper?.setText(R.id.item_play, NumberUtil.converString(item?.play!!))
        helper?.setText(R.id.item_review, NumberUtil.converString(item?.video_review!!))
        helper?.setText(R.id.item_ctime,converCTime(item?.created))
        helper?.setText(R.id.item_duration, item?.length)
    }

}