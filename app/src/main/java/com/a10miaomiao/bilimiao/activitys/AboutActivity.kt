package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.include_title_bar.*

/**
 * Created by 10喵喵 on 2017/10/21.
 */
class AboutActivity : BaseActivity() {

    override var layoutResID: Int = R.layout.activity_about

    override fun initViews(savedInstanceState: Bundle?) {
        showVersion()
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
    }
    override fun initToolBar() {
        toolbar.title = "关于"
        toolbar.setNavigationIcon(R.mipmap.ic_back)
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