package com.a10miaomiao.bilimiao.utils

import android.app.Activity

fun Activity.dip(dipValue: Int): Int{
    val scale = this.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}