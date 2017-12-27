package com.a10miaomiao.bilimiao.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.a10miaomiao.bilimiao.utils.ThemeHelper
import com.trello.rxlifecycle.components.support.RxAppCompatActivity



/**
 * Created by 10喵喵 on 2017/9/16.
 */
abstract class BaseActivity : RxAppCompatActivity() {
    abstract var layoutResID: Int
    var activity : Activity
        set(v){}
        get() = this

    var _theme = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _theme = ThemeHelper.getThemeId(this)
        setTheme(_theme)
        setContentView(layoutResID)
        initViews(savedInstanceState)
        initToolBar()
    }

    abstract fun initViews(savedInstanceState: Bundle?)

    abstract fun initToolBar()

    fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG)
                .show()
    }

    override fun onResume() {
        super.onResume()
        if (_theme != ThemeHelper.getThemeId(activity)) {
            val intent = intent
            overridePendingTransition(0, 0)//不设置进入退出动画
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
        }
    }
}
