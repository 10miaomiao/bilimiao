package com.a10miaomiao.bilimiao.utils

import android.content.Context
import android.content.SharedPreferences
import com.a10miaomiao.bilimiao.R


/**
 * Created by 10喵喵 on 2017/12/25.
 */
object ThemeHelper{
    private val CURRENT_THEME = "theme_current"

    val themeIds = arrayOf(
            R.style.PinkTheme,
            R.style.RedTheme,
            R.style.YellowTheme,
            R.style.GreenTheme,
            R.style.BlueTheme,
            R.style.PurpleTheme
    )

    fun getSharePreference(context: Context): SharedPreferences {
        return context.getSharedPreferences("bilimiao_theme", Context.MODE_PRIVATE)
    }


    fun setTheme(context: Context, themeId: Int) {
        getSharePreference(context).edit()
                .putInt(CURRENT_THEME, themeId)
                .apply()
    }


    fun getTheme(context: Context): Int {
        return getSharePreference(context).getInt(CURRENT_THEME, 0)
    }

    fun getThemeId(context: Context): Int {
        var index = getTheme(context)
        if(index >= themeIds.size)
            index = 0
        return themeIds[index]
    }
}