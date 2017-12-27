package com.a10miaomiao.bilimiao.adapter

import android.util.TypedValue
import android.widget.TextView
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.utils.log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder



/**
 * Created by 10喵喵 on 2017/11/23.
 */
class RankOrdersAdapter(list: Array<String>) : BaseQuickAdapter<RankOrdersAdapter.Data, BaseViewHolder>(R.layout.item_rank_orders,RankOrdersAdapter.create(list)) {
    var checkItemPosition = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(helper: BaseViewHolder?, item: Data?) {
        var text = helper!!.getView<TextView>(R.id.text)
        text.text = item?.text
        text.setTextColor(
                if(item!!.index == checkItemPosition) {
                    val typedValue = TypedValue()
                    mContext.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
                    mContext.resources.getColor(typedValue.resourceId)
                }else
                    mContext.resources.getColor(R.color.text_black)
        )
    }

    companion object {
        fun create(s: Array<String>): List<Data> {
            var list = ArrayList<Data>()
            (0 until s.size).mapTo(list) { Data(s[it], it) }
            log(list.size)
            return list
        }
    }

    data class Data(
            var text: String,
            var index: Int
    )
}