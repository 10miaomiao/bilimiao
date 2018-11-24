package com.a10miaomiao.bilimiao.fragments

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.EditPreventKeywordAdapter
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.db.KeyWordDB
import com.a10miaomiao.bilimiao.dialog.EditDialog
import com.a10miaomiao.bilimiao.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_edit_prevent_keyword.*

/**
 * Created by 10喵喵 on 2017/11/18.
 */
class EditPreventKeywordFragments : BaseFragment() {
    override var layoutResId = R.layout.fragment_edit_prevent_keyword

    val keywordDB: KeyWordDB by lazy {
        KeyWordDB(activity!!, KeyWordDB.DB_NAME, null, 1)
    }
    lateinit var keywords: ArrayList<String>
    val mAdapter by lazy {
        EditPreventKeywordAdapter(keywords)
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
            AlertDialog.Builder(activity!!)
                    .setTitle("确定删除关键字：\"${keywords[position]}\"")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", { dialogInterface, i ->
                        keywordDB.deleteHistory(keywords[position])
                        keywords.removeAt(position)
                        mAdapter.notifyDataSetChanged()
                    })
                    .show()
        }
        fab_add.setOnClickListener {
            var ed = EditDialog()
            ed.arguments = Bundle().apply {
                putString("hint", "请输入关键字")
            }
            ed.show(activity!!.supportFragmentManager,"EditPreventKeywordActivity->EditDialog")
            ed.onFinishInput = this::onFinishInput
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

    fun onFinishInput(text: String) {
        if (text.isEmpty())
            return
        keywordDB.insertHistory(text)
        keywords.add(text)
        mAdapter.notifyDataSetChanged()
    }

}