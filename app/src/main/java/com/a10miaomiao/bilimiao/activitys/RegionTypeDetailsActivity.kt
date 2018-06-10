package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.RegionPagerAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.entity.RegionTypesInfo
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import kotlinx.android.synthetic.main.activity_region_details.*
import rx.Observable


/**
 * Created by 10喵喵 on 2017/9/17.
 */
class RegionTypeDetailsActivity : BaseActivity() {
    override var layoutResID: Int = R.layout.activity_region_details

    val mDataBean by lazy {
        intent.extras.getParcelable< RegionTypesInfo.DataBean>(ConstantUtil.EXTRA_PARTITION)
    }

    val titles = ArrayList<String>()

    override fun initViews(savedInstanceState: Bundle?) {

        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        if (prefs.getBoolean("region_time_line", true))
            titles.add("设置时间")
        Observable.from(mDataBean?.children)
                .subscribe({ childrenBean ->
                    titles.add(childrenBean.name)
                    tabs.addTab(tabs.newTab().setText(childrenBean.name))
                })
        val mAdapter = RegionPagerAdapter(supportFragmentManager
                , mDataBean!!.tid, titles, mDataBean!!.children)

        //mTabLayout.setTabMode(TabLayout.GRAVITY_FILL);//设置tab模式，当前为系统默认模式
        tabs.tabMode = TabLayout.MODE_SCROLLABLE
        vp_view.adapter = mAdapter//给ViewPager设置适配器
        tabs.setupWithViewPager(vp_view)//将TabLayout和ViewPager关联起来。
        tabs.setTabsFromPagerAdapter(mAdapter)//给Tabs设置适配器
    }

    override fun initToolBar() {
        toolbar.title = mDataBean?.name
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener({ finish() })
    }

    companion object {
        fun launch(activity: Activity, dataBean: RegionTypesInfo.DataBean) {
            val mIntent = Intent(activity, RegionTypeDetailsActivity::class.java)
            val mBundle = Bundle()
            mBundle.putParcelable(ConstantUtil.EXTRA_PARTITION, dataBean)
            mIntent.putExtras(mBundle)
            activity.startActivity(mIntent)
        }
    }
}