package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.UpperChannelVideoListAdapter
import com.a10miaomiao.bilimiao.adapter.helper.RecyclerOnScrollListener
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.entity.UpperChannelVideoInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import com.a10miaomiao.bilimiao.utils.ThemeHelper
import com.a10miaomiao.bilimiao.views.LoadMoreView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_uper_video_list.*
import kotlinx.android.synthetic.main.include_title_bar.*

/**
 * Created by 10喵喵 on 2017/10/31.
 */
class UpperChannelVideoListActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_uper_video_list

    var archives = ArrayList<UpperChannelVideoInfo.VideoArchives>()
    private var pageNum = 1
    private val pageSize = 10
    private val mid by lazy {
        intent.extras.getInt(ConstantUtil.MID)
    }
    private val cid by lazy {
        intent.extras.getInt(ConstantUtil.CID)
    }
    private val mAdapter by lazy {
        UpperChannelVideoListAdapter(archives)
    }
    private val mLoadMoreView by lazy {
        LoadMoreView(activity)
    }
    private val mLinearLayoutManager by lazy {
        LinearLayoutManager(activity)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        initRecyclerView()
        loadData()

        val color = ThemeHelper.getColorAccent(this)
        swipe_ly.setColorSchemeResources(color, color,color, color)

        swipe_ly.setOnRefreshListener({
            clearList()
            loadData()
        })
    }

    private fun initRecyclerView() {
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = mLinearLayoutManager
            adapter = mAdapter
        }
        mAdapter.setOnItemClickListener { adapter, view, position ->
            IntentHandlerUtil.openWithPlayer(activity, IntentHandlerUtil.TYPE_VIDEO, archives[position].aid.toString())
        }
        mAdapter.setOnItemLongClickListener { adapter, view, position ->
            val items_selector = arrayOf("查看封面")
            AlertDialog.Builder(activity)
                    .setItems(items_selector, { dialogInterface, n ->
                        when (n) {
                            0 -> {
                                InfoActivity.launch(activity, archives[position].aid.toString(), "av")
                            }
                            1 -> {
                                SettingActivity.selectPalyer(activity)
                            }
                        }

                    })
                    .setCancelable(true)
                    .show()
            true
        }
        mAdapter.addFooterView(mLoadMoreView)
        recycle.addOnScrollListener(object : RecyclerOnScrollListener(mLinearLayoutManager, 1) {
            override fun onLoadMore() {
                if (mLoadMoreView!!.isLoading)
                    return
                if (mLoadMoreView!!.state != LoadMoreView.FAIL)
                    pageNum++
                loadData()
            }
        })

    }

    override fun initToolBar() {
        toolbar.title = intent.extras.getString(ConstantUtil.NAME)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadData() {
        showProgressBar()
        //val url = "https://space.bilibili.com/ajax/member/getSubmitVideos?mid=$mid&page=$pageNum&pagesize=$pageSize"
        MiaoHttp.newStringClient(
                url = BiliApiService.getUpperChanneVideo(mid, cid, pageNum, pageSize),
                onResponse = {
                    hideProgressBar()
                    var dataBean = Gson().fromJson(it, UpperChannelVideoInfo::class.java).data.list
                    if (dataBean.archives == null) {
                        mLoadMoreView.state = LoadMoreView.NOMORE
                    } else {
                        archives.addAll(dataBean.archives)
                        mAdapter.notifyDataSetChanged()
                        if (dataBean.archives.size < pageSize)
                            mLoadMoreView.state = LoadMoreView.NOMORE
                    }
                },
                onError = {
                    hideProgressBar()
                    mLoadMoreView.state = LoadMoreView.FAIL
                }
        )
    }

    /**
     * 清除列表
     */
    private fun clearList() {
        archives.clear()
        mAdapter?.notifyDataSetChanged()
        pageNum = 1
        mLoadMoreView?.state = LoadMoreView.LOADING
        //isNullList = false
    }

    /**
     * 显示加载圈
     */
    private fun showProgressBar() {
        swipe_ly?.isRefreshing = true
        mLoadMoreView?.visibility = View.VISIBLE
    }

    /**
     * 隐藏加载圈
     */
    private fun hideProgressBar() {
        swipe_ly?.isRefreshing = false
        mLoadMoreView?.visibility = View.GONE
    }

    companion object {
        fun launch(activity: Activity, mid: Int, cid: Int, name: String) {
            val mIntent = Intent(activity, UpperChannelVideoListActivity::class.java)
            mIntent.putExtra(ConstantUtil.MID, mid)
            mIntent.putExtra(ConstantUtil.CID, cid)
            mIntent.putExtra(ConstantUtil.NAME, name)
            activity.startActivity(mIntent)
        }
    }
}