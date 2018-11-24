package com.a10miaomiao.bilimiao.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import com.a10miaomiao.bilimiao.activitys.VideoInfoActivity

/**
 * Created by 10喵喵 on 2017/11/5.
 */
object IntentHandlerUtil {
    val TYPE_VIDEO = "video"
    val TYPE_AUTHOR = "author"
    val TYPE_BANGUMI = "bangumi"
    /**
     * 调用b站打开打开
     */
    fun openWithPlayer(activity: Activity,type: String, id: String){
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        if (!prefs.getBoolean("is_bili_player", false)) {
            if(type == TYPE_VIDEO){
                VideoInfoActivity.launch(activity,id)
                return
            }
        }
        openWithPlayer_old(activity, type, id)
    }

    fun openWithPlayer_old(activity: Activity,type: String, id: String){
        try {
            var intent = Intent(Intent.ACTION_VIEW)
            var url = when(type){
                TYPE_VIDEO -> "bilibili://video/$id"
                TYPE_AUTHOR -> "https://space.bilibili.com/$id"
                TYPE_BANGUMI -> "bilibili://bangumi/season/$id"
                else -> "bilibili://video/$id"
            }
            intent.data = Uri.parse(url)
            activity.startActivity(intent)
        }catch (e: Exception){
            var intent = Intent(Intent.ACTION_VIEW)
            var url = when(type){
                TYPE_VIDEO -> "http://www.bilibili.com/video/av$id"
                TYPE_AUTHOR -> "https://space.bilibili.com/$id"
                TYPE_BANGUMI -> "http://bangumi.bilibili.com/anime/$id/"
                else -> "http://www.bilibili.com/video/av$id"
            }
            intent.data = Uri.parse(url)
            activity.startActivity(intent)
        }
    }
}