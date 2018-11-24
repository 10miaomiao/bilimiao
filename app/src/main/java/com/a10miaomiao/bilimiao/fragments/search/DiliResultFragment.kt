package com.a10miaomiao.bilimiao.fragments.search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.activitys.DiliInfoActivity
import com.a10miaomiao.bilimiao.adapter.helper.RecyclerOnScrollListener
import com.a10miaomiao.bilimiao.adapter.search.DiliResultAdapter
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.entity.SearchDiliInfo
import com.a10miaomiao.bilimiao.entity.SearchDiliInfo2
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.ThemeHelper
import com.a10miaomiao.bilimiao.views.LoadMoreView
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_search_result_upper.*
import com.google.gson.reflect.TypeToken
import com.google.gson.GsonBuilder





/**
 * Created by 10喵喵 on 2018/10/5.
 */

class DiliResultFragment : BaseFragment() {
    override var layoutResId = R.layout.fragment_search_result_upper
    var pageNum = 1
    val pageSize = 10
    val keyword: String by lazy {
        arguments!!.getString(ConstantUtil.KETWORD)
    }
    var archives = ArrayList<SearchDiliInfo>()
    var mAdapter: DiliResultAdapter? = null
    var isNullList = false
        set(value) {
            field = value
            empty_view.visibility = if (value) View.VISIBLE else View.GONE
        }
    var loadMoreView: LoadMoreView? = null

    override fun finishCreateView(savedInstanceState: Bundle?) {
        loadMoreView = LoadMoreView(activity!!)
        mAdapter = DiliResultAdapter(archives)
        mAdapter?.addFooterView(loadMoreView)
        var mLinearLayoutManager = LinearLayoutManager(activity)
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = mLinearLayoutManager
            adapter = mAdapter
        }
        val color = ThemeHelper.getColorAccent(context!!)
        swipe_ly.setColorSchemeResources(color, color, color, color)

        swipe_ly.setOnRefreshListener {
            clearList()
            loadData()
        }
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val archive = archives[position]
            DiliInfoActivity.launch(activity!!, archive.typeid)
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
                url = "http://usr.005.tv/Appapi/api_sosuoarctype",
                method = MiaoHttp.POST,
                body = mapOf(
                        "keywords" to keyword,
                        "page" to (pageNum - 1).toString(),
                        "pagesize" to pageSize.toString()
                ),
                onResponse = {
                    isNullList = false
                    hideProgressBar()
                    try {
                        val type = object : TypeToken<List<SearchDiliInfo>>() {}.type
                        val dataBean = Gson().fromJson<List<SearchDiliInfo>>(it, type)
//                        if (dataBean != 0) {
//                            loadMoreView?.state = LoadMoreView.FAIL
//                            return@newStringClient
//                        }
                        if (archives.size < pageNum * pageSize && dataBean != null) {
                            archives.addAll(dataBean)
                            mAdapter?.notifyDataSetChanged()
                        }
                        if (archives.size == 0) {
                            isNullList = true
                        } else if (dataBean.size < pageSize) {
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
        fun newInstance(keyword: String): DiliResultFragment {
            val fragment = DiliResultFragment()
            val bundle = Bundle()
            bundle.putString(ConstantUtil.KETWORD, keyword)
            fragment.arguments = bundle
            return fragment
        }
    }
}
