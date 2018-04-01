package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.DanmakuAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.dialog.EditDialog
import com.a10miaomiao.bilimiao.entity.DanmakuInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.BiliDanmukuCompressionTools
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.FileUtil
import com.a10miaomiao.bilimiao.utils.log
import kotlinx.android.synthetic.main.activity_uper_video_list.*
import kotlinx.android.synthetic.main.include_title_bar.*
import java.io.ByteArrayInputStream
import java.util.*


/**
 * Created by 10喵喵 on 2017/11/1.
 */
class DanmakuActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_danmaku
    val cid by lazy {
        intent.extras.getString(ConstantUtil.CID)
    }
    var danmakus = ArrayList<DanmakuInfo.DanmakuItem>()
    var danmakuList = ArrayList<DanmakuInfo.DanmakuItem>()
    val mAdapter by lazy {
        DanmakuAdapter(danmakus)
    }
    var isFilter = false

    var danmakuData: ByteArray? = null

    override fun initViews(savedInstanceState: Bundle?) {
        initRecyclerView()
        loadData()
        val typedValue = TypedValue()
        activity.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        swipe_ly.setColorSchemeResources(typedValue.resourceId, typedValue.resourceId,
                typedValue.resourceId, typedValue.resourceId)

        swipe_ly.setOnRefreshListener({
            danmakus.clear()
            loadData()
        })
    }

    override fun initToolBar() {
        toolbar.title = "弹幕列表"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar.inflateMenu(R.menu.danmaku)
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.search -> {
                    var ed = EditDialog()
                    ed.arguments = Bundle().apply {
                        putString("hint", "多个关键字可用空格分开")
                    }
                    ed.show(supportFragmentManager, "DanmakuActivity->EditDialog")
                    ed.onFinishInput = this::onFinishInput
                }
                R.id.save ->{
                    if(danmakuData != null){
                        try {
                            var fileName = FileUtil("弹幕文件")
                                    .saveText(danmakuData!!,cid + ".xml")
                                    .fileName
                            toast("已投喂到$fileName")
                        }catch (e: Exception){
                            toast("保存失败")
                        }
                    }else{
                        toast("弹幕还没加载出来呢(￣▽￣)")
                    }
                }
            }
            true
        }
    }

    fun onFinishInput(text: String) {
        if (text.isEmpty())
            return
        var keywords = text.split(" ")
        isFilter = true
        toolbar.title = "筛选“$text”"
        danmakus.clear()
        danmakus.addAll(danmakuList.filter {
            keywords.any { key -> key.toUpperCase() in it.text.toUpperCase() }
        })
        for (dan in danmakus)
            log(dan.text)
        mAdapter.notifyDataSetChanged()
    }


    private fun initRecyclerView() {
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        mAdapter.setOnItemClickListener { adapter, view, position ->
//            var qud = QueryUserDialog()
//            val bundle = Bundle()
//            bundle.putString("hash", danmakus[position].id)
//            qud.arguments = bundle
//            qud.show(supportFragmentManager, "---")
            DanmakuDetailsActivity.launch(activity, danmakus[position].text,danmakus[position].id)
        }
    }

    private fun loadData() {
        swipe_ly?.isRefreshing = true
        MiaoHttp.newClient(
                url = BiliApiService.getDanmakuList(cid),
                parseNetworkResponse = {
                    danmakuData = BiliDanmukuCompressionTools.decompressXML(it.body()!!.bytes())
                    DanmakuInfo.parse(ByteArrayInputStream(danmakuData))
                },
                onResponse = {
                    swipe_ly?.isRefreshing = false
                    danmakuList.addAll(it.danmakuList)
                    danmakus.addAll(it.danmakuList)
                    mAdapter.notifyDataSetChanged()
                },
                onError = {
                    swipe_ly?.isRefreshing = false
                    toast("加载失败了(。_。)")
                }
        )
    }

    override fun onBackPressed() {
        if (isFilter) {
            isFilter = false
            toolbar.title = "弹幕列表"
            danmakus.clear()
            danmakus.addAll(danmakuList)
            mAdapter.notifyDataSetChanged()
        } else {
            finish()
        }
    }

    companion object {
        fun launch(activity: Activity, cid: String) {
            val mIntent = Intent(activity, DanmakuActivity::class.java)
            mIntent.putExtra(ConstantUtil.CID, cid)
            activity.startActivity(mIntent)
        }
    }
}

