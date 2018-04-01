package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.fragments.SettingPreferenceFragment
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import kotlinx.android.synthetic.main.include_title_bar.*

/**
 * Created by 10喵喵 on 2017/10/3.
 */
class SettingActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_setting
    //val select_items = arrayOf("哔哩哔哩", "哔哩哔哩(play版)", "哔哩哔哩概念版", "哔哩哔哩白")
    val select_items = arrayOf("哔哩哔哩", "哔哩哔哩(play版)", "哔哩哔哩概念版")

    override fun initViews(savedInstanceState: Bundle?) {
        var fragment = SettingPreferenceFragment()
        fragment.arguments = intent.extras
        fragmentManager.beginTransaction()
                .replace(R.id.prefs_frame, fragment)
                .commit()
    }


    override fun initToolBar() {
        toolbar.title = "设置"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        fun launch(activity: Activity) {
            val mIntent = Intent(activity, SettingActivity::class.java)
            mIntent.putExtra(ConstantUtil.DO, 0)
            activity.startActivity(mIntent)
        }

        fun selectPalyer(activity: Activity) {
            val mIntent = Intent(activity, SettingActivity::class.java)
            mIntent.putExtra(ConstantUtil.DO, 1)
            activity.startActivity(mIntent)
        }
    }
}