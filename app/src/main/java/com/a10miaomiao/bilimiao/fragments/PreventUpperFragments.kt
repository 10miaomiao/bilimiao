package com.a10miaomiao.bilimiao.fragments

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.db.PreventUpperDB
import com.a10miaomiao.bilimiao.views.DividerItemDecoration
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_prevent_upper.*

/**
 * Created by 10喵喵 on 2017/11/18.
 */
class PreventUpperFragments : BaseFragment() {
    override var layoutResId = R.layout.fragment_prevent_upper

    val keywordDB: PreventUpperDB by lazy {
        PreventUpperDB(activity, PreventUpperDB.DB_NAME, null, 1)
    }

    lateinit var keywords: ArrayList<PreventUpperDB.Upper>

    val mAdapter by lazy {
        PreventUpperAdapter(keywords)
    }

    override fun finishCreateView(savedInstanceState: Bundle?) {
        keywords = keywordDB.queryAllHistory()
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        recycle.addItemDecoration(DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL_LIST)) //分割线
        mAdapter.setOnItemClickListener { adapter, view, position ->
            AlertDialog.Builder(activity)
                    .setTitle("确定删除up主：\"${keywords[position].name}\"")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", { dialogInterface, i ->
                        keywordDB.deleteHistory(keywords[position].uid)
                        keywords.removeAt(position)
                        mAdapter.notifyDataSetChanged()
                    })
                    .show()
        }
    }
//
//    override fun initToolBar() {
//        toolbar.title = "编辑屏蔽关键字"
//        toolbar.setNavigationIcon(R.mipmap.ic_back)
//        toolbar.setNavigationOnClickListener {
//            finish()
//        }
//        toolbar.inflateMenu(R.menu.add)
//        toolbar.setOnMenuItemClickListener {
//            var ed = EditDialog()
//            ed.arguments = Bundle().apply {
//                putString("hint", "请输入关键字")
//            }
//            ed.show(supportFragmentManager,"EditPreventKeywordActivity->EditDialog")
//            ed.onFinishInput = this::onFinishInput
//            true
//        }
//    }


    class PreventUpperAdapter(list: List<PreventUpperDB.Upper>) : BaseQuickAdapter<PreventUpperDB.Upper, BaseViewHolder>(R.layout.item_prevent_keyword,list) {
        override fun convert(helper: BaseViewHolder?, item: PreventUpperDB.Upper?) {
            helper?.setText(R.id.tv_keyword, item!!.name)
        }
    }
}