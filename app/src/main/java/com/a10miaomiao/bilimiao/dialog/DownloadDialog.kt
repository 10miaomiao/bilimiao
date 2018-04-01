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
import com.a10miaomiao.bilimiao.entity.DownEntryInfo
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.a10miaomiao.bilimiao.entity.client.ClientDownInfo
import com.a10miaomiao.bilimiao.entity.client.EntryInfo
import com.a10miaomiao.bilimiao.netword.ApiHelper
import com.a10miaomiao.bilimiao.service.DownloadService
import com.a10miaomiao.bilimiao.utils.BiliClientDownHelper
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import kotlinx.android.synthetic.main.dialog_download.*


/**
 * Created by 10喵喵 on 2018/1/25.
 */
class DownloadDialog : DialogFragment() {
    val info by lazy {
        arguments.getParcelable<DownEntryInfo>(ConstantUtil.DATA)
    }
    val db: DownloadDB by lazy {
        DownloadDB(activity, DownloadDB.DB_NAME, null, 1)
    }
    lateinit var mAdapter: RankOrdersAdapter

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
        if(db.queryByCID(info.cid) != null){
            tv_download.text = "已加入下载"
            tv_download.isEnabled = false
            tv_download.setTextColor(activity.resources.getColor(R.color.text_grey))
        }
        //判断是否为番剧
        if(info.video_type == "anime"){
            tv_download_app.visibility = View.VISIBLE
        }
        //判断有没有存储权限
        if(!isPermissions()){
            tv_download.text = "没有存储权限"
            tv_download.isEnabled = false
            tv_download.setTextColor(activity.resources.getColor(R.color.text_grey))
            tv_download_app.visibility = View.GONE
        }
        //选择器部分
        mAdapter = RankOrdersAdapter(arrayOf("1080P","超清","高清","清晰"))
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
            DanmakuActivity.launch(activity,info.cid)
            dismiss()
        }
        /**
         * 开始下载
         */
        tv_download.setOnClickListener {
            if(info.video_type == "anime"){
                DownloadService.add(activity, DownloadInfo(
                        cid = info.cid,
                        aid = info.ep_id!!,
                        name = info.title,
                        quality = mAdapter.checkItemPosition,
                        type = info.video_type,
                        pic = info.cover
                ))
            }else{
                DownloadService.add(activity, DownloadInfo(
                        cid = info.cid,
                        aid = info.av_id,
                        name = info.title,
                        quality = mAdapter.checkItemPosition,
                        type = info.video_type,
                        pic = info.cover
                ))
            }
            dismiss()
            Toast.makeText(activity, "成功投喂给下载姬", Toast.LENGTH_LONG).show()
        }
        /**
         * 调用app客户端下载
         */
        tv_download_app.setOnClickListener {
            try{
                var entry = createEntry()
                BiliClientDownHelper.createAnime(activity,ClientDownInfo(
                        av_id = info.av_id,
                        danmaku_id = info.cid,
                        entry = entry,
                        episode_id = info.ep_id!!,
                        quality = entry.type_tag,
                        season_id = info.season_id!!
                ))
                Toast.makeText(activity, "成功投喂给b站客户端，请重启b站客户端", Toast.LENGTH_LONG).show()
            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(activity, "投喂失败(≧﹏ ≦)", Toast.LENGTH_LONG).show()
            }
            dismiss()
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

    /**
     * 创建Entry
     */
    fun createEntry(): EntryInfo{
        var description = "超清"
        var q = 3
        var quality = "lua.flv720.bb2api.64"
        when(mAdapter.checkItemPosition){
            0 -> {
                quality = "lua.hdflv2.bb2api.bd"
                description = "1080P"
                q = 4
            }
            1 -> {
                quality = "lua.flv.bb2api.80"
                description = "超清"
                q = 3
            }
            2 -> {
                quality = "lua.flv720.bb2api.64"
                description = "高清"
                q = 2
            }
            3 -> {
                quality = "lua.mp4.bb2api.16"
                description = "清晰"
                q = 1
            }
        }
        var entry = EntryInfo(
                //avid = info.av_id,
                title = info.season_title!!,
                cover = info.season_cover!!,
                type_tag = quality,
                time_create_stamp = ApiHelper.getTimeSpen(),
                time_update_stamp = ApiHelper.getTimeSpen(),
                is_completed = false,
                season_id = info.season_id!!,
                ep = EntryInfo.EpInfo(
                        av_id = info.av_id.toLong(),
                        danmaku = info.cid.toLong(),
                        cover = info.cover,
                        episode_id = info.ep_id!!.toLong(),
                        index_title = info.title
                ),
                source = EntryInfo.SourceInfo(
                        av_id = info.av_id.toLong(),
                        cid = info.cid.toLong()
                )
        )
        return entry
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
        fun newInstance(info: DownEntryInfo): DownloadDialog {
            val args = Bundle()
            val fragment = DownloadDialog()
            args.putParcelable(ConstantUtil.DATA,info)
            fragment.arguments = args
            return fragment
        }
    }

}