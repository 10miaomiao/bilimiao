package com.a10miaomiao.bilimiao.fragments.rank;

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.activitys.InfoActivity
import com.a10miaomiao.bilimiao.activitys.SettingActivity
import com.a10miaomiao.bilimiao.adapter.VideoRankAdapter
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.db.KeyWordDB
import com.a10miaomiao.bilimiao.db.PreventUpperDB
import com.a10miaomiao.bilimiao.entity.VideoRankInfo
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import com.a10miaomiao.bilimiao.utils.ThemeHelper
import com.a10miaomiao.bilimiao.views.LoadMoreView
import com.a10miaomiao.bilimiao.views.RankOrdersPopupWindow
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_rank_all.*

/**
 * Created by 10喵喵 on 2017/12/1.
 */

class VideoRankFragment : BaseFragment() {

    override var layoutResId = R.layout.fragment_rank_all

    private var isAll = true
    private var dayNum = 3
    private var rid = 0

    private var archives = ArrayList<VideoRankInfo.VideoInfo>()
    private var loadMoreView: LoadMoreView? = null
    private var mAdapter: VideoRankAdapter? = null

    var api: ((isall: Boolean, dayNum: Int, rid: Int) -> String)? = null

    val keywordDB: KeyWordDB by lazy {
        KeyWordDB(activity, KeyWordDB.DB_NAME, null, 1)
    }
    val upperDB: PreventUpperDB by lazy {
        PreventUpperDB(activity, PreventUpperDB.DB_NAME, null, 1)
    }
    lateinit var pKeywords: ArrayList<String>
    lateinit var pUppers: ArrayList<PreventUpperDB.Upper>


    override fun finishCreateView(savedInstanceState: Bundle?) {
        loadMoreView = LoadMoreView(activity)

        pKeywords = keywordDB.queryAllHistory()
        pUppers = upperDB.queryAllHistory()

        mAdapter = VideoRankAdapter(archives)
        mAdapter?.addFooterView(loadMoreView)
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            IntentHandlerUtil.openWithPlayer(activity, IntentHandlerUtil.TYPE_VIDEO, archives[position].aid)
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            val items_selector = arrayOf("查看封面")
            AlertDialog.Builder(activity)
                    .setItems(items_selector, { dialogInterface, n ->
                        when (n) {
                            0 -> {
                                InfoActivity.launch(activity, archives[position].aid, "av")
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
        val color = ThemeHelper.getColorAccent(context)
        swipe_ly.setColorSchemeResources(color, color,color, color)

        ddm_rank.popMenu = RankOrdersPopupWindow(activity, line, arrayOf("全部投稿", "近期投稿"))
        ddm_rank.popMenu?.onCheckItemPositionChanged = { text, position ->
            ddm_rank.text = text
            isAll = position == 0
            clearList()
            loadData()
        }

        ddm_duration.popMenu = RankOrdersPopupWindow(activity, line, arrayOf("三日排行", "日排行", "周排行","月排行"))
        ddm_duration.popMenu?.onCheckItemPositionChanged = { text, position ->
            ddm_duration.text = text
            var dayNums = arrayOf(3, 1, 7,30)
            dayNum = dayNums[position]
            clearList()
            loadData()
        }

        ddm_region.popMenu = RankOrdersPopupWindow(activity, line, arrayOf(
                "全部分区", "动画", "国创相关", "音乐",
                "舞蹈", "游戏", "科技", "生活",
                "鬼畜", "时尚", "娱乐", "影视"
        ))
        ddm_region.popMenu?.onCheckItemPositionChanged = { text, position ->
            ddm_region.text = text
            var rid_list = arrayOf(
                    0, 1, 168, 3,
                    129, 4, 36, 160,
                    119, 155, 5, 181
            )
            rid = rid_list[position]
            clearList()
            loadData()
        }
    }

    override fun loadData() {
        showProgressBar()
        MiaoHttp.newStringClient(
                url = api!!.invoke(isAll, dayNum, rid),
                onResponse = {
                    hideProgressBar()
                    try {
                        var dataBean = Gson().fromJson(it, VideoRankInfo::class.java).rank
                        if (dataBean.code != 0) {
                            loadMoreView?.state = LoadMoreView.FAIL
                            return@newStringClient
                        }
                        archives.addAll(dataBean.list.filter {
                            for (i in pKeywords) {
                                if (i.toUpperCase() in it.title.toUpperCase()) {
                                    return@filter false
                                }
                            }
                            return@filter pUppers.none { i -> it.mid == i.uid }
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

    companion object {
        fun newInstance(api: ((isall: Boolean, dayNum: Int, rid: Int) -> String)): VideoRankFragment {
            val fragment = VideoRankFragment()
            fragment.api = api
            return fragment
        }
    }
}
