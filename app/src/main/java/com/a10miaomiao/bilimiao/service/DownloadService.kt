package com.a10miaomiao.bilimiao.service

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.support.v7.app.NotificationCompat
import android.widget.Toast
import com.a10miaomiao.bilimiao.activitys.DownloadActivity
import com.a10miaomiao.bilimiao.db.DownloadDB
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.a10miaomiao.bilimiao.netword.ApiHelper
import com.a10miaomiao.bilimiao.netword.DownloadManager
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.FileUtil
import com.a10miaomiao.bilimiao.utils.log
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern




/**
 * Created by 10喵喵 on 2018/1/9.
 */
class DownloadService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    private var manager: NotificationManager? = null
//    private var mNotification: Notification? = null
    private var mBuilder: NotificationCompat.Builder? = null

//    var list = ArrayList<DownloadInfo>()

    var downloading: DownloadInfo? = null

    val db: DownloadDB by lazy {
        DownloadDB(this@DownloadService, DownloadDB.DB_NAME, null, 1)
    }

    var mDownloadManager = DownloadManager()

    override fun onCreate() {
        super.onCreate()
        manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mIntent = Intent(this, DownloadActivity::class.java)// 点击跳转到指定页面
        val pIntent = PendingIntent.getActivity(this, 0, mIntent, 0)
        mBuilder  = NotificationCompat.Builder(this)
        mBuilder!!.setContentTitle("准备下载")
                .setContentText("已下载0.00%")
                .setContentIntent(pIntent)
                .setSmallIcon(android.R.drawable.stat_sys_download)
        manager!!.notify(0, mBuilder!!.build())

        Observable.interval(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ aLong ->
                    if(downloading != null){
                        try {
                            val intent = Intent(DownloadService.ACTION_UPDATE)
                            intent.putExtra(ConstantUtil.DATA, downloading!!)
                            this@DownloadService.sendBroadcast(intent)
                            var progress = (downloading!!.progress * 1.0 / downloading!!.size * 100.0)
                            //progress = (progress / downloading!!.count) + (downloading!!.making * 1.0 / downloading!!.count * 100.0)
                            //log(progress.toString())
                            //log("----------------")
                            val fnum = DecimalFormat("##0.00")
                            mBuilder!!.setContentTitle(downloading!!.name)
                                    .setContentText("已下载${fnum.format(progress)}%")
                                    .setProgress(100,progress.toInt(),false)
                            manager!!.notify(0, mBuilder!!.build())
                            //downloading!!.save()
                        }catch (e: Exception){
                            e.printStackTrace()
                        }

                    }else{
                        manager?.cancel(0)
                    }
                })
        mDownloadManager.inProgress = {
            downloading?.progress = it.progress
        }
        mDownloadManager.onResponse = {
            if(downloading != null){
                downloading!!.making++
                if(downloading!!.making >= downloading!!.count){
                    downloading!!.del()
                    downloading!!.status = DownloadInfo.FINISH
                    db.upData(downloading!!)
                    nextDownload()
                }else{
                    var path = Environment.getExternalStorageDirectory().path + "/BiliMiao/"
                    FileUtil.isPath(path)
                    path = FileUtil.isPath(path + "b站视频/")
                    path = FileUtil.isPath(path + "${downloading!!.name}/")
                    mDownloadManager.create(downloading!!, File(path, downloading!!.making.toString() + downloading!!.videoType))
                }
            }
        }
        mDownloadManager.onError = {
            if(downloading != null){
                downloading!!.status = DownloadInfo.FAIL
                db.upData(downloading!!)
                nextDownload()
            }
        }
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val mBundle = intent.extras
        when(intent.action){
            ACTION_ADD -> {
                var info = mBundle.getParcelable<DownloadInfo>(ConstantUtil.DATA)
                if (downloading == null) {
                    downloading = info
                    newDownload()
                }
                db.insert(info)
            }
            ACTION_START -> {
                var cid = mBundle.getString(ConstantUtil.CID)
                if(downloading != null){
                    mDownloadManager.cancel()
                    downloading!!.status = DownloadInfo.PAUSE
                    db.upData(downloading!!)
                }
                var info = db.queryByCID(cid)
                if (info != null){
                    downloading = info
                    downloading!!.status = DownloadInfo.WAIT
                    db.upData(downloading!!)
                    newDownload()
                }
            }
            ACTION_PAUSE -> {
                mDownloadManager.cancel()
                if(downloading != null){
                    downloading!!.status = DownloadInfo.PAUSE
                    db.upData(downloading!!)
                    nextDownload()
                }else{
                    reView()
                }
            }
            ACTION_FINISHED -> {
                var cid = mBundle.getString(ConstantUtil.CID)
                db.delete(cid)
                if (cid == downloading?.cid){
                    mDownloadManager.cancel()
                    nextDownload()
                }else{
                    reView()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun playUrl(callback: () -> Unit,error: () -> Unit){
        //var query = "appkey=f3bb208b3d081dc8&cid=$cid&from=miniplay&otype=json&player=1&quality=1&type=mp4"
//        var query = ""//清晰度 32 64 80 112
//        var sing = ApiHelper.getMD5(query + "1c15888dc316e05a15fdd0a02ed6584f")
//        var url = "https://interface.bilibili.com/playurl?$query&sign=$sing"

//        var url = "http://interface.bilibili.com/playurl?_device=uwp&cid=" + cid + "&otype=xml&quality=" + 1 + "&appkey=" + ApiHelper.appKey_Android + "&access_key=&type=mp4&mid=" + "" + "&_buvid=B3CC4714-C1D3-4010-918B-8E5253E123C16133infoc&_hwid=03008c8c0300d6d1&platform=uwp_desktop" + "&ts=" + ApiHelper.GetTimeSpen()
//        url += "&sign=" + ApiHelper.GetSign_Android(url)
        var quality = arrayOf(112, 80, 64, 32)[downloading!!.quality]
        var url = "https://interface.bilibili.com/playurl?cid=${downloading!!.cid}&player=1&quality=$quality&qn=$quality&ts=${ApiHelper.getTimeSpen()}"
        url += "&sign=" + ApiHelper.getSing(url, "1c15888dc316e05a15fdd0a02ed6584f")

        MiaoHttp.newStringClient(
                url = url,
                headers = downloading!!.header!! ,
                onResponse = {
                    val pattern = """<length>(.*?)</length>.*?<size>(.*?)</size>.*?<url>.*?<!\[CDATA\[(.*?)]]></url>"""
                    //用正则式匹配文本获取匹配器
                    val matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE or Pattern.DOTALL).matcher(it)
                    if (matcher.find()) {
                        downloading!!.url = matcher.group(3)
                        downloading!!.length = matcher.group(1).toLong()
                        log("hhhhhhhhhhhhhhhhhhhh")
                        log(downloading!!.length)
                        downloading!!.size = matcher.group(2).toLong()
                        log(downloading!!.size)
                        db.upData(downloading!!)
                        callback()
                    } else {
                        error()
                    }
                },
                onError = {
                    error()
                }
        )
    }

    fun playUrl2(callback: () -> Unit,error: () -> Unit){
        var quality = arrayOf(4, 3, 2, 1)[downloading!!.quality]
        var url = "https://bangumi.bilibili.com/player/web_api/playurl/?cid=${downloading!!.cid}&module=bangumi&player=1&otype=json&type=flv&quality=$quality&ts=${ApiHelper.getTimeSpen()}"
        url += "&sign=" + ApiHelper.getSing(url, "1c15888dc316e05a15fdd0a02ed6584f")
        MiaoHttp.newStringClient(
                url = url,
                headers = downloading!!.header!! ,
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    try {
                        log(it)
                        val durls = (jsonParser.nextValue() as JSONObject).getJSONArray("durl")
                        downloading!!.url = ""
                        downloading!!.length = 0L
                        downloading!!.size = 0L
                        downloading!!.urls.clear()
                        for(i in 0 until durls.length()){
                            val durl = durls.getJSONObject(i)
                            downloading!!.urls.add(durl.getString("url"))
                            downloading!!.length += durl.getLong("length")
                            downloading!!.size += durl.getLong("size")
                        }
                        callback()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        error()
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                        error()
                    }
                },
                onError = {
                    error()
                }
        )
    }

    fun newDownload() {
        db.reData(downloading!!.cid)
        if(downloading!!.type == "anime"){
            playUrl2({
                startDownload()
            },{
                Toast.makeText(this,"下载姬被搞坏了",Toast.LENGTH_LONG).show()
                if(downloading != null){
                    downloading!!.status = DownloadInfo.FAIL
                    db.upData(downloading!!)
                    nextDownload()
                }
            })
        }else{
            playUrl({
                startDownload()
            },{
                Toast.makeText(this,"下载姬被搞坏了",Toast.LENGTH_LONG).show()
                if(downloading != null){
                    downloading!!.status = DownloadInfo.FAIL
                    db.upData(downloading!!)
                    nextDownload()
                }
            })
        }
    }

    fun getFile(info: DownloadInfo): File{
        var path = Environment.getExternalStorageDirectory().path + "/BiliMiao/"
        FileUtil.isPath(path)
        path = FileUtil.isPath(path + "b站视频/")
//            val urls = info.url!!.split(" ")
//            var url: String? = null
        var file = File(path, info.fileName)
        return file
    }

    fun startDownload(){
        var path = Environment.getExternalStorageDirectory().path + "/BiliMiao/"
        FileUtil.isPath(path)
        path = FileUtil.isPath(path + "b站视频/")

        downloading!!.videoType = videoType(downloading!!.url)
        db.upData(downloading!!)
        if (downloading!!.count > 1){
            path = FileUtil.isPath(path + "${downloading!!.name}/")
            mDownloadManager.create(downloading!!, File(path, downloading!!.making.toString() + downloading!!.videoType))
        }else{
            mDownloadManager.create(downloading!!, File(path, downloading!!.fileName))
        }
//        call = OkHttpUtils.get()
//                .tag(this)
//                .headers(downloading!!.header!!)
//                .url(url)
//                .build()
//        call!!.execute(object : FileCallBack(path, fileName)
//                {
//                    override fun inProgress(progress: Float, total: Long, id: Int) {
//                        downloading?.progress = progress
//                    }
//                    override fun onError(call: Call, e: java.lang.Exception, id: Int) {
//                        if(call.isCanceled)
//                            return
//                        if(downloading != null){
//                            downloading!!.status = DownloadInfo.FAIL
//                            db.upData(downloading!!)
//                            nextDownload()
//                        }
//                    }
//                    override fun onResponse(response: File, id: Int) {
//                        if (url == urls[urls.size - 1]){
//                            db.delete(downloading!!.cid)
//                            db2.insert(downloading!!)
//                            nextDownload()
//                        }else{
//                            startDownload()
//                        }
//                    }
//                })
    }


    fun nextDownload(){
        var list = db.queryAll()
        val info: DownloadInfo? = list.firstOrNull { it.status == DownloadInfo.WAIT }
        if (info != null){
            downloading = info
            downloading!!.status = DownloadInfo.DOWNLOADING
            db.upData(downloading!!)
            newDownload()
        }else{
            downloading = null
        }
        reView()
    }

    private fun reView(){
        val intent = Intent(DownloadService.ACTION_REVIEW)
        this@DownloadService.sendBroadcast(intent)
    }

    fun videoType(url: String): String{
        return if(".flv" in url)
            ".flv"
        else
            ".mp4"
    }

    override fun onDestroy() {
        super.onDestroy()
        if (downloading != null) {
            downloading!!.status = DownloadInfo.PAUSE
            db.upData(downloading!!)
            downloading = null
        }
    }

    companion object {
        //初始化
        private val MSG_INIT = 0
        val ACTION_ADD = "ACTION_ADD"
        //开始下载
        val ACTION_START = "ACTION_START"
        //暂停下载
        val ACTION_PAUSE = "ACTION_PAUSE"
        //结束下载
        val ACTION_FINISHED = "ACTION_FINISHED"
        //更新UI
        val ACTION_UPDATE = "ACTION_UPDATE"
        //刷新UI
        val ACTION_REVIEW = "ACTION_REVIEW"

        fun add(activity: Activity, dataBean: DownloadInfo) {
            val mIntent = Intent(activity, DownloadService::class.java)
            val mBundle = Bundle()
            mBundle.putParcelable(ConstantUtil.DATA, dataBean)
            mIntent.putExtras(mBundle)
            mIntent.action = ACTION_ADD
            activity.startService(mIntent)
        }

        fun start(activity: Activity,cid: String){
            val mIntent = Intent(activity, DownloadService::class.java)
            val mBundle = Bundle()
            mBundle.putString(ConstantUtil.CID, cid)
            mIntent.putExtras(mBundle)
            mIntent.action = ACTION_START
            activity.startService(mIntent)
        }

        fun del(activity: Activity,cid: String) {
            val mIntent = Intent(activity, DownloadService::class.java)
            val mBundle = Bundle()
            mBundle.putString(ConstantUtil.CID, cid)
            mIntent.putExtras(mBundle)
            mIntent.action = ACTION_FINISHED
            activity.startService(mIntent)
        }

        fun pause(activity: Activity) {
            val mIntent = Intent(activity, DownloadService::class.java)
            mIntent.action = ACTION_PAUSE
            activity.startService(mIntent)
        }
    }
}