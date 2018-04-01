package com.a10miaomiao.bilimiao.entity

import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.log
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by 10喵喵 on 2017/10/5.
 */
class AnimeDetailsInfo(aid: String) : DetailsInfo(aid) {
    var episodes = ArrayList<BangumiEpisodesInfo>()
    override var aidType = "ss"
    override fun get() {
        MiaoHttp.newStringClient(
                url = BiliApiService.getBangumiInfo(aid),
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    log(it)
                    try {
                        val jsonObject = (jsonParser.nextValue() as JSONObject).getJSONObject("result")
                        pic = jsonObject.getString("cover")
                        //log(pic!!)
                        title = jsonObject.getString("title")
                        var jsonArray = jsonObject.getJSONArray("episodes")
                        for (i in 0 until jsonArray.length()) {
                            episodes.add(BangumiEpisodesInfo(
                                    jsonArray.getJSONObject(i).getString("av_id"),
                                    jsonArray.getJSONObject(i).getInt("danmaku"),
                                    jsonArray.getJSONObject(i).getString("index_title"),
                                    jsonArray.getJSONObject(i).getString("index")
                            ))
                        }
                        if (episodes.isEmpty() && "僅" in title!!) {
                            getBiliPush()
                        } else {
                            onResponse?.invoke(this@AnimeDetailsInfo)
                        }
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

    /**
     * 从biliplu获取数据
     */
    fun getBiliPush() {
        MiaoHttp.newStringClient(
                url = "http://www.biliplus.com/api/bangumi?season=$aid",
                headers = mapOf("user-agent" to "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"),
                onResponse = {
                    log(it)
                    try {
                        val jsonParser = JSONTokener(it)
                        val jsonObject = (jsonParser.nextValue() as JSONObject).getJSONObject("result")
                        var jsonArray = jsonObject.getJSONArray("episodes")
                        for (i in 0 until jsonArray.length()) {
                            episodes.add(BangumiEpisodesInfo(
                                    jsonArray.getJSONObject(i).getString("av_id"),
                                    jsonArray.getJSONObject(i).getInt("danmaku"),
                                    jsonArray.getJSONObject(i).getString("index_title"),
                                    jsonArray.getJSONObject(i).getString("index")
                            ))
                        }
                        onResponse?.invoke(this@AnimeDetailsInfo)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        onError?.invoke(e, "视频信息解析失败或无该anime号")
                    } catch (e: ClassCastException) {
                        onError?.invoke(e, "网络好像有问题＞﹏＜")
                    }
                },
                onError = {
                    it.printStackTrace()
                    onError?.invoke(it, "无法连接到御坂网络")
                }
        )
    }
}