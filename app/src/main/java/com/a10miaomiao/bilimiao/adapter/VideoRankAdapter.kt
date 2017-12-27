package com.a10miaomiao.bilimiao.adapter

import android.widget.TextView
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.VideoRankInfo
import com.a10miaomiao.bilimiao.utils.NumberUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/12/2.
 */
class VideoRankAdapter(list: List<VideoRankInfo.VideoInfo>)
    : BaseQuickAdapter<VideoRankInfo.VideoInfo, BaseViewHolder>(R.layout.item_rank_video, list) {
    override fun convert(helper: BaseViewHolder?, item: VideoRankInfo.VideoInfo?) {
        Glide.with(mContext)
                .load(item?.pic)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper?.getView(R.id.item_img))

        helper?.setText(R.id.item_title, item?.title)
//        helper?.setText(R.id.item_play, converString(item?.play!!))
//        helper?.setText(R.id.item_review, NumberUtil.converString(item?.video_review!!))
        helper?.setText(R.id.item_user_name, item?.author)
        helper?.setText(R.id.item_duration, item?.duration!!)
        var sort_num = helper!!.getView<TextView>(R.id.item_sort_num)
        sort_num.text = item!!.sort_num.toString()
        if (item!!.sort_num <= 3)
            sort_num.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        else
            sort_num.setTextColor(mContext.resources.getColor(R.color.text_black))
        helper?.setText(R.id.item_pts, "综合评分：${item!!.pts}")
    }

    private fun converString(s: String): String {
        try {
            return NumberUtil.converString(s.toInt())
        } catch (e: NumberFormatException) {
            return s
        }
    }
}