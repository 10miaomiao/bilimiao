package com.a10miaomiao.bilimiao.fragments.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.a10miaomiao.bilimiao.BuildConfig
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.DownloadAdapter
import com.a10miaomiao.bilimiao.base.BaseFragment
import com.a10miaomiao.bilimiao.db.DownloadDB
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.a10miaomiao.bilimiao.service.DownloadService
import com.a10miaomiao.bilimiao.utils.log
import kotlinx.android.synthetic.main.fragment_download.*
import java.io.File


/**
 * Created by 10喵喵 on 2018/1/21.
 */
class DownloadedFragment : BaseFragment() {
    override var layoutResId = R.layout.fragment_download
    val db: DownloadDB by lazy {
        DownloadDB(activity, DownloadDB.DB_NAME, null, 1)
    }
    var mAdapter: DownloadAdapter? = null
    var list = ArrayList<DownloadInfo>()
    var path = Environment.getExternalStorageDirectory().path + "/BiliMiao/b站视频/"

    override fun finishCreateView(savedInstanceState: Bundle?) {
        mAdapter = DownloadAdapter(list)
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        mAdapter!!.setOnItemClickListener { adapter, view, position ->
            var item = list[position]
            var strFile = path + item.fileName
            var jsonFile = path + item.name + ".json"
            if (fileIsExists(strFile)) {
                val intent = Intent(Intent.ACTION_VIEW)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", File(strFile))
                    intent.setDataAndType(contentUri, "video/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    intent.setDataAndType(Uri.fromFile(File(strFile)), "video/*")
                }
                startActivity(intent)
            } else if (item._count > 1) {
                log(item._count)
                val items = arrayOfNulls<String>(item._count)
                for (i in 0 until item._count) {
                    items[i] = "视频片段${i + 1}"
                }
                AlertDialog.Builder(activity)
                        .setTitle("共${item._count}个视频片段")
                        .setItems(items, { dialogInterface, i ->
                            strFile = path + item.name + "/" + i + item.videoType
                            if (fileIsExists(strFile)) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", File(strFile))
                                    intent.setDataAndType(contentUri, "video/*")
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                } else {
                                    intent.setDataAndType(Uri.fromFile(File(strFile)), "video/*")
                                }
                                startActivity(intent)
                            } else {
                                AlertDialog.Builder(activity)
                                        .setTitle("文件被吃了ˋ( ° ▽、° ) ")
                                        .setNegativeButton("确定", null)
                                        .show()
                            }
                        })
                        .show()

            } else {
                AlertDialog.Builder(activity)
                        .setTitle("文件被吃了ˋ( ° ▽、° ) ")
                        .setNegativeButton("确定", null)
                        .show()
            }
        }
        mAdapter!!.setOnItemLongClickListener { adapter, view, position ->
            AlertDialog.Builder(activity)
                    .setItems(arrayOf("删除视频"), { dialogInterface, i ->
                        var item = list[i]
                        var f1 = File(path + list[i].fileName)
                        var f2 = File(path + list[i].name + "/")
                        if (f1.exists()) {
                            f1.delete()
                        }
                        if(f2.exists()) {
                            for (f in f2.listFiles()){
                                f.delete()
                            }
                            f2.delete()
                        }
                        db.delete(item.cid)
                        list.removeAt(i)
                        mAdapter!!.notifyDataSetChanged()
                    })
                    .show()
            true
        }
        //注册广播接收器
        val filter = IntentFilter()
        filter.addAction(DownloadService.ACTION_REVIEW)
        mActivity!!.registerReceiver(mReceiver, filter)
    }

    override fun loadData() {
        list.clear()
        list.addAll(db.queryByStatus(DownloadInfo.FINISH))
        mAdapter?.notifyDataSetChanged()
        img_null?.visibility = if (list.size == 0) View.VISIBLE else View.GONE
    }

    /**
     * 更新UI的广播接收器
     */
    var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (DownloadService.ACTION_REVIEW == intent?.action) {
                loadData()
            }
        }
    }

    /**
     * 文件是否存在
     */
    fun fileIsExists(strFile: String): Boolean {
        try {
            val f = File(strFile)
            if (!f.exists()) {
                return false
            }

        } catch (e: Exception) {
            return false
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mReceiver != null) {
            mActivity!!.unregisterReceiver(mReceiver)
        }
    }


}