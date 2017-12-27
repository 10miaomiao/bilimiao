package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.UpperChannelAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.db.PreventUpperDB
import com.a10miaomiao.bilimiao.entity.SubmitVideosInfo
import com.a10miaomiao.bilimiao.entity.UpperChannelInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.log
import com.a10miaomiao.bilimiao.views.UpperHeaderView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_uper_video_list.*
import kotlinx.android.synthetic.main.include_title_bar.*

/**
 * Created by 10喵喵 on 2017/10/29.
 */
class UpperDetailActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_uper_deyail_info

    val upperDB: PreventUpperDB by lazy {
        PreventUpperDB(activity, PreventUpperDB.DB_NAME, null, 1)
    }

    val mid by lazy {
        intent.extras.getInt(ConstantUtil.MID)
    }
    val name by lazy {
        intent.extras.getString(ConstantUtil.NAME)
    }

    var archives = ArrayList<UpperChannelInfo.UperChannelData>()

    val mAdapter by lazy {
        UpperChannelAdapter(archives)
    }
    var loaded = 0

    lateinit var upperHeaderView: UpperHeaderView

    var isPrevent = false   //是否屏蔽

    override fun initViews(savedInstanceState: Bundle?) {
        upperHeaderView = UpperHeaderView(activity)
        upperHeaderView.name = name
        upperHeaderView.url = intent.extras.getString(ConstantUtil.PIC)
        initRecyclerView()
        loadSubmitData()
        loadChannelData()
        swipe_ly.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent,
                R.color.colorAccent, R.color.colorAccent)
        swipe_ly.setOnRefreshListener({
            clearList()
            loadSubmitData()
            loadChannelData()
        })
    }

    override fun initToolBar() {
        toolbar.title = "up主的投稿"
        toolbar.setNavigationIcon(R.mipmap.ic_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        if (upperDB.searchUid(mid.toString())) {
            toolbar.menu.add("取消屏蔽该up主")
            isPrevent = true
        }else{
            toolbar.menu.add("屏蔽该up主")
            isPrevent = false
        }
        toolbar.setOnMenuItemClickListener {
            if(isPrevent) {
                upperDB.deleteHistory(mid.toString())
                isPrevent = false
                toolbar.menu.getItem(0).title = "屏蔽该up主"
            }else {
                upperDB.insertHistory(mid, name)
                isPrevent = true
                toolbar.menu.getItem(0).title = "取消屏蔽该up主"
            }

            true
        }
    }

    private fun initRecyclerView() {
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 2)
            adapter = mAdapter
        }
        mAdapter.addHeaderView(upperHeaderView)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (archives[position].isAll)
                UpperVideoListActivity.launch(activity, mid)
            else
                UpperChannelVideoListActivity.launch(activity, mid, archives[position].cid, archives[position].name)
        }
    }

    /**
     * 加载投稿视频封面
     */
    private fun loadSubmitData() {
        showProgressBar()
        MiaoHttp.newStringClient(
                url = BiliApiService.getUpperVideo(mid, 1, 1),
                onResponse = {
                    hideProgressBar()
                    var dataBean = Gson().fromJson(it, SubmitVideosInfo::class.java).data
                    if (dataBean.vlist.isNotEmpty()) {
                        archives.add(0, UpperChannelInfo.UperChannelData(
                                cid = 0,
                                mid = 0,
                                name = "全部投稿",
                                count = dataBean.count,
                                cover = "http:" + dataBean.vlist[0].pic,
                                archives = ArrayList<UpperChannelInfo.VideoArchives>(),
                                mtime = 0,
                                intro = "全部投稿",
                                isAll = true
                        ))
                    }
                    mAdapter.notifyDataSetChanged()
                },
                onError = {
                    hideProgressBar()
                    toast("网络错误")
                }
        )
    }

    /**
     * 加载频道数据
     */
    private fun loadChannelData() {
        showProgressBar()
        MiaoHttp.newStringClient(
                url = BiliApiService.getUpperChanne(mid),
                onResponse = {
                    hideProgressBar()
                    var dataBean = Gson().fromJson(it, UpperChannelInfo::class.java)
                    log(dataBean.data.size)
                    archives.addAll(dataBean.data)
                    mAdapter.notifyDataSetChanged()
                },
                onError = {
                    hideProgressBar()
                }
        )
    }

    /**
     * 清除列表
     */
    private fun clearList() {
        loaded = 0
        archives.clear()
        mAdapter?.notifyDataSetChanged()
    }

    /**
     * 显示加载圈
     */
    private fun showProgressBar() {
        swipe_ly.isRefreshing = true
    }

    /**
     * 隐藏加载圈
     */
    private fun hideProgressBar() {
        loaded++
        if (loaded == 2)
            swipe_ly.isRefreshing = false
    }

    companion object {
        fun launch(activity: Activity, mid: Int, name: String, pic: String) {
            val mIntent = Intent(activity, UpperDetailActivity::class.java)
            mIntent.putExtra(ConstantUtil.MID, mid)
            mIntent.putExtra(ConstantUtil.NAME, name)
            mIntent.putExtra(ConstantUtil.PIC, pic)
            activity.startActivity(mIntent)
        }
    }
}