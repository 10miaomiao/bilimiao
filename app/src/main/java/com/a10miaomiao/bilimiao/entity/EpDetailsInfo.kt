package com.a10miaomiao.bilimiao.entity

import com.a10miaomiao.bilimiao.netword.MiaoHttp
import org.json.JSONObject
import org.json.JSONTokener

class EpDetailsInfo(aid: String) : VideoDetailsInfo(aid) {
    override var aidType = "ep"

    override fun get() {
        MiaoHttp.newStringClient(
                url = "https://www.bilibili.com/bangumi/play/ep$aid",
                onResponse = {
                    try {
                        var a = "window.__INITIAL_STATE__={"
                        var n = it.indexOf(a)
                        var m = it.indexOf("};", n + a.length)
                        var s = it.substring(n + a.length - 1, m + 1)
                        val jsonParser = JSONTokener(s)
                        val jsonObject = (jsonParser.nextValue() as JSONObject)

                        //该集信息
                        val epInfo = jsonObject.getJSONObject("epInfo")
                        pic = epInfo.getString("cover")
                        title = epInfo.getString("index_title")
                        pages.add(VideoPageInfo(
                                epInfo.getLong("cid"),
                                "P1"
                        ))
                        //up主信息
                        val upInfo = jsonObject.getJSONObject("upInfo")
                        uper_id = upInfo.getInt("mid")
                        uper_name = upInfo.getString("uname")
                        uper_face = upInfo.getString("avatar")

                        //番剧信息
                        var mediaInfo = jsonObject.getJSONObject("mediaInfo")
                        season_cover = mediaInfo.getString("cover")
                        season_id = mediaInfo.getString("season_id")
                        season_title = mediaInfo.getString("title")
                        ep_id = aid
                        download_type = "anime"
                        onResponse?.invoke(this@EpDetailsInfo)
                    } catch (e: Exception){
                        onError?.invoke(e, "获取失败了喵＞﹏＜")
                    }
                },
                onError = {
                    onError?.invoke(it, "无法连接到御坂网络")
                }
        )
    }

}