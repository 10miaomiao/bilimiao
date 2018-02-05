package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.text.DecimalFormat

/**
 * Created by 10喵喵 on 2018/1/11.
 */
class DownloadAdapter(list: List<DownloadInfo>) : BaseQuickAdapter<DownloadInfo, BaseViewHolder>(R.layout.item_download, list) {

    override fun convert(helper: BaseViewHolder?, item: DownloadInfo?) {
        helper?.setText(R.id.item_name,item!!.name)
        when (item!!.status ){
            DownloadInfo.FAIL -> {
                helper?.setText(R.id.item_progress,"被搞坏了")
            }
            DownloadInfo.DOWNLOADING -> {
                val fnum = DecimalFormat("##0.00")
                var progress = (item!!.progress * 1.0 / item!!.size * 100.0)
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