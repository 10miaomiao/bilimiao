package com.a10miaomiao.bilimiao.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.utils.IntentHandlerUtil
import kotlinx.android.synthetic.main.dialog_user.*

/**
 * Created by 10喵喵 on 2017/11/4.
 */
class QueryUserDialog : DialogFragment() {
    private val hash by lazy {
        arguments!!.getString("hash")
    }
    private var uid: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.dialog_user, container)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadData()
        btn_view.setOnClickListener {
            if (uid != null) {
                IntentHandlerUtil.openWithPlayer(activity!!, IntentHandlerUtil.TYPE_AUTHOR, uid.toString())
            }
            dismiss()
        }

    }

    private fun loadData() {

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