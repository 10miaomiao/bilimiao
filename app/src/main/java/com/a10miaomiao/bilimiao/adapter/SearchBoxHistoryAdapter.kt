package com.a10miaomiao.bilimiao.adapter

import android.widget.TextView
import com.a10miaomiao.bilimiao.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/12/10.
 */
class SearchBoxHistoryAdapter(list: List<String>)
    : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_search_box_history, list) {

    var isHis = true

    override fun convert(helper: BaseViewHolder?, item: String?) {
        var item_tv = helper!!.getView<TextView>(R.id.tv_item_search_history)
        item_tv.text = item!!
        if(isHis){
            val drawable = mContext.resources.getDrawable(R.drawable.ic_history_24dp)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)//这一步必须要做,否则不会显示
            item_tv.setCompoundDrawables(drawable,null,null,null)
        }else {
            val drawable = mContext.resources.getDrawable(R.drawable.ic_search_24dp)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)//这一步必须要做,否则不会显示
            item_tv.setCompoundDrawables(drawable, null, null, null)
        }
    }
}