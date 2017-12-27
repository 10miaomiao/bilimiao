package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.UpperChannelVideoListAdapter
import com.a10miaomiao.bilimiao.adapter.helper.RecyclerOnScrollListener
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.entity.UpperChannelVideoInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.SettingUtil
import com.a10miaomiao.bilimiao.views.LoadMoreView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_uper_video_list.*
import kotlinx.android.synthetic.main.include_title_bar.*
import java.lang.Exception

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
            //应用程序的包名
            val packages = arrayOf("tv.danmaku.bili"
                    , "com.bilibili.app.in"
                    , "com.bilibili.app.blue"
                    , "tv.danmaku.bilixl")
            //要启动的Activity
            val activitys = arrayOf("tv.danmaku.bili.ui.intent.IntentHandlerActivity"
                    , "tv.danmaku.bili.ui.intent.IntentHandlerActivity"
                    , "tv.danmaku.bili.ui.intent.IntentHandlerActivity"
                    , "tv.danmaku.bili.activities.videopagelist.VideoPageListActivity")
            //播放器
            var player = SettingUtil.getInt(activity, ConstantUtil.PLAYER, ConstantUtil.PLAYER_BILI)
            player = if (player in 0..2) player else ConstantUtil.PLAYER_BILI
            val componetName = ComponentName(packages[player], activitys[player])
            val intent = Intent()
            intent.data = Uri.parse("http://www.bilibili.com/video/av${archives[position].aid}/")
            intent.component = componetName

            if (activity.packageManager.resolveActivity(intent, 0) != null) {  //存在
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(activity, "打开B站失败了 o((>ω< ))o", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            } else {
                SettingActivity.selectPalyer(activity)
            }
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
        toolbar.title = intent.extras.getString(ConstantUtil.NAME)
        toolbar.setNavigationIcon(R.mipmap.ic_back)
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