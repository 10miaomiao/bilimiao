package com.a10miaomiao.bilimiao.fragments

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.activitys.AboutActivity
import com.a10miaomiao.bilimiao.activitys.ThemePickerActivity
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.SettingUtil



/**
 * Created by 10喵喵 on 2017/10/28.
 */
class SettingPreferenceFragment : PreferenceFragment() {

    val about by lazy {
        findPreference("about") as Preference
    }
    val donate by lazy {
        findPreference("donate") as Preference
    }
    val help by lazy {
        findPreference("help") as Preference
    }

    private val select_items by lazy {
        activity.resources.getStringArray(R.array.player_name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_setting)
        if (arguments.getInt(ConstantUtil.DO) == 1) {
            selectPalyer()
        }
        showVersion()
    }

    /**
     * 显示应用版本号
     */
    private fun showVersion() {
        val version = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName
        about.summary = "版本：" + version
        donate.summary = "开发者想买女朋友o((>ω< ))o"
        help.summary = "有啥不懂的吗(￣▽￣)"
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen?, preference: Preference?): Boolean {
        when (preference?.key) {
            "player" -> {
                selectPalyer()
                return true
            }
            "about" -> {
                AboutActivity.launch(activity)
                return true
            }
            "donate" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                //HTTPS://QR.ALIPAY.COM/FKX07587MLQPOBBKACENE1
                try {
                    intent.data = Uri.parse("alipayqr://platformapi/startapp?saId=10000007&qrcode=https://qr.alipay.com/FKX07587MLQPOBBKACENE1")
                    startActivity(intent)
                } catch (e: Exception) {
                    intent.data = Uri.parse("https://qr.alipay.com/FKX07587MLQPOBBKACENE1")
                    startActivity(intent)
                }
                return true
            }
            "theme" -> {
                ThemePickerActivity.launch(activity)
            }
            "help" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://10miaomiao.cn/bilimiao/help.html")
                startActivity(intent)
            }
        }

        return false
    }

    /**
     * 选择播放器
     */
    fun selectPalyer() {
        AlertDialog.Builder(activity)
                .setItems(select_items) { dialogInterface, n ->
                    //应用程序的包名
                    val packages = arrayOf("tv.danmaku.bili"
                            , "com.bilibili.app.in"
                            , "com.bilibili.app.blue")
                    //要启动的Activity
                    val activitys = arrayOf("tv.danmaku.bili.ui.intent.IntentHandlerActivity"
                            , "tv.danmaku.bili.ui.intent.IntentHandlerActivity"
                            , "tv.danmaku.bili.ui.intent.IntentHandlerActivity")
                    var intent = Intent()
                    if (n == 3) {
                        intent = Intent(Intent.ACTION_VIEW)
                    } else {
                        val componetName = ComponentName(packages[n], activitys[n])
                        intent.component = componetName
                    }
                    if (activity.packageManager.resolveActivity(intent, 0) != null) {  //存在
                        SettingUtil.putInt(activity, ConstantUtil.PLAYER, n)
                        //player.summary = "当前播放器：${select_items[n]}"
                    } else {//不存在
                        Toast.makeText(activity, "修改失败，你大概没有安装${select_items[n]}", Toast.LENGTH_LONG).show()
                    }
                }
                .setCancelable(true)
                .show()
    }
}