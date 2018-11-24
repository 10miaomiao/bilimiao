package com.a10miaomiao.bilimiao.utils

object TextUtil {
    fun getIntermediateText1(text: String, s: String, e: String): String {
        val n = text.indexOf(s)
        val m = text.indexOf(e, n + s.length)
        return if (m != -1 && n != -1 && m > n) {
            text.substring(n + s.length, m)
        } else {
            ""
        }
    }

    fun getIntermediateTexts(text: String, s: String, e: String): ArrayList<String> {
        val result = ArrayList<String>()
        var m = 0
        var n = 0
        while (true) {
            n = text.indexOf(s, m)
            if (n == -1)
                break
            m = text.indexOf(e, n + s.length)
            if (m == -1)
                break
            result.add(text.substring(n + s.length, m))
        }
        return result
    }
}