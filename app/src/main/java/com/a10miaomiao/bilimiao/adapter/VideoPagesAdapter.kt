package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.VideoDetailsInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/11/3.
 */
class VideoPagesAdapter(list : List<VideoDetailsInfo.VideoPageInfo>) :
        BaseQuickAdapter<VideoDetailsInfo.VideoPageInfo, BaseViewHolder>(R.layout.item_bangumi_episodes,list){
    override fun convert(helper: BaseViewHolder?, item: VideoDetailsInfo.VideoPageInfo?) {
        helper?.setText(R.id.tv_title, item?.title)
    }
}