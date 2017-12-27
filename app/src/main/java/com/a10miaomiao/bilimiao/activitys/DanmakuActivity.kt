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
import com.a10miaomiao.bilimiao.dialog.QueryUserDialog
import com.a10miaomiao.bilimiao.entity.DanmakuInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.BiliDanmukuCompressionTools
import com.a10miaomiao.bilimiao.utils.ConstantUtil
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
        toolbar.setNavigationIcon(R.mipmap.ic_back)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar.inflateMenu(R.menu.search)
        toolbar.setOnMenuItemClickListener {
            var ed = EditDialog()
            ed.arguments = Bundle().apply {
                putString("hint", "多个关键字可用空格分开")
            }
            ed.show(supportFragmentManager, "DanmakuActivity->EditDialog")
            ed.onFinishInput = this::onFinishInput
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
            var b = true
            for (key in keywords) {
                if (it.text.indexOf(key) == -1) {
                    b = false
                }
            }
            b
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
            var qud = QueryUserDialog()
            val bundle = Bundle()
            bundle.putString("hash", danmakus[position].id)
            qud.arguments = bundle
            qud.show(supportFragmentManager, "---")
        }
    }

    private fun loadData() {
        swipe_ly?.isRefreshing = true
        MiaoHttp.newClient<DanmakuInfo>(
                url = BiliApiService.getDanmakuList(cid),
                parseNetworkResponse = {
                    DanmakuInfo.parse(
                            ByteArrayInputStream(BiliDanmukuCompressionTools.decompressXML(it.body().bytes()))
                    )
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
        /*val okHttpClient = OkHttpClient()
        val request = okhttp3.Request.Builder()
                .url(url)
                .removeHeader("Content-Encoding")
                .removeHeader("Content-Length")
                .build()
        okHttpClient.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {

                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        val stream = ByteArrayInputStream(BiliDanmukuCompressionTools.decompressXML(response!!.body().bytes()))
                        val info = DanmakuInfo.parse(stream)
                        danmakus.addAll(info.danmakuList)
                        mAdapter.notifyDataSetChanged()
                        //log(response!!.body().string())
                        //var stream = GZIPInputStream(response!!.body().byteStream())
//                        var stream = DeflaterInputStream(response!!.body().byteStream())
//                        //DanmakuInfo.parse(stream)
//                        var reader = BufferedReader(InputStreamReader(stream,"utf-8"))
//                        var sb = StringBuffer()
//                        var str = reader.readLine()
//                        while (str != null)
//                        {
//                            sb.append(str).append("\n")
//                            str = reader.readLine()
//                        }
//                        val arrayOutputStream = ByteArrayOutputStream()
//                        val buffer = ByteArray(1024)
//                        var len= gzip.read(buffer)
//                        while (len != -1) {
//                            arrayOutputStream.write(buffer, 0, len)
//                            len= gzip.read(buffer)
//                        }
//                        stream.close()
//                        reader.close()
//                        arrayOutputStream.close()
//                        val str = String(arrayOutputStream.toByteArray())
//                        log(sb.toString())
                    }
query, sign := EncodeSign(map[string]string{
		"cid":            strconv.Itoa(cid),
		"from":           "miniplay",
		"player":         "1",
		"otype":          "json",
		"type":           "mp4",
		"quality":        strconv.Itoa(quality),
		"appkey":         "f3bb208b3d081dc8",
	}, "1c15888dc316e05a15fdd0a02ed6584f")
query = cid=26433217&from=miniplay&player=1&otype=json&type=mp4&quality=1&appkey=appkey
sing = query+"1c15888dc316e05a15fdd0a02ed6584f"
url := "htt://interface.bilibili.com/playurl?&" + query + "&sign=" + sign
                })*/
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

