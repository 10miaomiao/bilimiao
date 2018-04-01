package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.include_title_bar.*
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by 10喵喵 on 2017/10/21.
 */
class AboutActivity : BaseActivity() {

    override var layoutResID: Int = R.layout.activity_about

    override fun initViews(savedInstanceState: Bundle?) {
        showVersion()
        loadData()
    }

    //显示应用版本号
    private fun showVersion(){
        val version = this.packageManager.getPackageInfo(this.packageName,0).versionName
        tv_version_subtitle.text = "版本：" + version

        var web_quote = """
 Bilibili 工具箱(<a href="http://biliquery.typcn.com/">http://biliquery.typcn.com/</a>)
            """
        tv_quote.text = Html.fromHtml(web_quote)
        tv_quote.movementMethod = LinkMovementMethod.getInstance()

        var web_addr = """
<a href="https://github.com/10miaomiao/bilimiao">https://github.com/10miaomiao/bilimiao</a>
            """
        tv_addr.text = Html.fromHtml(web_addr)
        tv_addr.movementMethod = LinkMovementMethod.getInstance()

        var web_me = """
<a href="https://10miaomiao.cn/">https://10miaomiao.cn/</a>
            """
        tv_me.text = Html.fromHtml(web_me)
        tv_me.movementMethod = LinkMovementMethod.getInstance()
    }

    fun loadData(){
        MiaoHttp.newStringClient(
                url = "https://10miaomiao.cn/miao/donate",
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    try {
                        val list = (jsonParser.nextValue() as JSONObject).getJSONArray("data")
                        var strDonate = StringBuffer()
                        for (i in 0 until list.length()){
                            strDonate.append((if (i == 0) "" else "，") + list.getString(i) )
                        }
                        tv_donate.text = strDonate
                    } catch (e: Exception) {
                        tv_donate.text = "连接到异世界出现错误＞﹏＜"
                    }
                },
                onError = {
                    tv_donate.text = "无法连接到异世界＞﹏＜"
                }
        )
    }

    override fun initToolBar() {
        toolbar.title = "关于"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    companion object{
        fun launch(activity: Activity){
            val mIntent = Intent(activity,AboutActivity::class.java)
            activity.startActivity(mIntent)
        }
    }
}