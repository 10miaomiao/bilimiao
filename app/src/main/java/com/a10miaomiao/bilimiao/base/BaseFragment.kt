package com.a10miaomiao.bilimiao.base;

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.a10miaomiao.bilimiao.utils.log


/**
 * Created by 10喵喵 on 2017/7/31.
 */

abstract class BaseFragment : LazyLoadFragment(){
    private var rootView: View? = null
    protected var mActivity: Activity? = null
    protected abstract var layoutResId : Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (rootView == null) {
            rootView = inflater!!.inflate(layoutResId, container, false)
            initView(rootView)
            initData(savedInstanceState)
            //initListener()
        }else{
            val parent = rootView?.parent as ViewGroup
            parent?.removeView(rootView)
        }
        return rootView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        finishCreateView(savedInstanceState)
    }

    abstract fun finishCreateView(savedInstanceState: Bundle?)

    /**StateView的根布局，默认是整个界面，如果需要变换可以重写此方法 */
    fun getStateViewRoot(): View? {
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    open fun initView(view : View?){

    }
    /**
     * 初始化数据
     */
    open fun initData(savedInstanceState: Bundle?) {

    }

    override fun onFragmentFirstVisible() {
        //当第一次可见的时候，加载数据
        loadData()
    }

    //加载数据
    open fun loadData(){

    }

    override fun onDestroy() {
        super.onDestroy()

        rootView = null
    }

}

