package com.a10miaomiao.bilimiao.entity

import com.a10miaomiao.bilimiao.R

/**
 * Created by 10喵喵 on 2017/9/16.
 */
data class HomeRegionInfo (
        var name: String,
        var icon: Int
){
    companion object{
        fun Create(): List<HomeRegionInfo> {
            var list = ArrayList<HomeRegionInfo>()
            val itemNames = arrayOf(
                    "番剧", "国创", "动画",
                    "音乐", "舞蹈", "游戏",
                    "科技", "生活", "鬼畜",
                    "时尚", "广告", "娱乐",
                    "电影", "电视剧"
            )
            val itemIcons = intArrayOf(
                    R.drawable.ic_region_fj, R.drawable.ic_region_fj_domestic, R.drawable.ic_region_dh,
                    R.drawable.ic_region_yy, R.drawable.ic_region_wd, R.drawable.ic_region_yx,
                    R.drawable.ic_region_kj, R.drawable.ic_region_sh, R.drawable.ic_region_gc,
                    R.drawable.ic_region_ss, R.drawable.ic_region_ad, R.drawable.ic_region_yl,
                    R.drawable.ic_region_dy, R.drawable.ic_region_dsj
            )
            (0 until itemNames.size).mapTo(list) { HomeRegionInfo(itemNames[it],itemIcons[it]) }
            return list
        }
    }
}
