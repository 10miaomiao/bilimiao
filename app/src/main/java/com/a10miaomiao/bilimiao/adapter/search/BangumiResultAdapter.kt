package com.a10miaomiao.bilimiao.adapter.search

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.SearchBangumiInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/11/8.
 */
class BangumiResultAdapter(list : List<SearchBangumiInfo.DataBean.ItemsBean>)
    : BaseQuickAdapter<SearchBangumiInfo.DataBean.ItemsBean, BaseViewHolder>(R.layout.item_search_bangumi,list) {
    override fun convert(helper: BaseViewHolder?, item: SearchBangumiInfo.DataBean.ItemsBean?) {
        Glide.with(mContext)
                .load(item?.cover)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper?.getView(R.id.item_img))

        helper?.setText(R.id.item_title,item?.title)
        if(item?.finish == 1)//是否完结
            helper?.setText(R.id.item_count, "${item?.newest_season}，${item?.total_count}话全")
        else
            helper?.setText(R.id.item_count, "${item?.newest_season}，更新至第${item?.total_count}话全")
        helper?.setText(R.id.item_details, item?.cat_desc)

    }
}