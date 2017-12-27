package com.a10miaomiao.bilimiao.activitys

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.text.ClipboardManager
import android.text.Editable
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.BangumiEpisodesAdapter
import com.a10miaomiao.bilimiao.adapter.VideoPagesAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.entity.*
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.gdlgxy.news.utils.FileUtil
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.include_title_bar.*
import java.util.regex.Pattern


/**
 * Created by 10喵喵 on 2017/9/16.
 */

class InfoActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_info

    var bitmap: Bitmap? = null

    var detailsInfo: DetailsInfo? = null

    var fileName = "null"
        set(v) {
            field = v
            et_file_name.setText(v)
            var ea: Editable = et_file_name.text
            et_file_name.setSelection(0, ea.length)
        }
        get() {
            if (et_file_name.text.isNotEmpty())
                return et_file_name.text.toString()
            return "${detailsInfo?.aidType!!}${detailsInfo?.aid}"
        }

    var bangumiEpisodesAdapter: BangumiEpisodesAdapter? = null
    var videoPagesAdapter: VideoPagesAdapter? = null

    override fun initToolBar() {
        if (detailsInfo != null)
            toolbar.title = "${detailsInfo!!.aidType}${detailsInfo!!.aid}"
        else
            toolbar.title = "查看b站封面"
        toolbar.setNavigationIcon(R.mipmap.ic_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun initViews(savedInstanceState: Bundle?) {
        val mBundle = intent.extras
        //String text = intent.getExtras().getString(Intent.EXTRA_TEXT);

        if (mBundle.containsKey(Intent.EXTRA_TEXT)) {
            val text = mBundle.getString(Intent.EXTRA_TEXT)
            findAid(text)
        }

//        if(detailsInfo == null && mBundle.containsKey(ConstantUtil.DETAILS_INFO)){
//            detailsInfo = mBundle.getParcelable(ConstantUtil.DETAILS_INFO)
//        }
        if (mBundle.containsKey(ConstantUtil.AID) && mBundle.containsKey(ConstantUtil.TYPE)) {
            val aid = intent.getStringExtra(ConstantUtil.AID)
            val type = intent.getStringExtra(ConstantUtil.TYPE)
            when (type) {
                "av" -> {
                    detailsInfo = VideoDetailsInfo(aid)
                }
                "anime" -> {
                    detailsInfo = AnimeDetailsInfo(aid)
                }
                "live" -> {
                    detailsInfo = LiveDetailsInfo(aid)
                }
                "au" -> {
                    detailsInfo = AudioDetailsInfo(aid)
                }
                "cv" -> {
                    detailsInfo = CvDetailsInfo(aid)
                }
            }
        }
        if (detailsInfo != null) {
            loadData()
        } else {
            tv_title.text = "无法解析"
            et_file_name.isEnabled = false
            hideProgressBar()
            AlertDialog.Builder(activity)
                    .setTitle("无法解析该分享")
                    .setPositiveButton("确定", { dialogInterface, i ->
                        finish()
                    })
                    .show()
        }
//        tv_file_name.setOnClickListener {
//            val editDialog = EditDialog()
//            editDialog.show(supportFragmentManager, fileName)
//        }

//        if (intent.extras.get("cover") != null) {
//            showImg(intent.getStringExtra("cover"))
//        } else {
//            loadData()
//        }

        btn_saveImg.setOnClickListener {
            saveImage()
        }
        img.setOnClickListener {
            if(detailsInfo?.pic != null)
                PhotoActivity.launch(activity, detailsInfo?.pic!!, fileName)
        }
//        btn_openImg.setOnClickListener {
//            toast("就是不打开 (手动傲娇")
//        }

        //获取sd卡权限
        requestPermissions(tv_permission)
        tv_permission.setOnClickListener(this::requestPermissions)
    }

    //动态获取sd卡权限
    private fun requestPermissions(v: View) {
        //判断是否6.0以上的手机 不是就不用
        if (Build.VERSION.SDK_INT >= 23) {
            //判断是否有这个权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //2、申请权限: 参数二：权限的数组；参数三：请求码
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);
            }
        }
    }

    /**
     * 判断授权的方法  授权成功直接调用写入方法  这是监听的回调
     * 参数  上下文   授权结果的数组   申请授权的数组
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            btn_saveImg.isEnabled = false
            btn_saveImg.text = "保存封面(没有权限)"
            tv_permission.visibility = View.VISIBLE
        } else {
            btn_saveImg.isEnabled = true
            btn_saveImg.text = "保存封面"
            tv_permission.visibility = View.GONE
        }
    }

    /**
     * 寻找视频id
     * 【你的名字】【煽情对白】N2V - 寻找 UP主: 卷成卷原创电音平台 http://www.bilibili.com/video/av13840208
     * NEW GAME!!, http://bangumi.bilibili.com/anime/6330/
     * 播主：Zelo-Balance http://live.bilibili.com/live/14047.html
     *
     * 【2018年1月新番介绍】童年回忆，京紫霸权？续作第二季 http://www.bilibili.com/read/cv99107
     */
    private fun findAid(text: String) {
        var a = ""
        a = getAid(text, ".*http://www.bilibili.com/video/av(\\d+)")
        if (a != "") {
            detailsInfo = VideoDetailsInfo(a)
            return
        }
        a = getAid(text, ".*http://bangumi.bilibili.com/anime/(\\d+)/")
        if (a != "") {
            detailsInfo = AnimeDetailsInfo(a)
            return
        }
        a = getAid(text, ".*http://live.bilibili.com/live/(\\d+).html")
        if (a != "") {
            detailsInfo = LiveDetailsInfo(a)
            return
        }
        a = getAid(text, ".*http://m.bilibili.com/audio/au(\\d+)")
        if (a != "") {
            detailsInfo = AudioDetailsInfo(a)
            return
        }
        a = getAid(text, ".*http://www.bilibili.com/read/cv(\\d+)")
        if (a != "") {
            detailsInfo = CvDetailsInfo(a)
            return
        }
    }

    /**
     * 用正则获取视频id
     */
    private fun getAid(text: String, regex: String): String {
        val compile = Pattern.compile(regex)
        val matcher = compile.matcher(text)
        if (matcher.find())
            return matcher.group(1)//提取匹配到的结果
        return ""
    }

    private fun loadData() {
        showProgressBar()
        detailsInfo?.apply {
            onError = { e, msg ->
                tv_title.text = msg
                et_file_name.isEnabled = false
                hideProgressBar()
            }
            onResponse = {
                showImg(it.pic!!)
                tv_title.text = it.title!!

                //小彩蛋
                fileName = if(detailsInfo!!.aidType == "av" && detailsInfo!!.aid == "170001") "男神"
                    else "${detailsInfo?.aidType}${detailsInfo?.aid}"

                if (detailsInfo is AnimeDetailsInfo)
                    loadBangumiEpisodes()
                if (detailsInfo is VideoDetailsInfo)
                    loadUperInfo()
                if (detailsInfo is AudioDetailsInfo)
                    loadAudioInfo()
            }
            get()
        }
    }

    /**
     * 加载番剧剧集
     */
    private fun loadBangumiEpisodes() {
        card_ban_list.visibility = View.VISIBLE
        bangumiEpisodesAdapter = BangumiEpisodesAdapter(
                (detailsInfo as AnimeDetailsInfo).episodes
        )
        bangumiEpisodesAdapter?.setOnItemClickListener { adapter, view, position ->
            InfoActivity.launch(activity, (detailsInfo as AnimeDetailsInfo).episodes[position].av_id, "av")
        }
        recycle.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 2)
            adapter = bangumiEpisodesAdapter
        }
    }

    /**
     * 加载up主信息
     */
    private fun loadUperInfo() {
        card_uper.visibility = View.VISIBLE
        card_danmaku.visibility = View.VISIBLE
        tv_uper_name.text = (detailsInfo as VideoDetailsInfo).uper_name
        Glide.with(activity)
                .load((detailsInfo as VideoDetailsInfo).uper_face)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ico_user_default)
                .dontAnimate()
                .into(iv_uper_avatar)
        card_uper.setOnClickListener {
            if (detailsInfo is VideoDetailsInfo)
                UpperDetailActivity.launch(activity
                        , (detailsInfo as VideoDetailsInfo).uper_id!!
                        , (detailsInfo as VideoDetailsInfo).uper_name!!
                        , (detailsInfo as VideoDetailsInfo).uper_face!!
                )
        }
        card_uper.setOnLongClickListener {
            val items_selector = arrayOf("查看up主头像")
            AlertDialog.Builder(activity)
                    .setItems(items_selector, { dialogInterface, n ->
                        PhotoActivity.launch(activity
                                , (detailsInfo as VideoDetailsInfo).uper_face!!
                                , (detailsInfo as VideoDetailsInfo).uper_name!!)
                    })
                    .setCancelable(true)
                    .show()
            true
        }
        videoPagesAdapter = VideoPagesAdapter(
                (detailsInfo as VideoDetailsInfo).pages
        )
        videoPagesAdapter?.setOnItemClickListener { adapter, view, position ->
            DanmakuActivity.launch(activity, (detailsInfo as VideoDetailsInfo).pages[position].cid.toString())
        }
        videoPagesAdapter?.setOnItemLongClickListener { adapter, view, position ->
            val items_selector = arrayOf("查看播放地址")
            AlertDialog.Builder(activity)
                    .setItems(items_selector, { dialogInterface, n ->
                        PlayInfoActivity.launch(activity, (detailsInfo as VideoDetailsInfo).pages[position].cid.toString())
                    })
                    .setCancelable(true)
                    .show()
            true
        }
        recycle_pages.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 2)
            adapter = videoPagesAdapter
        }
    }

    /**
     * 加载音频信息
     */
    private fun loadAudioInfo() {
        card_audio.visibility = View.VISIBLE
        et_audio_url.setText((detailsInfo as AudioDetailsInfo).audio_url)
        btn_copy.setOnClickListener {
            val plaster = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            plaster.text = et_audio_url.text
            toast("已复制到剪切板(/▽＼)")
        }
    }

    /**
     * 显示图片
     */
    private fun showImg(pic: String) {
        Glide.with(this)
                .load(pic)
                .asBitmap()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                        hideProgressBar()
                        bitmap = resource
                        img.setImageBitmap(resource)
                    }
                })
    }

    /**
     * 保存图片
     */
    private fun saveImage() {
        try {
            if (bitmap == null) {
                toast("图片读取失败")
                return
            }
            var filePath = FileUtil("b站封面", activity)
                    .saveJPG(bitmap!!, fileName)
                    .fileName
            toast("图片已保存至 $filePath")
        } catch (e: Exception) {
            toast("保存失败")
        }
    }

    /**
     * 显示加载圈
     */
    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }

    /**
     * 隐藏加载圈
     */
    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    companion object {
        //        fun launch(activity: Activity, detailsInfo: DetailsInfo) {
//            val mIntent = Intent(activity, InfoActivity::class.java)
//            val mBundle = Bundle()
//            mBundle.putParcelable(ConstantUtil.DETAILS_INFO, detailsInfo)
//            mIntent.putExtras(mBundle)
//            activity.startActivity(mIntent)
//        }
        fun launch(activity: Activity, aid: String, type: String) {
            val mIntent = Intent(activity, InfoActivity::class.java)
            mIntent.putExtra(ConstantUtil.AID, aid)
            mIntent.putExtra(ConstantUtil.TYPE, type)
            activity.startActivity(mIntent)
        }
    }
}
