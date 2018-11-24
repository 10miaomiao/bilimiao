package com.a10miaomiao.bilimiao.adapter.helper

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by 10喵喵 on 2017/10/4.
 */
abstract class RecyclerOnScrollListener(var mLinearLayoutManager: LinearLayoutManager,var span: Int)
    : RecyclerView.OnScrollListener() {

    abstract fun onLoadMore()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy == 0) {
            return
        }

        val visibleItemCount = recyclerView!!.getChildCount()
        val totalItemCount = mLinearLayoutManager.getItemCount()
        val lastCompletelyVisiableItemPosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition()

        if ( visibleItemCount > 0 && lastCompletelyVisiableItemPosition >= totalItemCount - 1 - span) {
            onLoadMore()
        }
    }
}