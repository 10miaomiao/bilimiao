package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.RegionTypeDetailsInfo
import com.a10miaomiao.bilimiao.utils.NumberUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/9/19.
 */
class RegionTypeDetailsAdapter (list: List<RegionTypeDetailsInfo.Result>):
        BaseQuickAdapter<RegionTypeDetailsInfo.Result, BaseViewHolder>(R.layout.item_region_details,list){

    override fun convert(helper: BaseViewHolder?, item: RegionTypeDetailsInfo.Result?) {
//        itemViewHolder.mVideoTitle.setText(archiveBean.getTitle());
//        itemViewHolder.mVideoPlayNum.setText(converString(archiveBean.getPlay()));
//        itemViewHolder.mVideoReviewNum.setText(NumberUtil.converString(archiveBean.getVideo_review()));
//        itemViewHolder.mUserName.setText(archiveBean.getAuthor());
//        itemViewHolder.mDuration.setText(NumberUtil.converDuration(archiveBean.getDuration()));
//        mVideoPic = $(R.id.item_img);
//        mVideoTitle = $(R.id.item_title);
//        mVideoPlayNum = $(R.id.item_play);
//        mVideoReviewNum = $(R.id.item_review);
//        mUserName = $(R.id.item_user_name);
//        mDuration = $(R.id.item_duration);
        Glide.with(mContext)
                .load("http:${item?.pic}")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper?.getView(R.id.item_img))

        helper?.setText(R.id.item_title,item?.title)
        helper?.setText(R.id.item_play, converString(item?.play!!))
        helper?.setText(R.id.item_review,NumberUtil.converString(item?.video_review!!))
        helper?.setText(R.id.item_user_name,item?.author)
        helper?.setText(R.id.item_duration,NumberUtil.converDuration(item?.duration!!))
    }

    private fun converString(s: String): String {
        try {
            return NumberUtil.converString(s.toInt())
        } catch (e: NumberFormatException) {
            return s
        }
    }

}