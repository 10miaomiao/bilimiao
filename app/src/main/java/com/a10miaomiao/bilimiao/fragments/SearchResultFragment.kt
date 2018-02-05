package com.a10miaomiao.bilimiao.fragments

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.activitys.InfoActivity
import com.a10miaomiao.bilimiao.activitys.SettingActivity
import com.a10miaomiao.bilimiao.adapter.SearchResultAdapter
import com.a10miaomiao.bilimiao.adapter.helper.RecyclerOnScrollListener
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.db.KeyWordDB
import com.a10miaomiao.bilimiao.db.PreventUpperDB
import com.a10miaomiao.bilimiao.entity.SearchArchiveInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import com.a10miaomiao.bilimiao.views.LoadMoreView
import com.a10miaomiao.bilimiao.views.RankOrdersPopupWindow
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_search_result.*


/**
 * Created by 10喵喵 on 2017/11/7.
 */
class SearchResultFragment : BaseFragment() {
    override var layoutResId = R.layout.fragment_search_result
    var pageNum = 1
    val pageSize = 10
    val keyword: String by lazy {
        arguments.getString(ConstantUtil.KETWORD)
    }
    var order = "default"
    var duration = 0
    var rid = 0

    var archives = ArrayList<SearchArchiveInfo.DataBean.ItemsBean.ArchiveBean>()
    var mAdapter: SearchResultAdapter? = null
    var isNullList = false
        set(value) {
            field = value
            empty_view.visibility = if (value) View.VISIBLE else View.GONE
        }
    var loadMoreView: LoadMoreView? = null
    var rankOrdersPop: RankOrdersPopupWindow? = null
    var regionPop: RankOrdersPopupWindow? = null
    var durationPop: RankOrdersPopupWindow? = null

    val keywordDB: KeyWordDB by lazy {
        KeyWordDB(activity, KeyWordDB.DB_NAME, null, 1)
    }
    val upperDB: PreventUpperDB by lazy {
        PreventUpperDB(activity, PreventUpperDB.DB_NAME, null, 1)
    }
    lateinit var pKeywords: ArrayList<String>
    lateinit var pUppers: ArrayList<PreventUpperDB.Upper>
    var pNumber = 0   //屏蔽数量
    var listSize: Int
        set(value) {

        }
        get() = archives.size + pNumber


    override fun finishCreateView(savedInstanceState: Bundle?) {
        loadMoreView = LoadMoreView(activity)
        mAdapter = SearchResultAdapter(archives)
        mAdapter?.addFooterView(loadMoreView)
        var mLinearLayoutManager = LinearLayoutManager(activity)
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = mLinearLayoutManager
            adapter = mAdapter
        }
        pKeywords = keywordDB.queryAllHistory()
        pUppers = upperDB.queryAllHistory()
        val typedValue = TypedValue()
        activity.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        swipe_ly.setColorSchemeResources(typedValue.resourceId, typedValue.resourceId,
                typedValue.resourceId, typedValue.resourceId)

        swipe_ly.setOnRefreshListener({
            clearList()
            loadData()
        })
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            IntentHandlerUtil.openWithPlayer(activity, "http://www.bilibili.com/video/av${archives[position].param}/")
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            val items_selector = arrayOf("查看封面", "修改默认播放器")
            AlertDialog.Builder(activity)
                    .setItems(items_selector, { dialogInterface, n ->
                        when (n) {
                            0 -> {
                                InfoActivity.launch(activity, archives[position].param, "av")
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
        recycle.addOnScrollListener(object : RecyclerOnScrollListener(mLinearLayoutManager, 1) {
            override fun onLoadMore() {
                if (loadMoreView!!.isLoading)
                    return
                if (loadMoreView!!.state != LoadMoreView.FAIL)
                    pageNum++
                loadData()
            }
        })


        //筛选排行依据
        rankOrdersPop = RankOrdersPopupWindow(activity, line, arrayOf("默认排序", "相关度", "新发布", "播放多", "弹幕多", "评论多", "收藏多"))
        rankOrdersPop?.onCheckItemPositionChanged = { text, position ->
            tv_rank_order.text = text
            var orders = arrayOf("default", "ranklevel", "pubdate", "click", "dm", "scores", "stow")
            order = orders[position]
            clearList()
            loadData()
        }
        tv_rank_order.popMenu = rankOrdersPop
        //筛选时长
        durationPop = RankOrdersPopupWindow(activity, line, arrayOf("全部时长", "0-10分钟", "10-30分钟", "30-60分钟", "60分钟+"))
        tv_duration.popMenu = durationPop
        durationPop?.onCheckItemPositionChanged = { text, position ->
            tv_duration.text = text
            duration = position//刚好对应0、1、2、3、4 -> all、0-10min、10-30min...
            clearList()
            loadData()
        }
        //筛选分区
        regionPop = RankOrdersPopupWindow(activity, line, arrayOf(
                        "全部分区","番剧", "国创", "动画",
                        "音乐", "舞蹈", "游戏", "科技",
                        "生活", "鬼畜", "时尚", "广告",
                        "娱乐", "电影", "电视剧"
                )
        )
        tv_region.popMenu = regionPop
        regionPop?.onCheckItemPositionChanged = { text, position ->
            tv_region.text = text
            var rid_list = arrayOf(
                    0,13,167,1,
                    3,129,4,36,
                    160,119,155,165,
                    5,23,11
            )
            rid = rid_list[position]
            clearList()
            loadData()
        }

    }

    override fun loadData() {
        showProgressBar()
        MiaoHttp.newStringClient(
                url = BiliApiService.getSearchArchive(keyword, pageNum, pageSize, order,duration, rid),
                onResponse = {
                    isNullList = false
                    hideProgressBar()
                    try {
                        var dataBean = Gson().fromJson(it, SearchArchiveInfo::class.java)
                        if (dataBean.code != 0) {
                            loadMoreView?.state = LoadMoreView.FAIL
                            return@newStringClient
                        }
                        if (listSize < pageNum * pageSize && dataBean.data.items.archive != null) {
                            archives.addAll(dataBean.data.items.archive.filter {
                                for (i in pKeywords) {
                                    if (i.toUpperCase() in it.title.toUpperCase()) {
                                        pNumber++
                                        return@filter false
                                    }
                                }
                                for (i in pUppers) {
                                    if (it.author == i.name) {
                                        pNumber++
                                        return@filter false
                                    }
                                }
                                return@filter true
                            })
                            mAdapter?.notifyDataSetChanged()
                        }
                        if (archives.size == 0) {
                            isNullList = true
                        } else if (dataBean.data.items.archive.size < pageSize) {
                            loadMoreView?.state = LoadMoreView.NOMORE
                        }
                    } catch (e: JsonSyntaxException) {
                        loadMoreView?.state = LoadMoreView.FAIL
                    }
                },
                onError = {
                    it.printStackTrace()
                    hideProgressBar()
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
        pageNum = 1
        loadMoreView?.state = LoadMoreView.LOADING
        isNullList = false
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

    companion object {
        fun newInstance(keyword: String): SearchResultFragment {
            val fragment = SearchResultFragment()
            val bundle = Bundle()
            bundle.putString(ConstantUtil.KETWORD, keyword)
            fragment.arguments = bundle
            return fragment
        }
    }
}