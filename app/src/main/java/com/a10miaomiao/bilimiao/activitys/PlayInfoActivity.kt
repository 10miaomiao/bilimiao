package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.ClipboardManager
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.db.DownloadDB
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.a10miaomiao.bilimiao.netword.ApiHelper
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.service.DownloadService
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.log
import kotlinx.android.synthetic.main.activity_play_info.*
import kotlinx.android.synthetic.main.include_title_bar.*
import java.util.regex.Pattern


/**
 * Created by 10喵喵 on 2017/11/12.
 */
class PlayInfoActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_play_info
    val cid by lazy {
        intent.extras.getString(ConstantUtil.CID)
    }
    val name by lazy {
        intent.extras.getString(ConstantUtil.NAME)
    }
    val aid by lazy {
        intent.extras.getString(ConstantUtil.AID)
    }
    val db: DownloadDB by lazy {
        DownloadDB(activity, DownloadDB.DB_NAME, null, 1)
    }
    val headers by lazy {
        mapOf<String, String>(
                "Referer" to "https://www.bilibili.com/video/av$aid",
                "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36"
        )
    }

    var info: DownloadInfo? = null

    override fun initViews(savedInstanceState: Bundle?) {
        loadData()
        btn_copy.setOnClickListener {
            val plaster = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            plaster.text = et_video_url.text
            toast("已复制到剪切板(/▽＼)")
        }
        btn_download.setOnClickListener {
            if (info != null)
                DownloadService.add(activity, info!!)
            else
                toast("数据未加载完成")
        }

    }

    override fun initToolBar() {
        toolbar.title = "播放地址"
        toolbar.setNavigationIcon(R.mipmap.ic_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadData() {
        //var query = "appkey=f3bb208b3d081dc8&cid=$cid&from=miniplay&otype=json&player=1&quality=1&type=mp4"
//        var query = ""//清晰度 32 64 80 112
//        var sing = ApiHelper.getMD5(query + "1c15888dc316e05a15fdd0a02ed6584f")
//        var url = "https://interface.bilibili.com/playurl?$query&sign=$sing"

//        var url = "http://interface.bilibili.com/playurl?_device=uwp&cid=" + cid + "&otype=xml&quality=" + 1 + "&appkey=" + ApiHelper.appKey_Android + "&access_key=&type=mp4&mid=" + "" + "&_buvid=B3CC4714-C1D3-4010-918B-8E5253E123C16133infoc&_hwid=03008c8c0300d6d1&platform=uwp_desktop" + "&ts=" + ApiHelper.GetTimeSpen()
//        url += "&sign=" + ApiHelper.GetSign_Android(url)

        var url = "https://interface.bilibili.com/playurl?cid=$cid&player=1&quality=64&qn=64&ts=${ApiHelper.getTimeSpen()}"
        url += "&sign=" + ApiHelper.getSing(url, "1c15888dc316e05a15fdd0a02ed6584f")

        log(url)
        MiaoHttp.newStringClient(
                url = url,
                headers = headers,
                onResponse = {
                    log(it)
//                    val jsonParser = JSONTokener(it)
//                    try {
//                        val jsonObject = (jsonParser.nextValue() as JSONObject).getJSONArray("durl").getJSONObject(0)
//                        et_video_url.setText(jsonObject.getString("url"))
//                        down(jsonObject.getString("url"))
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                        et_video_url.isEnabled = false
//                        et_video_url.setText("解析失败 -1")
//                    } catch (e: ClassCastException) {
//                        e.printStackTrace()
//                        et_video_url.isEnabled = false
//                        et_video_url.setText("解析失败 -2")
//                    }
                    val pattern = """<length>(.*?)</length>.*?<size>(.*?)</size>.*?<url>.*?<!\[CDATA\[(.*?)]]></url>"""
                    //用正则式匹配文本获取匹配器
                    val matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE or Pattern.DOTALL).matcher(it)
                    if (matcher.find()) {
                        info = DownloadInfo(
                                cid = cid,
                                aid = aid,
                                name = name,
                                type = "video",
                                length = matcher.group(1).toLong(),
                                size = matcher.group(2).toLong(),
                                pic = ""
                        )
                        et_video_url.setText(info!!.url)
                    } else {
                        et_video_url.isEnabled = false
                        et_video_url.setText("解析失败 -2")
                    }
                },
                onError = {
                    it.printStackTrace()
                    et_video_url.isEnabled = false
                    et_video_url.setText("无法连接到御坂网络")
                }
        )
    }

    companion object {
        fun launch(activity: Activity, cid: String, aid: String,name: String) {
            val mIntent = Intent(activity, PlayInfoActivity::class.java)
            mIntent.putExtra(ConstantUtil.CID, cid)
            mIntent.putExtra(ConstantUtil.AID, aid)
            mIntent.putExtra(ConstantUtil.NAME, name)
            activity.startActivity(mIntent)
        }
    }
}