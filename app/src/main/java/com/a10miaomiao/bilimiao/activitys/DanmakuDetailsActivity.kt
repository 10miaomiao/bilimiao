package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import kotlinx.android.synthetic.main.activity_danmaku_details.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by 10喵喵 on 2018/1/26.
 */
class DanmakuDetailsActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_danmaku_details

    private val hash by lazy {
        intent.extras.getString("hash")
    }

    private var uid: String? = null

    override fun initViews(savedInstanceState: Bundle?) {
        tv_text.text = intent.extras.getString(ConstantUtil.DATA)

        MiaoHttp.newStringClient(
                url = "http://biliquery.typcn.com/api/user/hash/$hash",
                onResponse = {
                    progress?.visibility = View.GONE
                    val jsonParser = JSONTokener(it)
                    try {
                        var jsonObject = jsonParser.nextValue() as JSONObject
                        if (jsonObject.getInt("error") == 0) {
                            var data = jsonObject.getJSONArray("data")
                            uid = data.getJSONObject(0).getString("id")
                            tv_user_id?.text = "发送者ID:$uid"
                        } else {
                            tv_user_id?.text = "查询遇到错误"
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        tv_user_id?.text = "查询失败"
                        btn_view?.text = "确定"
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                        tv_user_id?.text = "网络好像有问题＞﹏＜"
                        btn_view?.text = "确定"
                    }
                },
                onError = {
                    progress?.visibility = View.GONE
                    it.printStackTrace()
                    tv_user_id?.text = "网络好像有问题＞﹏＜"
                    btn_view?.text = "确定"
                }
        )

        btn_view.setOnClickListener {
            if (uid != null) {
                IntentHandlerUtil.openWithPlayer(activity, IntentHandlerUtil.TYPE_AUTHOR, uid.toString())
            }
        }
    }

    override fun initToolBar() {

    }

    companion object {
        fun launch(activity: Activity, text: String, hash: String) {
            val mIntent = Intent(activity, DanmakuDetailsActivity::class.java)
            mIntent.putExtra(ConstantUtil.DATA, text)
            mIntent.putExtra("hash", hash)
            activity.startActivity(mIntent)
        }
    }

}