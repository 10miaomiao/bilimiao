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
open class VideoDetailsInfo(aid: String) : DetailsInfo(aid) {
    override var aidType = "av"

    var uper_name: String? = null
    var uper_face: String? = null
    var uper_id: Int? = null
    var pages = ArrayList<VideoPageInfo>()

    var download_type = "video"
    var ep_id = ""
    var season_id = ""
    var season_cover = ""
    var season_title = ""

    override fun get() {
        MiaoHttp.newStringClient(
                url = BiliApiService.getVideoInfo(aid),
                onResponse = {
                    log(it)
                    try {
                        val jsonParser = JSONTokener(it)
                        val jsonObject = (jsonParser.nextValue() as JSONObject).getJSONObject("data")
                        resolveData(jsonObject)
                    } catch (e: JSONException) {
                        getBiliplus()
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
     * 从biliplus获取数据
     */
    fun getBiliplus() {
        MiaoHttp.newStringClient(
                url = "https://www.biliplus.com/api/view?id=$aid",
                onResponse = {
                    try {
                        log(it)
                        val jsonParser = JSONTokener(it)
                        val jsonObject = (jsonParser.nextValue() as JSONObject)//.getJSONObject("v2_app_api")
                        resolveData(jsonObject)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        onError?.invoke(e, "视频信息解析失败或无该av号")
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
     * 解析数据
     */
    fun resolveData(jsonObject: JSONObject) {
        pic = jsonObject.getString("pic")
        title = jsonObject.getString("title")
        //判断是否为番剧
        if (!jsonObject.isNull("season")) {
            download_type = "anime"
            //获取epid
            var redirect_url = jsonObject.getString("redirect_url")
            var n = redirect_url.indexOf("ep")
            var m = redirect_url.indexOf("/", n)
            if (n != -1 && m != -1) {
                ep_id = redirect_url.substring(n + 2, m)
            }

            var season = jsonObject.getJSONObject("season")
            season_cover = season.getString("cover")
            season_id = season.getString("season_id")
            season_title = season.getString("title")
        }
        try {
            val uper = jsonObject.getJSONObject("owner")
            uper_id = uper.getInt("mid")
            uper_name = uper.getString("name")
            uper_face = uper.getString("face")
        } catch (e: JSONException) {
            uper_id = jsonObject.getInt("mid")
            uper_name = jsonObject.getString("author")
            uper_face = jsonObject.getString("face")
        }
        //视频分p部分
        try {
            val pages_j = jsonObject.getJSONArray("pages")
            if (pages_j.length() == 1) {
                pages.add(VideoPageInfo(
                        pages_j.getJSONObject(0).getLong("cid"),
                        "P1"
                ))
            } else {
                for (i in 0 until pages_j.length()) {
                    pages.add(VideoPageInfo(
                            pages_j.getJSONObject(i).getLong("cid"),
                            pages_j.getJSONObject(i).getString("part")
                    ))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            val pages_j = jsonObject.getJSONArray("list")
            if (pages_j.length() == 1) {
                pages.add(VideoPageInfo(
                        pages_j.getJSONObject(0).getLong("cid"),
                        "P1"
                ))
            } else {
                for (i in 0 until pages_j.length()) {
                    pages.add(VideoPageInfo(
                            pages_j.getJSONObject(i).getLong("cid"),
                            pages_j.getJSONObject(i).getString("part")
                    ))
                }
            }
        }
        onResponse?.invoke(this@VideoDetailsInfo)
    }


    data class VideoPageInfo(
            var cid: Long,
            var title: String
    )
}