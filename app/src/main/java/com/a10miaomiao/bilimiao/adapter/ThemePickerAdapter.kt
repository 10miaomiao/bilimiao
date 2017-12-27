package com.a10miaomiao.bilimiao.adapter

import android.widget.TextView
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.entity.ThemePickerInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by 10喵喵 on 2017/12/25.
 */
class ThemePickerAdapter(list: List<ThemePickerInfo>) : BaseQuickAdapter<ThemePickerInfo, BaseViewHolder>(R.layout.item_theme_picker, list) {
    override fun convert(helper: BaseViewHolder?, item: ThemePickerInfo?) {
        var item_color = helper!!.getView<TextView>(R.id.item_color)
        var item_name = helper!!.getView<TextView>(R.id.item_name)
        var item_is_selected = helper!!.getView<TextView>(R.id.item_is_selected)
        var color = mContext.resources.getColor(item!!.color)
        item_color.setBackgroundColor(color)
        item_name.text = item!!.name
        item_name.setTextColor(color)
        if (item.isSelected) {
            item_is_selected.text = "使用中"
            item_is_selected.setTextColor(color)
        }else{
            item_is_selected.text = "使用"
            item_is_selected.setTextColor( mContext.resources.getColor(R.color.text_black))
        }
    }
}