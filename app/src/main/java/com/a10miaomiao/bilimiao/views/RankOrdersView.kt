package com.a10miaomiao.bilimiao.views

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.RankOrdersAdapter
import kotlinx.android.synthetic.main.layout_selectlist.view.*

/**
 * Created by 10喵喵 on 2017/11/23.
 */
class RankOrdersView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, R.layout.layout_selectlist, this)
        val mAdapter = RankOrdersAdapter(arrayOf("默认排序", "播放多", "新发布", "弹幕多"))
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 4)
            adapter = mAdapter
        }
    }
}