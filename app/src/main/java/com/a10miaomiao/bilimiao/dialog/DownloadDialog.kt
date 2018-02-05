package com.a10miaomiao.bilimiao.dialog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.*
import android.widget.Toast
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.activitys.DanmakuActivity
import com.a10miaomiao.bilimiao.adapter.RankOrdersAdapter
import com.a10miaomiao.bilimiao.db.DownloadDB
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.a10miaomiao.bilimiao.service.DownloadService
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import kotlinx.android.synthetic.main.dialog_download.*


/**
 * Created by 10喵喵 on 2018/1/25.
 */
class DownloadDialog : DialogFragment() {
    val cid by lazy {
        arguments.getString(ConstantUtil.CID)
    }
    val name by lazy {
        arguments.getString(ConstantUtil.NAME)
    }
    val aid by lazy {
        arguments.getString(ConstantUtil.AID)
    }
    val type by lazy {
        arguments.getString(ConstantUtil.TYPE)
    }
    val pic by lazy {
        arguments.getString(ConstantUtil.PIC)
    }
    val db: DownloadDB by lazy {
        DownloadDB(activity, DownloadDB.DB_NAME, null, 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogStyle2)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.dialog_download, container,false)
        return view
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if(db.queryByCID(cid) != null){
            tv_download.text = "已加入下载"
            tv_download.isEnabled = false
            tv_download.setTextColor(activity.resources.getColor(R.color.text_grey))
        }
        if(!isPermissions()){
            tv_download.text = "没有存储权限"
            tv_download.isEnabled = false
            tv_download.setTextColor(activity.resources.getColor(R.color.text_grey))
        }
        val mAdapter = RankOrdersAdapter(arrayOf("超清","高清","清晰","流畅"))
        mAdapter.checkItemPosition = 1
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 5)
            adapter = mAdapter
        }
        mAdapter.setOnItemClickListener { adapter, view, position ->
            mAdapter.checkItemPosition = position
        }
        tv_danmakiu.setOnClickListener {
            DanmakuActivity.launch(activity,cid)
            dismiss()
        }
        tv_download.setOnClickListener {
            //var qualitys = arrayOf(32, 64, 80, 112)
            DownloadService.add(activity, DownloadInfo(
                    cid = cid,
                    aid = aid,
                    name = name,
                    //quality = qualitys[mAdapter.checkItemPosition],
                    quality = mAdapter.checkItemPosition,
                    type = type,
                    pic = pic
            ))
            dismiss()
            Toast.makeText(activity, "成功投喂给下载姬", Toast.LENGTH_LONG).show()
        }
    }

    private fun initDialog() {
        val window = dialog.window
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels //DialogSearch的宽
        window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.BOTTOM)
    }

    override fun onStart() {
        super.onStart()
        initDialog()
    }

    //是否有存储权限
    private fun isPermissions(): Boolean {
        //判断是否6.0以上的手机 不是就不用
        if (Build.VERSION.SDK_INT >= 23) {
            //判断是否有这个权限
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    companion object {
        fun newInstance(cid: String, aid: String,name: String,type: String,pic: String): DownloadDialog {
            val args = Bundle()
            val fragment = DownloadDialog()
            args.putString(ConstantUtil.CID, cid)
            args.putString(ConstantUtil.AID, aid)
            args.putString(ConstantUtil.NAME, name)
            args.putString(ConstantUtil.TYPE, type)
            args.putString(ConstantUtil.PIC, pic)
            fragment.arguments = args
            return fragment
        }
    }

}