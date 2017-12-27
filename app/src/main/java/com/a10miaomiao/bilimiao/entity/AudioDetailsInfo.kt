package com.a10miaomiao.bilimiao.entity

import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.log
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by 10喵喵 on 2017/11/11.
 */
class AudioDetailsInfo(aid: String) : DetailsInfo(aid) {
    override var aidType = "au"
    var audio_url = ""
    override fun get() {
        MiaoHttp.newStringClient(
                url = BiliApiService.getAudioInfo(aid),
                onResponse = {
                    var a = "window.__INITIAL_STATE__ = "
                    var n = it.indexOf(a)
                    var m = it.indexOf(";", n)
                    if (n != -1 && m != -1) {
                        var s = it.substring(n + a.length, m)
                        log(s)
                        val jsonParser = JSONTokener(s)
                        try {
                            val jsonObject = (jsonParser.nextValue() as JSONObject).getJSONObject("audioReducer")
                            pic = jsonObject.getString("cover_url")
                            title = jsonObject.getString("title")
                            var urls = jsonObject.getJSONArray("urls")
                            if(urls.length() > 0)
                                audio_url = urls.getString(0)
                            else
                                audio_url = "获取失败"
                            onResponse?.invoke(this@AudioDetailsInfo)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            onError?.invoke(e, "视频信息解析失败或无该au号")
                        } catch (e: ClassCastException){
                            onError?.invoke(e,"解析失败 -2")
                        }
                    } else {
                        onError?.invoke(Exception(), "解析失败 -1")
                    }
                },
                onError = {
                    onError?.invoke(it, "无法连接到御坂网络")
                }
        )
    }
}