package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.fragments.download.DownloadedFragment
import com.a10miaomiao.bilimiao.fragments.download.DownloadingFragment
import kotlinx.android.synthetic.main.activity_download.*


/**
 * Created by 10喵喵 on 2018/1/11.
 */
class DownloadActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_download

    val mAdapter by lazy {
        PagerAdapter(supportFragmentManager)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        mAdapter.titles.addAll(arrayListOf("下载中","已完成"))
        mAdapter.fragments.addAll(arrayListOf(
                DownloadingFragment(),
                DownloadedFragment()
        ))

        vp_view.adapter = mAdapter//给ViewPager设置适配器
        tabs.apply {
            tabMode = TabLayout.MODE_FIXED
            setupWithViewPager(vp_view)//将TabLayout和ViewPager关联起来。
            setTabsFromPagerAdapter(mAdapter)//给Tabs设置适配器
        }
    }

    override fun initToolBar() {
        toolbar.title = "下载姬（测试版）"
        toolbar.setNavigationIcon(R.mipmap.ic_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        fun launch(activity: Activity) {
            val mIntent = Intent(activity, DownloadActivity::class.java)
            activity.startActivity(mIntent)
        }
    }
}