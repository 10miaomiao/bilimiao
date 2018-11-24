package com.a10miaomiao.bilimiao.activitys

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.log
import kotlinx.android.synthetic.main.activity_h5_player.*
import org.json.JSONObject
import org.json.JSONTokener

class H5PlayerActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_h5_player
    private val id by lazy {
        intent.getStringExtra(ConstantUtil.ID)
    }
    private val line by lazy {
        intent.getStringExtra("line")
    }


    override fun initViews(savedInstanceState: Bundle?) {
        val webSettings = mWebView.settings
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true); // 关键点
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容
        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }
        mWebView.webChromeClient = object : WebChromeClient(){
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)
            }
        }
        getPlayUrl()
    }

    override fun initToolBar() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setBackgroundDrawableResource(android.R.color.black)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
//        window.decorView.systemUiVisibility = uiOptions
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
    }

    private fun getPlayUrl() {
        MiaoHttp.newStringClient(
                url = "http://usr.005.tv/Appapi/api_getarchives",
                method = MiaoHttp.POST,
                body = mapOf(
                        "id" to id
                ),
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    val jsonObject = jsonParser.nextValue() as JSONObject
                    val detail = jsonObject.getJSONObject("detail")
                    val body = detail.getString("body")
                    log(line + body)
                    mWebView.loadUrl(line + body)
                }
        )
    }


    override fun onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            mWebView.clearHistory()
            (mWebView.parent as ViewGroup).removeView(mWebView)
            mWebView.destroy()
        }
        super.onDestroy()
    }

    companion object {
        fun launch(activity: Activity, id: String, line: String) {
            val mIntent = Intent(activity, H5PlayerActivity::class.java)
            mIntent.putExtra(ConstantUtil.ID, id)
            mIntent.putExtra("line", line)
            activity.startActivity(mIntent)
        }
    }
}