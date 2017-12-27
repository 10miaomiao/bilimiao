package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.UpperVideoListAdapter
import com.a10miaomiao.bilimiao.adapter.helper.RecyclerOnScrollListener
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.entity.SubmitVideosInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import com.a10miaomiao.bilimiao.views.LoadMoreView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_uper_video_list.*
import kotlinx.android.synthetic.main.include_title_bar.*

/**
 * Created by 10喵喵 on 2017/10/30.
 */
class UpperVideoListActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_uper_video_list

    var archives = ArrayList<SubmitVideosInfo.VideoInfo>()
    private var pageNum = 1
    private val pageSize = 10
    private val mid by lazy {
        intent.extras.getInt(ConstantUtil.MID)
    }
    private val mAdapter by lazy {
        UpperVideoListAdapter(archives)
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
        swipe_ly.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent,
                R.color.colorAccent, R.color.colorAccent)

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
            IntentHandlerUtil.openWithPlayer(activity, "http://www.bilibili.com/video/av${archives[position].aid}/")
        }
        mAdapter.setOnItemLongClickListener { adapter, view, position ->
            val items_selector = arrayOf("查看封面", "修改默认播放器")
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
        toolbar.title = "全部投稿"
        toolbar.setNavigationIcon(R.mipmap.ic_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadData() {
        showProgressBar()
        MiaoHttp.newStringClient(
                url = BiliApiService.getUpperVideo(mid, pageNum, pageSize),
                onResponse = {
                    hideProgressBar()
                    var dataBean = Gson().fromJson(it, SubmitVideosInfo::class.java).data
                    if (dataBean.vlist == null) {
                        mLoadMoreView.state = LoadMoreView.NOMORE
                        return@newStringClient
                    }
                    archives.addAll(dataBean.vlist)
                    mAdapter.notifyDataSetChanged()
                    if (dataBean.vlist.size < pageSize)
                        mLoadMoreView.state = LoadMoreView.NOMORE
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
        fun launch(activity: Activity, mid: Int) {
            val mIntent = Intent(activity, UpperVideoListActivity::class.java)
            mIntent.putExtra(ConstantUtil.MID, mid)
            activity.startActivity(mIntent)
        }
    }
}