package com.a10miaomiao.bilimiao.views

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.RankOrdersAdapter
import com.a10miaomiao.bilimiao.utils.log

class QualityPopupWindow(var activity: Activity, var anchor: View)
    : PopupWindow(LayoutInflater.from(activity).inflate(R.layout.layout_quality, null)
        , LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) {

    val list = ArrayList<String>()
    val value_list = ArrayList<String>()
    val mAdapter = RankOrdersAdapter(list)
    var onCheckItemPositionChanged: ((value: String, position: Int) -> Unit)? = null
    val recycle by lazy {
        contentView.findViewById(R.id.recycle) as RecyclerView
    }

    init {
        this.apply {
            setBackgroundDrawable(ColorDrawable(Color.argb(200,0,0,0)))
            animationStyle = R.style.popwindow_anim_left
            isOutsideTouchable = true
            isFocusable = true
            width = dip(100f)
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (position != mAdapter.checkItemPosition) {
                mAdapter.checkItemPosition = position
                onCheckItemPositionChanged?.invoke(value_list[position], position)
                dismiss()
            }
        }
        //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
//        contentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
//        val popupWidth = contentView.measuredWidth    //  获取测量后的宽度
//        val popupHeight = contentView.measuredHeight  //获取测量后的高度
//
//        // 获得位置 这里的anchor是目标控件，就是你要放在这个anchor的上面还是下面
//        val location = IntArray(2)
//        anchor.getLocationOnScreen(location)
//        //这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以

        //this.showAtLocation(anchor,Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
//        this.showAsDropDown(anchor, 0, 0)
//        //获取popupwindow高度确定动画开始位置
//        var contentHeight = ViewUtils.getViewMeasuredHeight(contentView)
//        //var contentHeight = 200
//        var fromYDelta = -contentHeight - 50
//        this.contentView.startAnimation(AnimationUtil.createInAnimation(activity, fromYDelta))
    }

    fun show() {
        this.showAtLocation(anchor, Gravity.RIGHT,0 ,0)
    }

    fun setData(data: List<String>){
        list.clear()
        list.addAll(data)
        mAdapter.notifyDataSetChanged()
    }

    fun setValueList(value: List<String>){
        value_list.clear()
        value_list.addAll(value)
    }
    fun setValue(value: String){
        value_list.forEachIndexed { index, s ->
            if (value == s) {
                mAdapter.checkItemPosition = index
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun dip(value: Float): Int {
        val dm = activity.resources.displayMetrics
        return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5).toInt()
    }


}