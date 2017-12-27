package com.a10miaomiao.bilimiao.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.NumberPicker
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.utils.SelectorDateUtil
import kotlinx.android.synthetic.main.layout_date_picker.view.*
import java.util.*

/**
 * Created by 10喵喵 on 2017/6/24.
 */

class DatePickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    val time: String
        get() = (np_year.value.toString() +
                (if (np_month.value < 10)
                    "0" + np_month.value
                else
                    np_month.value.toString()) +
                if (np_date.value < 10)
                    "0" + np_date.value
                else
                    np_date.value.toString())

    var onValueChangedListener: (() -> Unit)? = null

    init {
        //setOrientation(HORIZONTAL);
        View.inflate(context, R.layout.layout_date_picker, this)
        init()
    }

    private fun init() {
        val date = Date()
        np_year.minValue = 2009
        np_month.minValue = 1
        np_date.minValue = 1
        np_year.maxValue = 1900 + date.year
        np_month.maxValue = 12
        np_date.maxValue = 31

        np_year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        np_month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        np_date.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        np_date.setOnValueChangedListener { picker, oldVal, newVal ->
            onValueChangedListener?.invoke()
        }

        np_month.setOnValueChangedListener { picker, oldVal, newVal ->
            np_date.maxValue = SelectorDateUtil.getMonthDate((np_year.value % 4 == 0), newVal - 1)
            onValueChangedListener?.invoke()
        }
        np_year.setOnValueChangedListener { picker, oldVal, newVal ->
            np_date.maxValue = SelectorDateUtil.getMonthDate(newVal % 4 == 0, np_month.value - 1)
            onValueChangedListener?.invoke()
        }
    }

    fun setYear(value: Int) {
        np_year.value = value
    }

    fun setMonth(value: Int) {
        np_month.value = value
    }

    fun setDate(value: Int) {
        np_date.value = value
    }

    fun setValue(date: Date) {
        setYear(date.year + 1900)
        setMonth(date.month + 1)
        setDate(date.date)
    }

    override fun setEnabled(enabled: Boolean) {
        np_year.isEnabled = enabled
        np_month.isEnabled = enabled
        np_date.isEnabled = enabled
    }
}
