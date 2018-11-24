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
    //    var audio_url = ""
    override fun get() {
        MiaoHttp.newStringClient(
                url = BiliApiService.getAudioInfo(aid),
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    try {
                        val jsonObject = (jsonParser.nextValue() as JSONObject)
                        if (jsonObject.getInt("code") == 0) {
                            val data = jsonObject.getJSONObject("data")
                            pic = data.getString("cover")
                            title = data.getString("title")
//                            audio_url = if (urls.length() > 0) urls.getString(0) else "获取失败"
                            onResponse?.invoke(this@AudioDetailsInfo)
                        } else {
                            onError?.invoke(Exception(), jsonObject.getString("msg"))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        onError?.invoke(e, "视频信息解析失败或无该au号")
                    } catch (e: ClassCastException) {
                        onError?.invoke(e, "解析失败 -2")
                    }
                },
                onError = {
                    onError?.invoke(it, "无法连接到御坂网络")
                }
        )
    }
}