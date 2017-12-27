package com.a10miaomiao.bilimiao.adapter

import com.a10miaomiao.bilimiao.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/10/24.
 */
class EditPreventKeywordAdapter(list: List<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_prevent_keyword,list) {
    override fun convert(helper: BaseViewHolder?, item: String?) {
        helper?.setText(R.id.tv_keyword, item)
    }
}