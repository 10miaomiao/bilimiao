package com.a10miaomiao.bilimiao.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.views.RankOrdersPopupWindow

/**
 * Created by 10喵喵 on 2017/11/23.
 */
class DropDownMenu@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : TextView(context, attrs, defStyleAttr) {
    var popMenu : RankOrdersPopupWindow? = null
        set(value) {
            field = value
            field?.setOnDismissListener {
                val drawable = resources.getDrawable(R.drawable.ic_arrow_drop_down_24dp)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)//这一步必须要做,否则不会显示
                this.setCompoundDrawables(null,null,drawable,null)
            }
        }
    init {
        setOnClickListener {
            popMenu?.show()
            val drawable = resources.getDrawable(R.drawable.ic_arrow_drop_up_24dp)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)//这一步必须要做,否则不会显示
            this.setCompoundDrawables(null,null,drawable,null)
        }
    }
}