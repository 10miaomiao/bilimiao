package com.a10miaomiao.bilimiao.utils

import android.content.Context
import android.os.Environment
import android.preference.PreferenceManager
import com.a10miaomiao.bilimiao.entity.client.ClientDownInfo

/**
 * Created by 10喵喵 on 2018/2/12.
 */
object BiliClientDownHelper {
    /**
     * 创建番剧
     */
    fun createAnime(context: Context, info: ClientDownInfo) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        var client_type = prefs.getString("bili_client_type","0").toInt()
        var biliPackname = arrayOf("tv.danmaku.bili"
                , "com.bilibili.app.in"
                , "com.bilibili.app.blue")[client_type]
        var biliPath = Environment.getExternalStorageDirectory().path + "/Android/data" + "/$biliPackname/download/"
        FileUtil.isPath(biliPath)
        var sPath = FileUtil.isPath(biliPath + "s_${info.season_id}/")  //番剧文件夹
        var ePath = FileUtil.isPath(sPath + info.episode_id + "/")      //剧集文件夹
        //var videoPath = FileUtil.isPath(ePath + info.quality + "/")     //视频文件夹
        FileUtil(ePath, miao = false).saveJSON(info.entry, "entry")
        // FileUtil(videoPath, miao = false).saveJSON(info.index, "entry")
    }
}