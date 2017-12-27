package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.SearchUpperInfo
import com.a10miaomiao.bilimiao.utils.NumberUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/11/8.
 */
class UpperResultAdapter(list : List<SearchUpperInfo.DataBean.ItemsBean>)
    : BaseQuickAdapter<SearchUpperInfo.DataBean.ItemsBean, BaseViewHolder>(R.layout.item_search_upper,list) {
    override fun convert(helper: BaseViewHolder?, item: SearchUpperInfo.DataBean.ItemsBean?) {
        Glide.with(mContext)
                .load(item?.cover)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper?.getView(R.id.item_avatar_view))

        helper?.setText(R.id.item_user_name,item?.title)
        helper?.setText(R.id.item_user_fans,"粉丝：" +NumberUtil.converString(item?.fans!!))
        helper?.setText(R.id.item_user_videos,"视频数：" +NumberUtil.converString(item?.archives!!))
        helper?.setText(R.id.item_user_details,item?.sign)

//        if(item?.finish == 1)//是否完结
//            helper?.setText(R.id.item_count, "${item?.newest_season}，${item?.total_count}话全")
//        else
//            helper?.setText(R.id.item_count, "${item?.newest_season}，更新至第${item?.total_count}话全")
//        helper?.setText(R.id.item_details, item?.cat_desc)
    }
}