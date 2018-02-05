package com.a10miaomiao.bilimiao.fragments

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.activitys.InfoActivity
import com.a10miaomiao.bilimiao.activitys.SelectorDateActivity
import com.a10miaomiao.bilimiao.activitys.SettingActivity
import com.a10miaomiao.bilimiao.adapter.RegionTypeDetailsAdapter
import com.a10miaomiao.bilimiao.adapter.helper.RecyclerOnScrollListener
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.db.KeyWordDB
import com.a10miaomiao.bilimiao.db.PreventUpperDB
import com.a10miaomiao.bilimiao.entity.RegionTypeDetailsInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import com.a10miaomiao.bilimiao.utils.SelectorDateUtil
import com.a10miaomiao.bilimiao.utils.log
import com.a10miaomiao.bilimiao.views.LoadMoreView
import com.a10miaomiao.bilimiao.views.RankOrdersPopupWindow
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_region_details.*




/**
 * Created by 10喵喵 on 2017/9/18.
 */
class RegionTypeDetailsFragment : BaseFragment() {
    override var layoutResId: Int = R.layout.fragment_region_details
    private var timeFrom: String? = null
    private var timeTo: String? = null

    private var rid: Int? = null
    private var pageNum = 1
    private val pageSize = 10

    private var isNullList: Boolean = false  //列表为空
        set(value) {
            field = value
            img_null?.visibility = if (value!!) View.VISIBLE else View.GONE
        }
    private var rankOrder = "click"  //排行依据
    private var text_rankOrdrer = "播放量"

    var mAdapter: RegionTypeDetailsAdapter? = null
    val archives = ArrayList<RegionTypeDetailsInfo.Result>()
    var loadMoreView: LoadMoreView? = null
    var popupWindow: RankOrdersPopupWindow? = null

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
        pKeywords = keywordDB.queryAllHistory()
        pUppers = upperDB.queryAllHistory()
        loadMoreView = LoadMoreView(activity)
        initRecyclerView()
        initListener()
    }

    override fun initData(savedInstanceState: Bundle?) {
        rid = arguments.getInt(ConstantUtil.EXTRA_RID)
        reTime()
        log(rid!!)
    }

    /**
     * 设置listener的操作
     */
    private fun initListener() {
        popupWindow = RankOrdersPopupWindow(activity, line, arrayOf("播放数", "评论数", "收藏数", "硬币数", "弹幕数"))
        popupWindow?.onCheckItemPositionChanged = { text, position ->
            text_rankOrdrer = text
            tv_rank_order.text = text_rankOrdrer
            val rankOrders = arrayOf("click", "scores", "stow", "coin", "dm")
            rankOrder = rankOrders[position]
            clearList()
            loadData()
        }
        tv_rank_order.popMenu = popupWindow

        selector_date.setOnClickListener {
            SelectorDateActivity.launch(activity)
        }

        val typedValue = TypedValue()
        activity.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        swipe_ly.setColorSchemeResources(typedValue.resourceId, typedValue.resourceId,
                typedValue.resourceId, typedValue.resourceId)

        swipe_ly.setOnRefreshListener({
            //            Observable.timer(1000, TimeUnit.MILLISECONDS)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({aLong ->
//                        hideProgressBar()
//                     })
            clearList()
            loadData()
        })

    }

    private fun initRecyclerView() {
        mAdapter = RegionTypeDetailsAdapter(archives)
        var mLinearLayoutManager = LinearLayoutManager(activity)
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = mLinearLayoutManager
            adapter = mAdapter
        }
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            IntentHandlerUtil.openWithPlayer(activity, "http://www.bilibili.com/video/av${archives[position].id}/")
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            val items_selector = arrayOf("查看封面", "修改默认播放器")
            AlertDialog.Builder(activity)
                    .setItems(items_selector, { dialogInterface, n ->
                        when (n) {
                            0 -> {
                                InfoActivity.launch(activity, archives[position].id, "av")
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
        mAdapter?.addFooterView(loadMoreView)

        recycle.addOnScrollListener(object : RecyclerOnScrollListener(mLinearLayoutManager, 1) {
            override fun onLoadMore() {
                if (loadMoreView!!.isLoading)
                    return
                if (loadMoreView!!.state != LoadMoreView.FAIL)
                    pageNum++
                loadData()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        //判断日期是否有修改
        if (reTime() && (isNullList || archives.size != 0)) {
            clearList()
            loadData()
        }
    }

    /**
     * 刷新日期，有更改返回true
     */
    private fun reTime(): Boolean {
        val selectorDateUtil = SelectorDateUtil(activity)
        if (timeFrom != null || timeTo != null) {
            if (timeFrom == selectorDateUtil.timeFrom && timeTo == selectorDateUtil.timeTo) {
                selector_date?.text = SelectorDateUtil.formatDate(timeFrom!!, "-") + "至" +
                        SelectorDateUtil.formatDate(timeTo!!, "-")
                return false
            }
        }
        timeFrom = selectorDateUtil.timeFrom!!
        timeTo = selectorDateUtil.timeTo!!
        selector_date?.text = SelectorDateUtil.formatDate(timeFrom!!, "-") + "至" +
                SelectorDateUtil.formatDate(timeTo!!, "-")
        return true
    }

    /**
     * 懒加载
     */
    override fun loadData() {
        if (rid == null)
            return
        showProgressBar()
        MiaoHttp.newStringClient(
                url = BiliApiService.getRegionTypeVideoList(rid!!, rankOrder, pageNum, pageSize, timeFrom!!, timeTo!!),
                onResponse = {
                    isNullList = false
                    hideProgressBar()
                    var dataBean = Gson().fromJson(it, RegionTypeDetailsInfo::class.java)
                    if (dataBean.code != 0) {
                        loadMoreView?.state = LoadMoreView.FAIL
                        return@newStringClient
                    }
                    if (listSize < pageNum * pageSize && dataBean.result != null) {
                        //屏蔽关键字
                        archives.addAll(dataBean.result.filter {
                            for (i in pKeywords) {
                                if (i.toUpperCase() in it.title.toUpperCase()) {
                                    pNumber++
                                    return@filter false
                                }
                            }
                            for (i in pUppers) {
                                if (it.mid == i.uid) {
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
                    } else if (dataBean.result.size < pageSize) {
                        loadMoreView?.state = LoadMoreView.NOMORE
                    }
                },
                onError = {
                    isNullList = false
                    hideProgressBar()
                    loadMoreView?.state = LoadMoreView.FAIL
                }
        )
    }

    /**
     * 清除列表
     */
    private fun clearList() {
        pKeywords = keywordDB.queryAllHistory()
        pUppers = upperDB.queryAllHistory()

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
        fun newInstance(rid: Int): RegionTypeDetailsFragment {
            val fragment = RegionTypeDetailsFragment()
            val bundle = Bundle()
            bundle.putInt(ConstantUtil.EXTRA_RID, rid)
            fragment.arguments = bundle
            return fragment
        }
    }
}