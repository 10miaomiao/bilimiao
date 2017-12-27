package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.BangumiEpisodesInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/10/26.
 */
class BangumiEpisodesAdapter(list : List<BangumiEpisodesInfo>) :
        BaseQuickAdapter<BangumiEpisodesInfo, BaseViewHolder>(R.layout.item_bangumi_episodes,list){
    override fun convert(helper: BaseViewHolder?, item: BangumiEpisodesInfo?) {
        helper?.setText(R.id.tv_title,"${item?.index} ${item?.index_title}")
    }
}