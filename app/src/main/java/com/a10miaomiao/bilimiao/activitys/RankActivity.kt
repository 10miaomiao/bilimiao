package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.PagerAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.fragments.rank.BangumiRankFragment
import com.a10miaomiao.bilimiao.fragments.rank.VideoRankFragment
import com.a10miaomiao.bilimiao.netword.BiliApiService
import kotlinx.android.synthetic.main.activity_edit_prevent_keyword.*

/**
 * Created by 10喵喵 on 2017/11/30.
 */
class RankActivity : BaseActivity(){
    override var layoutResID = R.layout.activity_edit_prevent_keyword

    override fun initViews(savedInstanceState: Bundle?) {
        val mAdapter = PagerAdapter(supportFragmentManager)
        mAdapter.titles.addAll(arrayListOf(
                "全站榜","原创榜","新番榜","新人榜"
        ))

        mAdapter.fragments.addAll(arrayListOf(
                VideoRankFragment.newInstance(BiliApiService::getRankAll),
                VideoRankFragment.newInstance(BiliApiService::getRankOrigin),
                BangumiRankFragment(),
                VideoRankFragment.newInstance(BiliApiService::getRankRookie)
        ))
        tabs.tabMode = TabLayout.LAYOUT_MODE_CLIP_BOUNDS
        vp_view.adapter = mAdapter//给ViewPager设置适配器
        tabs.setupWithViewPager(vp_view)//将TabLayout和ViewPager关联起来。
        tabs.setTabsFromPagerAdapter(mAdapter)//给Tabs设置适配器
    }

    override fun initToolBar() {
        toolbar.title = "排行榜"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener({ finish() })
    }


    companion object {
        fun launch(activity: Activity) {
            val mIntent = Intent(activity, RankActivity::class.java)
            activity.startActivity(mIntent)
        }
    }

}