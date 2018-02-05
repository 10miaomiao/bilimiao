package com.a10miaomiao.bilimiao.utils

import android.util.Log

/**
 * Created by 10喵喵 on 2017/8/3.
 */
fun log(msg: String) {
    log("------", msg)
}
fun log(msg: Int) {
    log(msg.toString())
}
fun log(msg: Long) {
    log(msg.toString())
}
fun log(tag: String, msg: String) {
    var msg = msg.trim()
    var index = 0
    val maxLength = 3000
    var sub: String
    while (index < msg.length) {
        // java的字符不允许指定超过总的长度end
        if (msg.length <= index + maxLength) {
            sub = msg.substring(index)
        } else {
            sub = msg.substring(index, index + maxLength)
        }
        index += maxLength
        Log.d(tag, sub.trim())
    }
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
fun log(tag: String, msg: Long) {
    log(tag,msg.toString())
}

