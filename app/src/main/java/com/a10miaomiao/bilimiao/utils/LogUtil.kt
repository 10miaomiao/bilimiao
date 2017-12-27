package com.a10miaomiao.bilimiao.utils

import android.util.Log

/**
 * Created by 10喵喵 on 2017/8/3.
 */
fun log(msg: String) {
    Log.d("------", msg)
}
fun log(msg: Int) {
    log(msg.toString())
}
fun log(tag: String, msg: String) {
    Log.d(tag, msg)
}
fun log(tag: Int, msg: String) {
    log(tag.toString(),msg)
}
fun log(tag: String, msg: Int) {
    log(tag,msg.toString())
}
fun log(tag: Int, msg: Int) {
    log(tag.toString(),msg.toString())
}

