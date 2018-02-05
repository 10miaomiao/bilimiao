package com.a10miaomiao.bilimiao.fragments.rank

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.activitys.InfoActivity
import com.a10miaomiao.bilimiao.activitys.SettingActivity
import com.a10miaomiao.bilimiao.adapter.BangumiRankAdapter
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.db.KeyWordDB
import com.a10miaomiao.bilimiao.entity.BangumiRankInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import com.a10miaomiao.bilimiao.views.LoadMoreView
import com.a10miaomiao.bilimiao.views.RankOrdersPopupWindow
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_rank_all.*

/**
 * Created by 10喵喵 on 2017/12/2.
 */
class BangumiRankFragment: BaseFragment() {
    override var layoutResId = R.layout.fragment_rank_bangumi
    private var dayNum = 3
    private var region = "global"

    private var archives = ArrayList<BangumiRankInfo.BangumiInfo>()
    private var loadMoreView: LoadMoreView? = null
    private var mAdapter: BangumiRankAdapter? = null

    val keywordDB: KeyWordDB by lazy {
        KeyWordDB(activity, KeyWordDB.DB_NAME, null, 1)
    }
    lateinit var pKeywords: ArrayList<String>

    override fun finishCreateView(savedInstanceState: Bundle?) {
        loadMoreView = LoadMoreView(activity)

        pKeywords = keywordDB.queryAllHistory()

        mAdapter = BangumiRankAdapter(archives)
        mAdapter?.addFooterView(loadMoreView)
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            IntentHandlerUtil.openWithPlayer(activity, "http://bangumi.bilibili.com/anime/${archives[position].season_id}/")
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            val items_selector = arrayOf("查看封面", "修改默认播放器")
            AlertDialog.Builder(activity)
                    .setItems(items_selector, { dialogInterface, n ->
                        when (n) {
                            0 -> {
                                InfoActivity.launch(activity, archives[position].season_id, "anime")
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

        val typedValue = TypedValue()
        activity.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        swipe_ly.setColorSchemeResources(typedValue.resourceId, typedValue.resourceId,
                typedValue.resourceId, typedValue.resourceId)
        swipe_ly.setOnRefreshListener({
            clearList()
            loadData()
        })

        ddm_duration.popMenu = RankOrdersPopupWindow(activity, line, arrayOf("三日排行", "周排行"))
        ddm_duration.popMenu?.onCheckItemPositionChanged = { text, position ->
            ddm_duration.text = text
            dayNum = arrayOf(3, 7)[position]
            clearList()
            loadData()
        }

        ddm_region.popMenu = RankOrdersPopupWindow(activity, line, arrayOf(
                "番剧", "国产动画"
        ))
        ddm_region.popMenu?.onCheckItemPositionChanged = { text, position ->
            ddm_region.text = text
            region = if(position == 0) "global" else "cn"
            clearList()
            loadData()
        }
    }

    override fun loadData() {
        showProgressBar()
        MiaoHttp.newStringClient(
                url = BiliApiService.getRankBangumi(region,dayNum),
                onResponse = {
                    hideProgressBar()
                    try {
                        var m = it.indexOf("(")
                        var n = it.lastIndexOf(")")
                        var res = it.substring(m + 1,n)
                        var dataBean = Gson().fromJson(res, BangumiRankInfo::class.java)
                        if (dataBean.code != 0) {
                            loadMoreView?.state = LoadMoreView.FAIL
                            return@newStringClient
                        }
                        archives.addAll(dataBean.result.list.filter {
                            return@filter pKeywords.none { i -> i.toUpperCase() in it.title.toUpperCase() }
                        })
                        for (i in 0..(archives.size - 1)) {
                            archives[i].sort_num = i + 1
                        }
                        mAdapter?.notifyDataSetChanged()
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        loadMoreView?.state = LoadMoreView.FAIL
                    }
                },
                onError = {
                    hideProgressBar()
                    it.printStackTrace()
                    loadMoreView?.state = LoadMoreView.FAIL
                }
        )
    }

    /**
     * 清除列表
     */
    private fun clearList() {
        archives.clear()
        mAdapter?.notifyDataSetChanged()
        loadMoreView?.state = LoadMoreView.LOADING
        //isNullList = false
    }

    /**
     * 显示加载圈
     */
    private fun showProgressBar() {
        swipe_ly?.isRefreshing = true
        loadMoreView?.visibility = View.VISIBLE
        //progress.visibility = View.VISIBLE
    }

    /**
     * 隐藏加载圈
     */
    private fun hideProgressBar() {
        swipe_ly?.isRefreshing = false
        loadMoreView?.visibility = View.GONE
        //progress.visibility = View.GONE
    }
}