package com.a10miaomiao.bilimiao.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.a10miaomiao.bilimiao.entity.RegionTypesInfo
import com.a10miaomiao.bilimiao.fragments.RegionTypeDetailsFragment
import com.a10miaomiao.bilimiao.fragments.SelectorDateFragment
import rx.Observable


/**
 * Created by 10喵喵 on 2017/9/18.
 */
class RegionPagerAdapter(fm: FragmentManager
                         ,var id: Int
                         ,var titles: List<String>
                         ,var childrens: List<RegionTypesInfo.DataBean.ChildrenBean>): FragmentStatePagerAdapter(fm) {
    private val fragments = ArrayList<Fragment>()

    init{
        if(titles[0] == "设置时间")
            fragments.add(SelectorDateFragment())
        Observable.from(childrens)
                .subscribe({ childrenBean -> fragments.add(RegionTypeDetailsFragment.newInstance(childrenBean.tid)) })
    }
    
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}