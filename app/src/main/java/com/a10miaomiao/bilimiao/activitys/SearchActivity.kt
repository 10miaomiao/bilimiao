package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.dialog.SearchBoxFragment
import com.a10miaomiao.bilimiao.fragments.search.BangumiResultFragment
import com.a10miaomiao.bilimiao.fragments.search.MovieResultsFragment
import com.a10miaomiao.bilimiao.fragments.search.SearchResultFragment
import com.a10miaomiao.bilimiao.fragments.search.UpperResultFragment
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import kotlinx.android.synthetic.main.activity_search.*
import java.util.regex.Pattern


/**
 * Created by 10喵喵 on 2017/11/7.
 */
class SearchActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_search
    lateinit var searchFragment: SearchBoxFragment
    var mAdapter: PagerAdapter? = null
//    lateinit var searchResultFragment: SearchResultFragment
//    lateinit var bangumiResultFragment: BangumiResultFragment
//    lateinit var upperResultFragment: UpperResultFragment

    override fun initViews(savedInstanceState: Bundle?) {
        var keyword = intent.extras.getString(ConstantUtil.KETWORD)
        tv_search.text = keyword
        searchFragment = SearchBoxFragment.newInstance(keyword)
//        searchResultFragment = SearchResultFragment.newInstance(keyword)
//        bangumiResultFragment = BangumiResultFragment.newInstance(keyword)
//        upperResultFragment = UpperResultFragment.newInstance(keyword)

        tabs.tabMode = TabLayout.MODE_FIXED//设置tab模式，当前为系统默认模式
        mAdapter = PagerAdapter(supportFragmentManager)
        mAdapter?.titles?.addAll(arrayListOf("综合", "番剧", "up主", "影视"))
        mAdapter?.fragments?.addAll(arrayListOf(
                SearchResultFragment.newInstance(keyword),
                BangumiResultFragment.newInstance(keyword),
                UpperResultFragment.newInstance(keyword),
                MovieResultsFragment.newInstance(keyword)
        ))

        vp_view.adapter = mAdapter//给ViewPager设置适配器
        tabs.setupWithViewPager(vp_view)//将TabLayout和ViewPager关联起来。
        tabs.setTabsFromPagerAdapter(mAdapter)//给Tabs设置适配器
    }

    override fun initToolBar() {
        iv_search_back.setOnClickListener {
            finish()
        }
        searchFragment.onSearchClick = {
            var d = search(it)
            if (d == null) {
                //toast("请输入正确的格式")
                tv_search.text = it
//                mAdapter?.fragments?.clear()
//                mAdapter?.fragments?.addAll(arrayListOf(
//                        SearchResultFragment.newInstance(it),
//                        BangumiResultFragment.newInstance(it),
//                        UpperResultFragment.newInstance(it)
//                ))
//                mAdapter?.notifyDataSetChanged()
//                var bundle = Bundle()
//                bundle.putString(ConstantUtil.KETWORD,it)
//                searchResultFragment.arguments = bundle
//                bangumiResultFragment.arguments = bundle
//                upperResultFragment.arguments = bundle
                noAnimationLaunch(activity, it)
                finish()
                overridePendingTransition(0, 0)
                true
            } else {
                InfoActivity.launch(activity, d.aid, d.type)
                true
            }
        }
        tv_search.setOnClickListener {
            searchFragment.show(supportFragmentManager, "-----")
        }
        iv_search_search.setOnClickListener {
            searchFragment.show(supportFragmentManager, "-----")
        }
    }

    private fun search(keyword: String): Info? {
        var a = ""
        var ss = arrayOf("av", "ss", "live", "au", "cv", "ep")
        for (s in ss) {
            a = getAid(keyword, "$s(\\d+)")
            if (a != "") {
                return Info(a, s)
            }
        }
        return null
    }

    /**
     * 用正则获取视频id
     */
    private fun getAid(text: String, regex: String): String {
        val compile = Pattern.compile(regex)
        val matcher = compile.matcher(text)
        if (matcher.find())
            return matcher.group(1)//提取匹配到的结果
        return ""
    }

    companion object {
        fun launch(activity: Activity, keyword: String) {
            val mIntent = Intent(activity, SearchActivity::class.java)
            mIntent.putExtra(ConstantUtil.KETWORD, keyword)
            activity.startActivity(mIntent)
        }

        fun noAnimationLaunch(activity: Activity, keyword: String) {
            val mIntent = Intent(activity, SearchActivity::class.java)
            mIntent.putExtra(ConstantUtil.KETWORD, keyword)
            mIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION;
            activity.startActivity(mIntent)
        }
    }

    data class Info(
            var aid: String,
            var type: String
    )
}