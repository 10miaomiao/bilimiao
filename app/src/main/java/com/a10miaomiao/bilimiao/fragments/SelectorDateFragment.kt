package com.a10miaomiao.bilimiao.fragments

import android.os.Bundle
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.activitys.SelectorDateActivity
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.utils.SelectorDateUtil
import kotlinx.android.synthetic.main.fragment_selector_date.*


/**
 * Created by 10喵喵 on 2017/9/18.
 */
class SelectorDateFragment : BaseFragment() {
    override var layoutResId: Int = R.layout.fragment_selector_date
    override fun finishCreateView(savedInstanceState: Bundle?) {
        btn_time.setOnClickListener {
            SelectorDateActivity.launch(activity!!)
        }
        loadDate()
    }

    override fun onStart() {
        super.onStart()
        loadDate()
    }

    /**
     * 加载日期
     */
    private fun loadDate() {
        val selectorDateUtil = SelectorDateUtil(activity!!)
        val s = arrayOf("当前为默认模式", "当前为自定义模式", "当前为快速选择月份模式")
        text_type.text = s[selectorDateUtil.timeType]
        val timeFrom = SelectorDateUtil.formatDate(selectorDateUtil.timeFrom!!, "/")
        val timeTo = SelectorDateUtil.formatDate(selectorDateUtil.timeTo!!, "/")
        text_time.text = "$timeFrom\n至\n$timeTo"
    }
}