package com.a10miaomiao.bilimiao.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.a10miaomiao.bilimiao.R
import kotlinx.android.synthetic.main.layout_select_view.view.*

/**
 * 左右选择控件
 * Created by 10喵喵 on 2017/10/22.
 */
class SelectView : FrameLayout {
    var titles : Array<String>? = null
        set(value) {
            field = value
            tv_title.text = value!![0]
        }
    var index = 0
        set(value) {
            field = value
            tv_title.text = titles!![index]
            onChangeItem?.invoke(index)
        }
    var onChangeItem : ((index: Int) -> Unit)? = null

    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }
    private fun initView(){
        View.inflate(context, R.layout.layout_select_view, this)
        iv_to_left.setOnClickListener {
            index = if(index == 0) titles!!.size - 1 else index - 1
            tv_title.text = titles!![index]
            onChangeItem?.invoke(index)
        }
        iv_to_right.setOnClickListener {
            index = if(index == titles!!.size - 1) 0 else index + 1
            tv_title.text = titles!![index]
            onChangeItem?.invoke(index)
        }
    }

}