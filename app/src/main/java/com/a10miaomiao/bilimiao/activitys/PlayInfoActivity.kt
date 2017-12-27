package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.ClipboardManager
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.netword.ApiHelper
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.log
import kotlinx.android.synthetic.main.activity_play_info.*
import kotlinx.android.synthetic.main.include_title_bar.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by 10喵喵 on 2017/11/12.
 */
class PlayInfoActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_play_info
    val cid by lazy {
        intent.extras.getString(ConstantUtil.CID)
    }
    override fun initViews(savedInstanceState: Bundle?) {
        loadData()
        btn_copy.setOnClickListener {
            val plaster = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            plaster.text = et_video_url.text
            toast("已复制到剪切板(/▽＼)")
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
        var query = "appkey=f3bb208b3d081dc8&cid=$cid&from=miniplay&otype=json&player=1&quality=1&type=mp4"
        var sing = ApiHelper.getMD5(query + "1c15888dc316e05a15fdd0a02ed6584f")
        var url = "http://interface.bilibili.com/playurl?$query&sign=$sing"

//        var url = "http://api.bilibili.com/playurl?aid=16224852&page=1&callback=jQuery1720044678678032856745_1510649728188&platform=html5&quality=1&vtype=mp4&type=jsonp&token=e670ef1b4a5c9d9e62a14faf219a3967&_=1510649730503"

//        var url = "http://interface.bilibili.com/playurl?_device=uwp&cid=" + cid + "&otype=xml&quality=" + 1 + "&appkey=" + ApiHelper.appKey_Android + "&access_key=&type=mp4&mid=" + "" + "&_buvid=B3CC4714-C1D3-4010-918B-8E5253E123C16133infoc&_hwid=03008c8c0300d6d1&platform=uwp_desktop" + "&ts=" + ApiHelper.GetTimeSpen()
//        url += "&sign=" + ApiHelper.GetSign_Android(url)
        log(url)
        MiaoHttp.newStringClient(
                url = url,
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    try {
                        val jsonObject = (jsonParser.nextValue() as JSONObject).getJSONArray("durl").getJSONObject(0)
                        et_video_url.setText(jsonObject.getString("url"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        et_video_url.isEnabled = false
                        et_video_url.setText("解析失败 -1")
                    } catch (e: ClassCastException){
                        e.printStackTrace()
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
        fun launch(activity: Activity, cid: String) {
            val mIntent = Intent(activity, PlayInfoActivity::class.java)
            mIntent.putExtra(ConstantUtil.CID, cid)
            activity.startActivity(mIntent)
        }
    }
}