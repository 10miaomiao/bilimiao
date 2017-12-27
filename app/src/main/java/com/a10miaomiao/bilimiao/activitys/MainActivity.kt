package com.a10miaomiao.bilimiao.activitys

import android.content.Context
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
import com.a10miaomiao.bilimiao.entity.RegionTypesInfo
import com.a10miaomiao.bilimiao.utils.SelectorDateUtil
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


/**
 * Created by 10喵喵 on 2017/9/16.
 */
class MainActivity : BaseActivity() {
    var regionTypes = ArrayList<RegionTypesInfo.DataBean>()
    var info: Info? = null
    var searchFragment = SearchBoxFragment.newInstance()

    override var layoutResID = R.layout.activity_main

    override fun initViews(savedInstanceState: Bundle?) {
        //searchClipboard()
        val mAdapter = HomeRegionItemAdapter()
        recycle_region.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 5)
            adapter = mAdapter
        }
        mAdapter.setOnItemClickListener { adapter, view, position ->
            RegionTypeDetailsActivity.launch(activity, regionTypes[position])
        }
        searchFragment.onSearchClick = {
            var d = search(it)
            if (d == null) {
                SearchActivity.launch(activity, it)
                true
            } else {
                InfoActivity.launch(activity, d.aid, d.type)
                true
            }
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
            popupMenu.setOnMenuItemClickListener (this::onMenuItemClick)
            popupMenu.show()
        }
        btn_time_line.setOnClickListener {
            SelectorDateActivity.launch(activity)
        }
        btn_rank.setOnClickListener {
            RankActivity.launch(activity)
        }
        btn_edit_keyword.setOnClickListener {
            EditPreventKeywordActivity.launch(activity)
        }
        searchClipboard()
        setRegionCard()
        setTimeLineCard()
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
        //读取分区列表
        Observable.just(readAssetsJson())
                .compose(bindToLifecycle())
                .map({ Gson().fromJson(it, RegionTypesInfo::class.java) })
                .map({ it.data })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ dataBeans ->
                    regionTypes.addAll(dataBeans)
                }, { throwable ->
                    toast("读取分区列表遇到错误")
                })

    }

    /**
     * 设置时间线卡片
     */
    private fun setTimeLineCard() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        if (prefs.getBoolean("home_time_line", true)) {
            card_time_line.visibility = View.VISIBLE
            val selectorDateUtil = SelectorDateUtil(activity)
            val timeFrom = SelectorDateUtil.formatDate(selectorDateUtil.timeFrom!!, "/")
            val timeTo = SelectorDateUtil.formatDate(selectorDateUtil.timeTo!!, "/")
            tv_time_line.text = "${timeFrom}至${timeTo}"
        } else {
            card_time_line.visibility = View.GONE
        }
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
        var a = ""
        var ss = arrayOf("av", "anime", "live", "au", "cv")
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
            findAid(plaster.text.toString().trim())
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
    private fun findAid(text: String) {
        var a = ""
        a = getAid(text, ".*http://www.bilibili.com/video/av(\\d+).*")
        if (a != "") {
            info = Info(a, "av")
            return
        }
        a = getAid(text, ".*http://bangumi.bilibili.com/anime/(\\d+)/.*")
        if (a != "") {
            info = Info(a, "anime")
            return
        }
        a = getAid(text, ".*http://live.bilibili.com/live/(\\d+).html.*")
        if (a != "") {
            info = Info(a, "live")
            return
        }
        a = getAid(text, ".*https://m.bilibili.com/audio/au(\\d+)")
        if (a != "") {
            info = Info(a, "au")
            return
        }
        a = getAid(text, ".*http://www.bilibili.com/read/cv(\\d+)")
        if (a != "") {
            info = Info(a, "cv")
            return
        }
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
     * 菜单项单击
     */
    fun onMenuItemClick(item: MenuItem): Boolean{
        when(item.itemId){
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
