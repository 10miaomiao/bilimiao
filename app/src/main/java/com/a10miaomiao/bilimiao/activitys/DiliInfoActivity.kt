package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.RankOrdersAdapter
import com.a10miaomiao.bilimiao.adapter.VideoPagesAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.entity.DiliAnimeInfo
import com.a10miaomiao.bilimiao.entity.VideoDetailsInfo
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.TextUtil
import com.a10miaomiao.bilimiao.utils.log
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dili_info.*
import kotlinx.android.synthetic.main.include_title_bar.*
import org.json.JSONObject
import org.json.JSONTokener

class DiliInfoActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_dili_info
    private val typeid by lazy {
        intent.getStringExtra(ConstantUtil.ID)
    }
    private var info: DiliAnimeInfo? = null
    val pages = ArrayList<VideoDetailsInfo.VideoPageInfo>()
    val pagesAdapter = VideoPagesAdapter(pages)
    var lineUrls = ArrayList<String>()
    val lines = ArrayList<String>()
    val lineAdapter = RankOrdersAdapter(lines)


    override fun initViews(savedInstanceState: Bundle?) {
        recycle.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(activity, 4)
            adapter = pagesAdapter
        }
        pagesAdapter.setOnItemClickListener { adapter, view, position ->
            H5PlayerActivity.launch(activity!!
                    , pages[position].cid.toString()
                    , lineUrls[lineAdapter.checkItemPosition])
        }

        recycle_line.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 5)
            adapter = lineAdapter
        }
        lineAdapter.setOnItemClickListener{ adapter, view, position ->
            lineAdapter.checkItemPosition = position
        }
        loadData()
        loadPagesData()
        loadConfig()
    }

    override fun initToolBar() {
        toolbar.title = "番剧详情"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadData() {
        MiaoHttp.newStringClient(
                url = "http://usr.005.tv/Appapi/api_getarctypeinfo",
                method = MiaoHttp.POST,
                body = mapOf(
                        "typeid" to typeid
                ),
                onResponse = {
                    info = Gson().fromJson(it, DiliAnimeInfo::class.java)
                    updateView()
                },
                onError = {

                }
        )
    }

    private fun loadPagesData() {
        MiaoHttp.newStringClient(
                url = "http://usr.005.tv/Appapi/api_getarchive",
                method = MiaoHttp.POST,
                body = mapOf(
                        "typeid" to typeid
                ),
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    val jsonArray = (jsonParser.nextValue() as JSONObject).getJSONArray("list")
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        pages.add(VideoDetailsInfo.VideoPageInfo(
                                cid = jsonObject.getString("id").toLong(),
                                title = jsonObject.getString("writer")
                        ))
                    }
                    pagesAdapter.notifyDataSetChanged()
                },
                onError = {

                }
        )
    }

    /**
     * 线路信息
     */
    private fun loadConfig(){
        MiaoHttp.newStringClient(
                url = "http://usr.005.tv/Appapi/api_getconfig",
                method = MiaoHttp.POST,
                body = mapOf(
                        "id" to "1"
                ),
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    val jsonArray = (jsonParser.nextValue() as JSONObject).getJSONArray("line")
                    for (i in 0 until jsonArray.length()) {
                        val s = jsonArray.getString(i)
                        lines.add("线路${i + 1}")
                        lineUrls.add(s.replace(" ", ""))
                    }
                    lineAdapter.notifyDataSetChanged()
                },
                onError = {
                    it.printStackTrace()
                }
        )
    }

    private fun updateView() = info?.apply {
        mTitleTv.text = typename
        Glide.with(activity)
                .load(suoluetudizhi)
                .dontAnimate()
                .into(mCoverIV)
        mRegionTv.text = "地区：$diqu"
        mAgeTv.text = "年代：$niandai"
        mTagsTv.text = "标签：$biaoqian"
        mIntroductionTV.text = description
    }

    private fun getPlayUrl(id: String) {
        log(id)
        MiaoHttp.newStringClient(
                url = "http://usr.005.tv/Appapi/api_getarchives",
                method = MiaoHttp.POST,
                body = mapOf(
                        "id" to id
                ),
                onResponse = {
                    log(it)
                }
        )
    }

    companion object {
        fun launch(activity: Activity, id: String) {
            val mIntent = Intent(activity, DiliInfoActivity::class.java)
            mIntent.putExtra(ConstantUtil.ID, id)
            activity.startActivity(mIntent)
        }
    }

}
