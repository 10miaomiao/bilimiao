package com.a10miaomiao.bilimiao.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.a10miaomiao.bilimiao.R
import kotlinx.android.synthetic.main.layout_load_more.view.*


/**
 * Created by 10喵喵 on 2017/9/29.
 */
class LoadMoreView : FrameLayout {
    companion object{
        val LOADING = 0 //加载更多
        val NOMORE = 1  //没有更多
        val FAIL = 2  //加载失败
    }

    var state : Int? = null
        set(value) {
            field = value
            when(value){
                LOADING ->{
                    text?.text = "加载中"
                    progress?.visibility = View.VISIBLE
                }
                NOMORE ->{
                    visibility = View.VISIBLE
                    text?.text = "下面没有了"
                    progress?.visibility = View.GONE
                }
                FAIL ->{
                    visibility = View.VISIBLE
                    text?.text = "无法连接到御坂网络"
                    progress?.visibility = View.GONE
                }
            }
        }

    var isLoading: Boolean = true
        private set
        get() = visibility == View.VISIBLE || state == NOMORE

    constructor(context: Context) : super(context){
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }
    fun initView(){
        View.inflate(context, R.layout.layout_load_more, this)
        visibility = View.GONE
    }
}
