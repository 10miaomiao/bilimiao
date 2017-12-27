package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.DanmakuInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/11/1.
 */
class DanmakuAdapter(list: List<DanmakuInfo.DanmakuItem>) :
        BaseQuickAdapter<DanmakuInfo.DanmakuItem, BaseViewHolder>(R.layout.item_danmakiu,list) {
    override fun convert(helper: BaseViewHolder?, item: DanmakuInfo.DanmakuItem?) {
        helper?.setText(R.id.item_text,item!!.text)
        helper?.setText(R.id.item_mode,item!!.modeStr)
        helper?.setText(R.id.item_time,item!!.timeStr)
    }
}