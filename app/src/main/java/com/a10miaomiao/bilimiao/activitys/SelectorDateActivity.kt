package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.SelectorDateUtil
import com.a10miaomiao.bilimiao.utils.SettingUtil
import com.a10miaomiao.bilimiao.utils.log
import kotlinx.android.synthetic.main.activity_selector_date.*
import kotlinx.android.synthetic.main.include_title_bar.*
import java.util.*


/**
 * Created by 10喵喵 on 2017/9/22.
 */
class SelectorDateActivity : BaseActivity() {

    override var layoutResID: Int = R.layout.activity_selector_date

    var mCards = arrayOfNulls<CardView>(3)

    var isLink = false
        set(value) {
            if (getTimeSpan("锁定失败")) {
                field = value
                SettingUtil.putBoolean(activity, ConstantUtil.TIME_IS_LINK, value)
                ib_to.setImageResource(
                        if (value) R.drawable.ic_link_black_24dp else R.drawable.ic_unfold_more_black_24dp
                )
            }
        }

    var timeSpan = 1

    override fun initToolBar() {
        toolbar.title = "设置时间线"
        toolbar.setNavigationIcon(R.drawable.ic_check_white_24dp)
        toolbar.setNavigationOnClickListener {
            if (saveDate())
                finish()
        }
        toolbar.inflateMenu(R.menu.close)
        toolbar.setOnMenuItemClickListener {
            finish()
            true
        }
    }

    override fun initViews(savedInstanceState: Bundle?) {
        mCards[0] = card1
        mCards[1] = card2
        mCards[2] = card3

        val selectorDateUtil = SelectorDateUtil(activity)
        time_from.setValue(selectorDateUtil.startDate!!)
        time_to.setValue(selectorDateUtil.endDate!!)
        month_picker.setValue(selectorDateUtil.startDate)

        isLink = SettingUtil.getBoolean(activity, ConstantUtil.TIME_IS_LINK, true)
        if(isLink)
            if(!getTimeSpan("锁定失败"))
                isLink = false

        select_view.apply {
            titles = arrayOf("默认模式", "自定义", "按月份快速选择")
            onChangeItem = {
                mCards[0]?.visibility = View.GONE
                mCards[1]?.visibility = View.GONE
                mCards[2]?.visibility = View.GONE
                mCards[it]?.visibility = View.VISIBLE
            }
            index = selectorDateUtil.timeType
        }
        btn_ok.setOnClickListener {
            if (saveDate())
                finish()
        }
        ib_to.setOnClickListener {
            isLink = !isLink
        }
        time_from.onValueChangedListener = {
            if (isLink) {
                log("time_from.time",time_from.time)
                val calendar = Calendar.getInstance()
                calendar.time = SelectorDateUtil.text2date(time_from.time)
                calendar.set(Calendar.HOUR, 0)//小时设置为0
                calendar.set(Calendar.MINUTE, 0)//分钟设置为0
                calendar.set(Calendar.SECOND, 0)//秒设置为0
                calendar.add(Calendar.DAY_OF_MONTH, timeSpan)
                time_to.setValue(calendar.time)
            }
        }
        time_to.onValueChangedListener = {
            if (isLink) {
                log("time_to.time",time_to.time)
                val calendar = Calendar.getInstance()
                calendar.time = SelectorDateUtil.text2date(time_to.time)
                calendar.set(Calendar.HOUR, 0)//小时设置为0
                calendar.set(Calendar.MINUTE, 0)//分钟设置为0
                calendar.set(Calendar.SECOND, 0)//秒设置为0
                calendar.add(Calendar.DAY_OF_MONTH, - timeSpan)
                time_from.setValue(calendar.time)
            }
        }
    }


    /**
     * 保存时间线
     */
    private fun saveDate(): Boolean {
        when (select_view.index) {
            0 -> {
                SettingUtil.putInt(activity, ConstantUtil.TIME_TYPE, 0)
            }
            1 -> {
                if (getTimeSpan("时间线跳跃失败")) {
                    SettingUtil.putInt(activity, ConstantUtil.TIME_TYPE, 1)
                    SettingUtil.putString(activity, ConstantUtil.TIME_FROM, time_from.time)
                    SettingUtil.putString(activity, ConstantUtil.TIME_TO, time_to.time)
                } else {
                    return false
                }
            }
            2 -> {
                SettingUtil.putInt(activity, ConstantUtil.TIME_TYPE, 2)
                SettingUtil.putString(activity, ConstantUtil.TIME_FROM, month_picker.timeFrom)
                SettingUtil.putString(activity, ConstantUtil.TIME_TO, month_picker.timeTo)
            }
        }
        return true
    }

    private fun getTimeSpan(title: String): Boolean {
        val startDate = SelectorDateUtil.text2date(time_from.time)
        val endDate = SelectorDateUtil.text2date(time_to.time)
        timeSpan = SelectorDateUtil.getGapCount(startDate, endDate)
        if (timeSpan > 30) {
            AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage("时间跨度不能超过30天")
                    .setNegativeButton("确定", null)
                    .show()
            return false
        } else if (timeSpan < 0) {
            AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage("起始时间不能大于结束时间")
                    .setNegativeButton("确定", null)
                    .show()
            return false
        } else {
            return true
        }
    }

    companion object {
        fun launch(activity: Activity) {
            val mIntent = Intent(activity, SelectorDateActivity::class.java)
            activity.startActivity(mIntent)
        }

    }

}