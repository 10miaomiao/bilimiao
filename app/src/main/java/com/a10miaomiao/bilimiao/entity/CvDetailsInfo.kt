package com.a10miaomiao.bilimiao.entity

import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.log

/**
 * Created by 10喵喵 on 2017/12/9.
 */
class CvDetailsInfo(aid: String) : DetailsInfo(aid) {
    override var aidType = "cv"
    override fun get() {
        MiaoHttp.newStringClient(
                url = BiliApiService.getCvInfo(aid),
                onResponse = {
                    var a = "<title>"
                    var n = it.indexOf(a)
                    var m = it.indexOf("</title>", n)
                    if (n != -1 && m != -1) {
                        title = it.substring(n + a.length, m)
                        a = "banner_url: \""
                        n = it.indexOf(a)
                        m = it.indexOf("\",", n)
                        log(m,n)
                        if (n != -1 && m != -1) {
                            pic = it.substring(n+ a.length, m)
                        }else{
                            onError?.invoke(Exception(), "该专栏不存在或专栏没有封面")
                            return@newStringClient
                        }
                    } else {
                        onError?.invoke(Exception(), "解析失败")
                        return@newStringClient
                    }
                    onResponse?.invoke(this@CvDetailsInfo)
                },
                onError = {
                    onError?.invoke(it, "无法连接到御坂网络")
                }
        )
    }
}