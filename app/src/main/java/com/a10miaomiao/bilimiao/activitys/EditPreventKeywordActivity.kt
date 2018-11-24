package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.PagerAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.fragments.EditPreventKeywordFragments
import com.a10miaomiao.bilimiao.fragments.PreventUpperFragments
import kotlinx.android.synthetic.main.activity_edit_prevent_keyword.*

/**
 * Created by 10喵喵 on 2017/10/24.
 */
class EditPreventKeywordActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_edit_prevent_keyword


    override fun initViews(savedInstanceState: Bundle?) {
        val mAdapter = PagerAdapter(supportFragmentManager)
        mAdapter.titles.addAll(arrayListOf(
                "关键字","up主"
        ))
        mAdapter.fragments.addAll(arrayListOf(
                EditPreventKeywordFragments(),
                PreventUpperFragments()
        ))
        tabs.tabMode = TabLayout.LAYOUT_MODE_CLIP_BOUNDS
        vp_view.adapter = mAdapter//给ViewPager设置适配器
        tabs.setupWithViewPager(vp_view)//将TabLayout和ViewPager关联起来。
        tabs.setTabsFromPagerAdapter(mAdapter)//给Tabs设置适配器
    }

    override fun initToolBar() {
        toolbar.title = "屏蔽设置"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener({ finish() })
    }


    companion object {
        fun launch(activity: Activity) {
            val mIntent = Intent(activity, EditPreventKeywordActivity::class.java)
            activity.startActivity(mIntent)
        }
    }

}

