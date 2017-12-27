package com.a10miaomiao.bilimiao.entity

import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by 10喵喵 on 2017/10/5.
 */
class AnimeDetailsInfo(aid: String) : DetailsInfo(aid) {
    var episodes = ArrayList<BangumiEpisodesInfo>()
    override var aidType = "anime"
    override fun get() {
        MiaoHttp.newStringClient(
                url = BiliApiService.getBangumiInfo(aid),
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    //log(response)
                    try {
                        val jsonObject = (jsonParser.nextValue() as JSONObject).getJSONObject("result")
                        pic = jsonObject.getString("cover")
                        //log(pic!!)
                        title = jsonObject.getString("bangumi_title")
                        var jsonArray = jsonObject.getJSONArray("episodes")
                        for (i in 0 until jsonArray.length()) {
                            episodes.add(BangumiEpisodesInfo(
                                    jsonArray.getJSONObject(i).getString("av_id"),
                                    jsonArray.getJSONObject(i).getInt("danmaku"),
                                    jsonArray.getJSONObject(i).getString("index_title"),
                                    jsonArray.getJSONObject(i).getString("index")
                            ))
                        }
//                            var str_episodes = jsonObject.getJSONArray("episodes").toString()
//                            episodes = Gson().fromJson(str_episodes
//                                    , object : TypeToken<BangumiEpisodesInfo>() {}.type)
//                            //log(title!!)
                        onResponse?.invoke(this@AnimeDetailsInfo)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        onError?.invoke(e, "视频信息解析失败或无该anime号")
                    } catch (e: ClassCastException) {
                        onError?.invoke(e, "网络好像有问题＞﹏＜")
                    }
                },
                onError = {
                    onError?.invoke(it, "无法连接到御坂网络")
                }
        )
    }

}