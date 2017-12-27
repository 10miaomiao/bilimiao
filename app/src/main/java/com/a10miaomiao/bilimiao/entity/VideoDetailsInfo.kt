package com.a10miaomiao.bilimiao.entity

import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by 10喵喵 on 2017/10/5.
 */
class VideoDetailsInfo(aid: String) : DetailsInfo(aid) {
    override var aidType = "av"

    var uper_name: String? = null
    var uper_face: String? = null
    var uper_id: Int? = null
    var pages = ArrayList<VideoPageInfo>()
    override fun get() {
        MiaoHttp.newStringClient(
                url = BiliApiService.getVideoInfo(aid),
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    try {
                        val jsonObject = (jsonParser.nextValue() as JSONObject).getJSONObject("data")
                        pic = jsonObject.getString("pic")
                        title = jsonObject.getString("title")
                        val uper = jsonObject.getJSONObject("owner")
                        uper_id = uper.getInt("mid")
                        uper_name = uper.getString("name")
                        uper_face = uper.getString("face")
                        //视频分p部分
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
                        onResponse?.invoke(this@VideoDetailsInfo)
                    } catch (e: JSONException) {
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

    data class VideoPageInfo(
            var cid: Long,
            var title: String
    )
}