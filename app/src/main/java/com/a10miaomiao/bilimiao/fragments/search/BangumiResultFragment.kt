package com.a10miaomiao.bilimiao.fragments.search

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.activitys.InfoActivity
import com.a10miaomiao.bilimiao.activitys.SettingActivity
import com.a10miaomiao.bilimiao.adapter.helper.RecyclerOnScrollListener
import com.a10miaomiao.bilimiao.adapter.search.BangumiResultAdapter
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.db.KeyWordDB
import com.a10miaomiao.bilimiao.entity.SearchBangumiInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import com.a10miaomiao.bilimiao.utils.ThemeHelper
import com.a10miaomiao.bilimiao.views.LoadMoreView
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_search_result_upper.*

/**
 * Created by 10喵喵 on 2017/11/8.
 */

class BangumiResultFragment : BaseFragment() {
    override var layoutResId = R.layout.fragment_search_result_upper
    var pageNum = 1
    val pageSize = 10
    val keyword: String by lazy {
        arguments!!.getString(ConstantUtil.KETWORD)
    }
    var archives = ArrayList<SearchBangumiInfo.DataBean.ItemsBean>()
    var mAdapter: BangumiResultAdapter? = null
    var isNullList = false
        set(value) {
            field = value
            empty_view.visibility = if (value) View.VISIBLE else View.GONE
        }
    var loadMoreView: LoadMoreView? = null
    lateinit var pKeywords: ArrayList<String>
    val keywordDB: KeyWordDB by lazy {
        KeyWordDB(activity!!, KeyWordDB.DB_NAME, null, 1)
    }
    var pNumber = 0   //屏蔽数量
    var listSize: Int
        set(value) {

        }
        get() = archives.size + pNumber

    override fun finishCreateView(savedInstanceState: Bundle?) {
        loadMoreView = LoadMoreView(activity!!)
        mAdapter = BangumiResultAdapter(archives)
        mAdapter?.addFooterView(loadMoreView)
        var mLinearLayoutManager = LinearLayoutManager(activity)
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = mLinearLayoutManager
            adapter = mAdapter
        }
        pKeywords = keywordDB.queryAllHistory()
        val color = ThemeHelper.getColorAccent(context!!)
        swipe_ly.setColorSchemeResources(color, color,color, color)

        swipe_ly.setOnRefreshListener({
            clearList()
            loadData()
        })
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            IntentHandlerUtil.openWithPlayer(activity!!, IntentHandlerUtil.TYPE_BANGUMI, archives[position].param)
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            val items_selector = arrayOf("查看封面")
            AlertDialog.Builder(activity!!)
                    .setItems(items_selector, { dialogInterface, n ->
                        when (n) {
                            0 -> {
                                InfoActivity.launch(activity!!, archives[position].param, "anime")
                            }
                            1 -> {
                                SettingActivity.selectPalyer(activity!!)
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
    }

    override fun loadData() {
        showProgressBar()
        MiaoHttp.newStringClient(
                url = BiliApiService.getSearchBangumi(keyword,pageNum,pageSize),
                onResponse = {
                    isNullList = false
                    hideProgressBar()
                    try {
                        var dataBean = Gson().fromJson(it, SearchBangumiInfo::class.java)
                        if (dataBean.code != 0) {
                            loadMoreView?.state = LoadMoreView.FAIL
                            return@newStringClient
                        }
                        if (listSize < pageNum * pageSize && dataBean.data.items != null) {
                            archives.addAll(dataBean.data.items.filter {
                                for (i in pKeywords) {
                                    if (i.toUpperCase() in it.title.toUpperCase()) {
                                        pNumber++
                                        false
                                    }
                                }
                                true
                            })
                            mAdapter?.notifyDataSetChanged()
                        }
                        if (archives.size == 0) {
                            isNullList = true
                        } else if (dataBean.data.items.size < pageSize) {
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
        fun newInstance(keyword: String): BangumiResultFragment {
            val fragment = BangumiResultFragment()
            val bundle = Bundle()
            bundle.putString(ConstantUtil.KETWORD, keyword)
            fragment.arguments = bundle
            return fragment
        }
    }
}
