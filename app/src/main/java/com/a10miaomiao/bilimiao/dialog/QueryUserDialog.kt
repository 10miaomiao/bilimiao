package com.a10miaomiao.bilimiao.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import com.a10miaomiao.bilimiao.utils.log
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.dialog_user.*
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.lang.Exception

/**
 * Created by 10喵喵 on 2017/11/4.
 */
class QueryUserDialog : DialogFragment() {
    private val hash by lazy {
        arguments.getString("hash")
    }
    private var uid: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.dialog_user, container)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        loadData()
        btn_view.setOnClickListener {
            if (uid != null) {
                IntentHandlerUtil.openWithPlayer(activity,"https://space.bilibili.com/$uid/")
            }
            dismiss()
        }

    }

    private fun loadData() {
        var url = "http://biliquery.typcn.com/api/user/hash/$hash"
        log(url)
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(object : StringCallback() {
                    override fun onResponse(response: String, id: Int) {
                        progress?.visibility = View.GONE
                        val jsonParser = JSONTokener(response)
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
                    }

                    override fun onError(call: Call?, e: Exception, id: Int) {
                        progress?.visibility = View.GONE
                        e.printStackTrace()
                        tv_user_id?.text = "网络好像有问题＞﹏＜"
                        btn_view?.text = "确定"
                    }
                })

    }

    override fun onStart() {
        super.onStart()
        initDialog()
    }

    private fun initDialog() {
        val window = dialog.window
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }
}