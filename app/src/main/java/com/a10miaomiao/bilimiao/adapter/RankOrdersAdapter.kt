package com.a10miaomiao.bilimiao.adapter

import android.util.TypedValue
import android.widget.TextView
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.utils.ThemeHelper
import com.a10miaomiao.bilimiao.utils.log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_region_details.*


/**
 * Created by 10喵喵 on 2017/11/23.
 */
class RankOrdersAdapter(list: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_rank_orders, list) {

    constructor(list: Array<String>) : this(list.toList())

    var checkItemPosition = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(helper: BaseViewHolder?, item: String?) {
        var text = helper!!.getView<TextView>(R.id.text)
        text.text = item
        text.setTextColor(
                if(helper.position == checkItemPosition)
                    mContext.resources.getColor(ThemeHelper.getColorAccent(mContext))
                else
                    mContext.resources.getColor(R.color.text_black)
        )
    }
}