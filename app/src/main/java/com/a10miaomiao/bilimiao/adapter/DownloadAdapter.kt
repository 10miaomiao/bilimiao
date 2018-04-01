package com.a10miaomiao.bilimiao.adapter

import android.view.View
import android.widget.ProgressBar
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.R.id.item_duration
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.a10miaomiao.bilimiao.utils.NumberUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.text.DecimalFormat

/**
 * Created by 10喵喵 on 2018/1/11.
 */
class DownloadAdapter(list: List<DownloadInfo>) : BaseQuickAdapter<DownloadInfo, BaseViewHolder>(R.layout.item_download, list) {

    override fun convert(helper: BaseViewHolder?, item: DownloadInfo?) {
        helper?.setText(R.id.item_name,item!!.name)
        Glide.with(mContext)
                .load(item?.pic)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(helper?.getView(R.id.item_img))
        helper?.setText(item_duration,NumberUtil.converDuration((item!!.length / 1000).toInt()))
        when (item!!.status ){
            DownloadInfo.FAIL -> {
                helper?.setText(R.id.item_progress,"被搞坏了")
            }
            DownloadInfo.DOWNLOADING -> {
                val fnum = DecimalFormat("##0.00")
                var progress = (item!!.progress * 1.0 / item!!.size * 100.0)
                var progress_bar = helper?.getView<ProgressBar>(R.id.item_progress_bar)
                progress_bar?.visibility = View.VISIBLE
                progress_bar?.progress = progress.toInt()
//                helper?.setVisible(R.id.item_progress_bar, true)
//                helper?.setProgress(R.id.item_progress_bar, progress.toInt())
                if(item._count > 1){
                    helper?.setText(R.id.item_progress,"已下载${fnum.format(progress)}%(${item.making + 1}/${item._count})")
                }else{
                    helper?.setText(R.id.item_progress,"已下载${fnum.format(progress)}%")
                }
            }
            DownloadInfo.PAUSE -> {
                helper?.setText(R.id.item_progress,"暂停中")
            }
            DownloadInfo.FINISH -> {
                helper?.setText(R.id.item_progress,"已完成")
            }
            DownloadInfo.WAIT -> {
                helper?.setText(R.id.item_progress,"等待中")
            }
        }

    }
}