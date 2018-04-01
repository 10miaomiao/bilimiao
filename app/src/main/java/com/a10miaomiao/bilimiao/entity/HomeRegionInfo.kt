package com.a10miaomiao.bilimiao.entity

import android.content.Context
import android.preference.PreferenceManager
import com.a10miaomiao.bilimiao.R

/**
 * Created by 10喵喵 on 2017/9/16.
 */
data class HomeRegionInfo (
        var name: String,
        var icon: Int
){
    companion object{
        fun create(context: Context): List<HomeRegionInfo> {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            var list = ArrayList<HomeRegionInfo>()
            val itemNames = arrayOf(
                    "番剧", "国创", "动画",
                    "音乐", "舞蹈", "游戏",
                    "科技", "生活", "鬼畜",
                    "时尚", "广告", "娱乐",
                    "影视", "电影", "电视剧"
            )
            val itemIcons = if (prefs.getBoolean("region_ico_new", false))
                intArrayOf(
                    R.drawable.ic_category_t13, R.drawable.ic_category_t167, R.drawable.ic_category_t1,
                    R.drawable.ic_category_t3, R.drawable.ic_category_t129, R.drawable.ic_category_t4,
                    R.drawable.ic_category_t36, R.drawable.ic_category_t160, R.drawable.ic_category_t119,
                    R.drawable.ic_category_t155, R.drawable.ic_category_t165, R.drawable.ic_category_t5,
                    R.drawable.ic_category_t181, R.drawable.ic_category_t23, R.drawable.ic_category_t11
                )
            else
                intArrayOf(
                    R.drawable.ic_region_fj, R.drawable.ic_region_fj_domestic, R.drawable.ic_region_dh,
                    R.drawable.ic_region_yy, R.drawable.ic_region_wd, R.drawable.ic_region_yx,
                    R.drawable.ic_region_kj, R.drawable.ic_region_sh, R.drawable.ic_region_gc,
                    R.drawable.ic_region_ss, R.drawable.ic_region_ad, R.drawable.ic_region_yl,
                    R.drawable.ic_region_ys,R.drawable.ic_region_dy, R.drawable.ic_region_dsj
                )
            (0 until itemNames.size).mapTo(list) { HomeRegionInfo(itemNames[it],itemIcons[it]) }
            return list
        }
    }
}
