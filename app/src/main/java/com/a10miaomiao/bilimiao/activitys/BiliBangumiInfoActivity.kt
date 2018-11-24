package com.a10miaomiao.bilimiao.activitys

import android.os.Bundle
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.VideoPagesAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.entity.DiliAnimeInfo
import com.a10miaomiao.bilimiao.entity.VideoDetailsInfo
import com.a10miaomiao.bilimiao.utils.ConstantUtil

class BiliBangumiInfoActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_dili_info
    private var info: DiliAnimeInfo? = null
    val pages = ArrayList<VideoDetailsInfo.VideoPageInfo>()
    val pagesAdapter = VideoPagesAdapter(pages)

    val id by lazy {
        intent.extras.getString(ConstantUtil.ID)
    }

    override fun initViews(savedInstanceState: Bundle?) {

        loadData()
    }

    override fun initToolBar() {

    }

    private fun loadData(){

    }
}