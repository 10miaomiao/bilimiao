package com.a10miaomiao.bilimiao.activitys

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.PopupMenu
import android.text.ClipboardManager
import android.view.MenuItem
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.HomeRegionItemAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.dialog.SearchBoxFragment
import com.a10miaomiao.bilimiao.entity.HomeRegionInfo
import com.a10miaomiao.bilimiao.entity.MiaoAdInfo
import com.a10miaomiao.bilimiao.entity.RegionTypesInfo
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import com.a10miaomiao.bilimiao.utils.SelectorDateUtil
import com.a10miaomiao.bilimiao.utils.ThemeHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


/**
 * Created by 10喵喵 on 2017/9/16.
 */
class MainActivity : BaseActivity() {
    private var regionTypes = ArrayList<RegionTypesInfo.DataBean>()
    private var info: Info? = null
    private var searchFragment = SearchBoxFragment.newInstance()

    private val homeRegionList = ArrayList<HomeRegionInfo>()
    private val homeRegionAdapter = HomeRegionItemAdapter(homeRegionList)
    private val homeMoreList = arrayListOf(
            HomeRegionInfo("视频下载", R.drawable.ic_home_more_download),
            HomeRegionInfo("排行榜", R.drawable.ic_home_more_rank),
            HomeRegionInfo("屏蔽设置", R.drawable.ic_home_more_prevent)
    )
    private val homeMoreAdapter = HomeRegionItemAdapter(homeMoreList)

    override var layoutResID = R.layout.activity_main

    override fun initViews(savedInstanceState: Bundle?) {
        recycle_region.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(activity, 5)
            adapter = homeRegionAdapter
        }
        homeRegionAdapter.setOnItemClickListener { adapter, view, position ->
            RegionTypeDetailsActivity.launch(activity, regionTypes[position])
        }

