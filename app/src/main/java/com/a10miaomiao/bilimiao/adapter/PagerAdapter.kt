package com.a10miaomiao.bilimiao.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by 10喵喵 on 2017/12/1.
 */
class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    val titles = ArrayList<String>()
    val fragments = ArrayList<Fragment>()

    override fun getItem(position: Int) = fragments[position]
    override fun getCount() = fragments.size
    override fun getPageTitle(position: Int) = titles[position]
}