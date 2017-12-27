package com.a10miaomiao.bilimiao.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.utils.KeyboardUtil
import kotlinx.android.synthetic.main.dialog_edit_name.*


/**
 * Created by 10喵喵 on 2017/10/23.
 */
class EditDialog : DialogFragment() {


    var onFinishInput : ((text : String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogStyle2)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.dialog_edit_name, container,false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        btn_ok.setOnClickListener {
            KeyboardUtil.closeKeyboard(activity, et_content)
            dismiss()
            onFinishInput?.invoke(et_content.text.toString())
        }
        dialog.setOnDismissListener {
            KeyboardUtil.closeKeyboard(activity, et_content)
        }
        view_search_outside.setOnClickListener {
            KeyboardUtil.closeKeyboard(activity, et_content)
            dismiss()
        }
        if(arguments.containsKey("hint")){
            et_content.hint = arguments.getString("hint")
        }
        //et_content.setText(name)
//        var ea: Editable = et_content.text
//        et_content.setSelection(0, ea.length)
    }

    override fun onStart() {
        super.onStart()
        initDialog()
    }

    private fun initDialog() {
        val window = dialog.window
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels //DialogSearch的宽
        window.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT)
        window.setGravity(Gravity.BOTTOM)
        KeyboardUtil.openKeyboard(activity, et_content)
    }
}