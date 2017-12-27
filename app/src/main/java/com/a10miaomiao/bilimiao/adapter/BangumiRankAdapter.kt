package com.a10miaomiao.bilimiao.adapter

import android.widget.TextView
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.BangumiRankInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/12/2.
 */
class BangumiRankAdapter(list : List<BangumiRankInfo.BangumiInfo>)
    : BaseQuickAdapter<BangumiRankInfo.BangumiInfo, BaseViewHolder>(R.layout.item_rank_bangumi,list) {
    override fun convert(helper: BaseViewHolder?, item: BangumiRankInfo.BangumiInfo?) {
        Glide.with(mContext)
                .load(item?.cover)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper?.getView(R.id.item_img))

        helper?.setText(R.id.item_title,item?.title)
        if(item?.is_finish == 1)//是否完结
            helper?.setText(R.id.item_count, "${item?.newest_ep_index}话全")
        else
            helper?.setText(R.id.item_count, "连载中，更新至第${item?.newest_ep_index}话全")
        helper?.setText(R.id.item_details, "综合评分：${item?.pts}")

        var sort_num = helper!!.getView<TextView>(R.id.item_sort_num)
        sort_num.text = item!!.sort_num.toString()
        if (item!!.sort_num <= 3)
            sort_num.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        else
            sort_num.setTextColor(mContext.resources.getColor(R.color.text_black))


    }
}