        recycle_more.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(activity, 4)
            adapter = homeMoreAdapter
        }
        homeMoreAdapter.setOnItemClickListener { adapter, view, position ->
            when(position){
                0 -> DownloadActivity.launch(activity)
                1 -> RankActivity.launch(activity)
                2 -> EditPreventKeywordActivity.launch(activity)
            }
        }

        searchFragment.onSearchClick = {
            if (it == "/封印解除") {
                ThemeHelper.setTheme(activity, 6)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                overridePendingTransition(0, 0)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                var d = search(it)
                if (d == null) {
                    SearchActivity.launch(activity, it)
                } else {
                    InfoActivity.launch(activity, d.aid, d.type)
                }
            }
            true
        }

        find_card_view.setOnClickListener {
            if (info == null) {
                find_card_view.visibility = View.GONE
            } else {
                InfoActivity.launch(activity, info!!.aid, info!!.type)
            }
        }
        tv_search.setOnClickListener {
            searchFragment.show(supportFragmentManager, "MainActivity->SearchBoxFragment")
        }
        iv_search.setOnClickListener {
            searchFragment.show(supportFragmentManager, "MainActivity->SearchBoxFragment")
        }
        iv_more.setOnClickListener {
            val popupMenu = PopupMenu(activity, it)
            popupMenu.inflate(R.menu.main)
            popupMenu.setOnMenuItemClickListener(this::onMenuItemClick)
            popupMenu.show()
        }
        layout_time_line.setOnClickListener {
            SelectorDateActivity.launch(activity)
        }

        mButton.setOnClickListener {
            var mIntent = Intent(activity, PlayerActivity::class.java)
            startActivity(mIntent)
        }
        searchClipboard()
        setRegionCard()
        setTimeLineCard()
        getAd()
    }

    /**
     * 设置分区卡片
     */
    private fun setRegionCard() {
        //随机显示标题
        val titles = arrayOf("时光姬", "时光基", "时光姬", "时光姬")
        val subtitle_titles = arrayOf("ε=ε=ε=┏(゜ロ゜;)┛", "(　o=^•ェ•)o　┏━┓", "(/▽＼)", "ヽ(✿ﾟ▽ﾟ)ノ")
        val random = Random()
        title_region.text = titles[random.nextInt(titles.size)]
        subtitle_region.text = subtitle_titles[random.nextInt(titles.size)]
        //分区列表
        homeRegionList.clear()
        homeRegionList.addAll(HomeRegionInfo.create(activity))
        homeRegionAdapter.notifyDataSetChanged()
        //读取分区列表
        Observable.just(readAssetsJson())
                .compose(bindToLifecycle())
                .map { Gson().fromJson(it, RegionTypesInfo::class.java).data }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ dataBeans ->
                    regionTypes.addAll(dataBeans)
                }, { _ ->
                    toast("读取分区列表遇到错误")
                })
    }

    /**
     * 设置时间线卡片
     */
    private fun setTimeLineCard() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        if (prefs.getBoolean("home_time_line", true)) {
            layout_time_line.visibility = View.VISIBLE
            val selectorDateUtil = SelectorDateUtil(activity)
            val timeFrom = SelectorDateUtil.formatDate(selectorDateUtil.timeFrom!!, "-")
            val timeTo = SelectorDateUtil.formatDate(selectorDateUtil.timeTo!!, "-")
            tv_time_line.text = "${timeFrom}至${timeTo}"
        } else {
            layout_time_line.visibility = View.GONE
        }
    }

    /**
     * 设置更改功能卡片
     */
    private fun setMoreCard() {
        //随机显示标题
//        val titles = arrayOf("更多", "", "时光姬", "时光姬")
        val subtitles = arrayOf("ε=ε=ε=┏(゜ロ゜;)┛", "(　o=^•ェ•)o　┏━┓", "(/▽＼)", "ヽ(✿ﾟ▽ﾟ)ノ")
        val random = Random()
//        title_more.text = titles[random.nextInt(titles.size)]
        subtitle_more.text = subtitles[random.nextInt(subtitles.size)]
        //功能列表
        homeMoreList.clear()
        homeMoreList.addAll(HomeRegionInfo.create(activity))
        homeMoreAdapter.notifyDataSetChanged()
    }

    override fun initToolBar() {

    }

    override fun onRestart() {
        super.onRestart()
        searchClipboard()
        setRegionCard()
        setTimeLineCard()
    }

    /**
     * 搜索
     */
    private fun search(key: String): Info? {
        var a: String
        var ss = arrayOf("av", "ss", "live", "au", "cv", "ep")
        for (s in ss) {
            a = getAid(key, "$s(\\d+)")
            if (a != "") {
                return Info(a, s)
            }
        }
        return null
    }

    /**
     * 搜索剪切板
     */
    private fun searchClipboard() {
        val plaster = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (plaster.hasText()) {
            info = findAid(plaster.text.toString().trim())
            if (info != null) {
                find_avid.text = "点击进入 ${info!!.type}${info!!.aid}"
            }
        }
        find_card_view.visibility = if (info == null) View.GONE else View.VISIBLE
    }

    /**
     * 寻找视频id
     * 【你的名字】【煽情对白】N2V - 寻找 UP主: 卷成卷原创电音平台 http://www.bilibili.com/video/av13840208
     * NEW GAME!!, http://bangumi.bilibili.com/anime/6330/
     * 播主：Zelo-Balance http://live.bilibili.com/live/14047.html
     */
    private fun findAid(text: String): Info? {
        var a = ""
        a = getAid(text, ".*http://www.bilibili.com/video/av(\\d+).*")
        if (a != "") {
            return Info(a, "av")
        }
        a = getAid(text, ".*http://bangumi.bilibili.com/anime/(\\d+)/.*")
        if (a != "") {
            return Info(a, "ss")
        }
        a = getAid(text, ".*http://live.bilibili.com/live/(\\d+).html.*")
        if (a != "") {
            return Info(a, "live")
        }
        a = getAid(text, ".*https://m.bilibili.com/audio/au(\\d+)")
        if (a != "") {
            return Info(a, "au")
        }
        a = getAid(text, ".*http://www.bilibili.com/read/cv(\\d+)")
        if (a != "") {
            return Info(a, "cv")
        }
        a = getAid(text, ".*http://m.bilibili.com/bangumi/play/ss(\\d+)")
        if (a != "") {
            return Info(a, "ss")
        }
        a = getAid(text, ".*https://m.bilibili.com/bangumi/play/ep(\\d+).*")
        if (a != "") {
            return Info(a, "ep")
        }
        return null
    }

    /**
     * 用正则获取视频id
     */
    private fun getAid(text: String, regex: String): String {
        val compile = Pattern.compile(regex)
        val matcher = compile.matcher(text)
        if (matcher.find())
            return matcher.group(1)//提取匹配到的结果
        return ""
    }

    /**
     * 读取assets下的json数据
     */
    private fun readAssetsJson(): String? {
        val assetManager = activity.assets
        try {
            val inputStream = assetManager.open("region.json")
            val br = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var str: String? = br.readLine()
            while (str != null) {
                stringBuilder.append(str)
                str = br.readLine()
            }
            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 获取广告信息
     */
    private fun getAd() {
        MiaoHttp.newStringClient(
                url = "https://10miaomiao.cn/miao/bilimiao/ad",
                onResponse = {
                    try {
                        var info = Gson().fromJson(it, MiaoAdInfo::class.java)
                        if (info.code == 0) {
                            var dataBean = info.data
                            //是否显示视频推荐
                            if (dataBean.isShow) {
                                card_ad.visibility = View.VISIBLE
                                tv_ad.text = dataBean.title
                                btn_ad.text = dataBean.link.text
                                btn_ad.setOnClickListener {
                                    linkAd(dataBean.link.url)
                                }
                            }
                        }
                    } catch (e: Exception) {

                    }
                }
        )
    }

    /**
     * 广告跳转
     */
    private fun linkAd(url: String) {
        var info = findAid(url)
        if (info != null) {
            if (info.type == "av") {
                IntentHandlerUtil.openWithPlayer(activity, info.aid, IntentHandlerUtil.TYPE_VIDEO)
                return
            }
            if (info.type == "ss") {
                IntentHandlerUtil.openWithPlayer(activity, info.aid, IntentHandlerUtil.TYPE_BANGUMI)
                return
            }
        }
        //普通链接 调用浏览器
        var intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    /**
     * 菜单项单击
     */
    fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> SettingActivity.launch(activity)
            R.id.theme -> ThemePickerActivity.launch(activity)
        }
        return true
    }


    data class Info(
            var aid: String,
            var type: String
    )
}
