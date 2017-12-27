package com.a10miaomiao.bilimiao.entity

import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by 10喵喵 on 2017/10/12.
 */
class LiveDetailsInfo(aid: String): DetailsInfo(aid) {
    override var aidType = "live"
    override fun get() {
       MiaoHttp.newStringClient(
               url = BiliApiService.getLiveInfo(aid),
               onResponse = {
                   val jsonParser = JSONTokener(it)
                   try {
                       val jsonObject = (jsonParser.nextValue() as JSONObject).getJSONObject("data")
                       pic = jsonObject.getString("cover")
                       //log(pic!!)
                       title = jsonObject.getString("title")
                       //log(title!!)
                       onResponse?.invoke(this@LiveDetailsInfo)
                   } catch (e: JSONException) {
                       onError?.invoke(e,"视频信息解析失败或无该live号")
                   } catch (e: ClassCastException){
                       onError?.invoke(e,"网络好像有问题＞﹏＜")
                   }
               },
               onError = {
                   onError?.invoke(it,"无法连接到御坂网络")
               }
       )
    }
}