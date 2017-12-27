package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.ThemePickerAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.entity.ThemePickerInfo
import com.a10miaomiao.bilimiao.utils.ThemeHelper
import com.a10miaomiao.bilimiao.views.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_theme_picker.*
import kotlinx.android.synthetic.main.include_title_bar.*

/**
 * Created by 10喵喵 on 2017/12/25.
 */
class ThemePickerActivity : BaseActivity(){
    override var layoutResID = R.layout.activity_theme_picker
    val list = arrayListOf(
            ThemePickerInfo(R.color.pink, "少女粉"),
            ThemePickerInfo(R.color.red, "姨妈红"),
            ThemePickerInfo(R.color.orange, "咸蛋黄"),
            ThemePickerInfo(R.color.green_light, "早苗绿"),
            ThemePickerInfo(R.color.blue, "胖次蓝"),
            ThemePickerInfo(R.color.purple, "基佬紫")
    )
    val mAdapter by lazy {
        ThemePickerAdapter(list)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        list[ThemeHelper.getTheme(activity)].isSelected = true
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        recycle.addItemDecoration(DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL_LIST)) //分割线
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if(list[position].isSelected)
                return@setOnItemClickListener
            ThemeHelper.setTheme(activity,position)

            overridePendingTransition(0, 0)//不设置进入退出动画
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            launch(activity)
        }
    }

    override fun initToolBar() {
        toolbar.title = "选择主题"
        toolbar.setNavigationIcon(R.mipmap.ic_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        fun launch(activity: Activity) {
            val mIntent = Intent(activity, ThemePickerActivity::class.java)
            activity.startActivity(mIntent)
        }
    }
}