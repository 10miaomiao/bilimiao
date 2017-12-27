package com.a10miaomiao.bilimiao.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.a10miaomiao.bilimiao.activitys.SettingActivity
import java.lang.Exception

/**
 * Created by 10喵喵 on 2017/11/5.
 */
object IntentHandlerUtil {
    /**
     * 用播放器打开
     */
    fun openWithPlayer(activity: Activity, url: String){
        //应用程序的包名
        val packages = arrayOf("tv.danmaku.bili"
                , "com.bilibili.app.in"
                , "com.bilibili.app.blue"
                , "tv.danmaku.bilixl")
        //要启动的Activity
        val activitys = arrayOf("tv.danmaku.bili.ui.intent.IntentHandlerActivity"
                , "tv.danmaku.bili.ui.intent.IntentHandlerActivity"
                , "tv.danmaku.bili.ui.intent.IntentHandlerActivity"
                , "tv.danmaku.bili.activities.videopagelist.VideoPageListActivity")
        //播放器
        var player = SettingUtil.getInt(activity, ConstantUtil.PLAYER, ConstantUtil.PLAYER_BILI)
        player = if (player in 0..3) player else ConstantUtil.PLAYER_BILI
        var intent = Intent()
        if (player == 3) {
            intent = Intent(Intent.ACTION_VIEW)
        } else {
            val componetName = ComponentName(packages[player], activitys[player])
            intent.component = componetName
        }
        intent.data = Uri.parse(url)
        if (activity.packageManager.resolveActivity(intent, 0) != null) {  //存在
            try {
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "打开B站失败了 o((>ω< ))o", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        } else {//不存在
            SettingActivity.selectPalyer(activity)
        }
    }
}