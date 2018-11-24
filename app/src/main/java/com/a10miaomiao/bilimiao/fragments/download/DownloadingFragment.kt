package com.a10miaomiao.bilimiao.fragments.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.DownloadAdapter
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.db.DownloadDB
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.a10miaomiao.bilimiao.service.DownloadService
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import kotlinx.android.synthetic.main.fragment_download.*
import java.io.File

/**
 * Created by 10喵喵 on 2018/1/21.
 */
class DownloadingFragment : BaseFragment() {
    override var layoutResId = R.layout.fragment_download
    val db: DownloadDB by lazy {
        DownloadDB(activity!!, DownloadDB.DB_NAME, null, 1)
    }

    var mAdapter: DownloadAdapter? = null
    var list = ArrayList<DownloadInfo>()

    override fun finishCreateView(savedInstanceState: Bundle?) {
        mAdapter = DownloadAdapter(list)
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        mAdapter!!.setOnItemClickListener { adapter, view, position ->
            if (list[position].status == DownloadInfo.DOWNLOADING) {
                DownloadService.pause(activity!!)
            } else {
                for (item in list) {
                    if (item.status == DownloadInfo.DOWNLOADING) {
                        item.status = DownloadInfo.WAIT
                        break
                    }
                }
                DownloadService.start(activity!!, list[position].cid)
            }
        }
        mAdapter!!.setOnItemLongClickListener { adapter, view, position ->
            val info = list[position]
            AlertDialog.Builder(activity!!)
                    .setItems(arrayOf("取消下载", "重新下载", "取消全部")) { dialogInterface, i ->
                        when (i) {
                            // 取消下载
                            0 -> {
                                DownloadService.del(activity!!, info.cid)
                                delDownloadFlie(info)
                            }
                            // 重新下载
                            1 -> {
                                DownloadService.pause(activity!!)
                                delDownloadFlie(info)
                                DownloadService.start(activity!!, info.cid)
                            }
                            // 取消全部
                            2 -> {
                                val _list = ArrayList<DownloadInfo>()
                                _list.addAll(list)
                                DownloadService.delAll(activity!!)
                                _list.forEach { delDownloadFlie(it) }
                            }
                        }
                    }
                    .show()
            true
        }

        //注册广播接收器
        val filter = IntentFilter()
        filter.addAction(DownloadService.ACTION_UPDATE)
        filter.addAction(DownloadService.ACTION_REVIEW)
        mActivity!!.registerReceiver(mReceiver, filter)
    }

    override fun loadData() {
        list.addAll(db.queryArge("status!=?", arrayOf(DownloadInfo.FINISH.toString())))
        mAdapter?.notifyDataSetChanged()
        img_null?.visibility = if (list.size == 0) View.VISIBLE else View.GONE
    }

    /**
     * 删除下载文件
     */
    private fun delDownloadFlie(info: DownloadInfo) {
        var path = Environment.getExternalStorageDirectory().path + "/BiliMiao/b站视频/"
        var f1 = File(path + info.fileName + ".temp")
        var f2 = File(path + info.name + "/")
        if (f1.exists()) {
            f1.delete()
        }
        if (f2.exists()) {
            for (f in f2.listFiles()) {
                f.delete()
            }
            f2.delete()
        }
    }

    /**
     * 更新UI的广播接收器
     */
    var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (DownloadService.ACTION_UPDATE == intent?.action) {
                var info = intent.extras.getParcelable<DownloadInfo>(ConstantUtil.DATA)
                for (item in list) {
                    if (item.cid == info.cid) {
                        item.status = DownloadInfo.DOWNLOADING
                        item.progress = info.progress
                        item.size = info.size
                        item.making = info.making
                        item._count = info._count
                        mAdapter?.notifyDataSetChanged()
                        break
                    }
                }
            } else if (DownloadService.ACTION_REVIEW == intent?.action) {
                list.clear()
                loadData()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mReceiver != null) {
            mActivity!!.unregisterReceiver(mReceiver)
        }
    }

}