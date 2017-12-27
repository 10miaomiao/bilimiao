package com.a10miaomiao.bilimiao.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by 10喵喵 on 2017/9/22.
 */
class SelectorDateUtil(var context: Context) {
    var timeType: Int = ConstantUtil.TIME_TYPE_DEFAULT
        private set
    var timeFrom: String? = null
    var timeTo: String? = null

    var startDate: Date? = null
        get() = text2date(timeFrom!!)
        private set

    var endDate: Date? = null
        get() = text2date(timeTo!!)
        private set

    init {
        timeType = SettingUtil.getInt(context, ConstantUtil.TIME_TYPE, ConstantUtil.TIME_TYPE_DEFAULT)
        initDate()
    }

    fun initDate() {
        var startDate: Date
        var endDate: Date = Date()
        var calendar = Calendar.getInstance()
        calendar.time = endDate
        calendar.set(Calendar.HOUR, 0)//小时设置为0
        calendar.set(Calendar.MINUTE, 0)//分钟设置为0
        calendar.set(Calendar.SECOND, 0)//秒设置为0
        calendar.add(Calendar.DAY_OF_MONTH, -7)//参数-，换为1则为加1代表在原来时间的基础上减少一天一天;ps:加减月数 小时同加减天数
        startDate = calendar.time//获取时间：'2014-07-08 00:00:00'
        when (timeType) {
            ConstantUtil.TIME_TYPE_DEFAULT -> {   //默认
                timeFrom = date2text(startDate)
                timeTo = date2text(endDate)
            }
            ConstantUtil.TIME_TYPE_CUSTOM -> { //自定义
                timeFrom = SettingUtil.getString(context, ConstantUtil.TIME_FROM, date2text(startDate))
                timeTo = SettingUtil.getString(context, ConstantUtil.TIME_TO, date2text(endDate))
            }
            ConstantUtil.TIME_TYPE_MONTH -> { //按月份
                startDate.date = 1
                endDate.month = startDate.getMonth()
                endDate.date = getMonthDate(endDate.getYear() % 4 === 0, endDate.getMonth())
                timeFrom = SettingUtil.getString(context, ConstantUtil.TIME_FROM, date2text(startDate))
                timeTo = SettingUtil.getString(context, ConstantUtil.TIME_TO, date2text(endDate))
            }
        }
    }

    companion object {
        fun date2text(d: Date): String {
            val sf = SimpleDateFormat("yyyyMMdd")
            return sf.format(d)
        }

        fun text2date(text: String): Date {
            return Date(
                    text.substring(0, text.length - 4).toInt() - 1900,
                    text.substring(text.length - 4, text.length - 2).toInt() - 1,
                    text.substring(text.length - 2, text.length).toInt()
            )
        }

        fun getMonthDate(isLeapYear: Boolean, month: Int): Int {
            val dates = intArrayOf(31, if (isLeapYear) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            return dates[month]
        }

        fun getGapCount(startDate: Date,endDate: Date): Int {
            val startL: Long = startDate!!.time
            val endL: Long = endDate!!.time
            return ((endL - startL) / (1000 * 60 * 60 * 24)).toInt()
        }

        /**
         *格式化日期,把20170926转成2017-06-26
         */
        fun formatDate(date: String,span: String): String {
            var y = date.substring(0, date.length - 4)
            var m = date.substring(date.length - 4, date.length - 2)
            var d = date.substring(date.length - 2, date.length)
            return "$y$span$m$span$d"
        }
    }

}