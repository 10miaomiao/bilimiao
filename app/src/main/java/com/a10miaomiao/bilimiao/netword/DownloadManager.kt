package com.a10miaomiao.bilimiao.netword

import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.a10miaomiao.bilimiao.utils.log
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit




/**
 * Created by 10喵喵 on 2018/1/29.
 */
class DownloadManager {
    private var mClient: OkHttpClient? = null

    private var call : Call? = null

    var onResponse: ((info: DownloadInfo) -> Unit)? = null
    var onError: ((e: Exception) -> Unit)? = null
    var inProgress: ((info: DownloadInfo) -> Unit)? = null
    var mObservable: Subscription? = null

    init {
        mClient = OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build()
    }

    /**
     * 创建下载
     */
    fun create(downloadInfo: DownloadInfo,file: File){
        mObservable = Observable.just(downloadInfo)
                .flatMap {Observable.create(DownloadSubscribe(it,file))}
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<DownloadInfo>() {
                    override fun onCompleted() {

                    }

                    override fun onNext(t: DownloadInfo?) {
                        inProgress?.invoke(t!!)
                    }

                    override fun onError(e: Throwable?) {

                    }
                })
//        mObservable = Observable.create(DownloadSubscribe(downloadInfo))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Subscriber<DownloadInfo>() {
//                    override fun onCompleted() {
//
//                    }
//
//                    override fun onNext(t: DownloadInfo?) {
//                        inProgress?.invoke(t!!)
//                    }
//
//                    override fun onError(e: Throwable?) {
//
//                    }
//                })
    }

    /**
     * 取消下载
     */
    fun cancel(){
        call?.cancel()
        mObservable?.unsubscribe()
    }

     inner class DownloadSubscribe(var info: DownloadInfo,var file: File) : Observable.OnSubscribe<DownloadInfo> {
        override fun call(t: Subscriber<in DownloadInfo>) {
            if(file.exists()){
                info.progress = file.length()
            }
            var downloadLength = info.progress//已经下载好的长度
            var contentLength = getContentLength(info.url)
            if (contentLength > 0)
                info.size = contentLength
            else
                contentLength =info.size
            //初始进度信息
            //t.onNext(info)
            val request = Request.Builder()
                    .url(info.url)

            if(downloadLength > 0){
                request.addHeader("RANGE", "bytes=$downloadLength-$contentLength")
            }

            for (keys in info.header!!.keys){
                request.addHeader(keys,info.header!![keys])
            }

            call = mClient!!.newCall(request.build())
            //downCalls.put(url, call)//把这个添加到call里,方便取消
            val response = call!!.execute()

            log(response.isSuccessful.toString())
            log(response.code())
            var headers = response.headers()
            for(header in headers.names()){
                log(header,headers[header])
            }

            var fileOutputStream: FileOutputStream? = null
            try {
                val `is` = response.body().byteStream()
                val bis = BufferedInputStream( `is` )
                fileOutputStream = FileOutputStream(file, true)
                var buffer = ByteArray(2048)//缓冲数组2kB
                var len: Int = bis.read(buffer)
                while (len != -1) {
                    fileOutputStream!!.write(buffer, 0, len)
                    downloadLength += len
                    info.progress = downloadLength
                    //log("downloadLength",downloadLength)
                    //t.onNext(info)
                    len = bis.read(buffer)
                }
                fileOutputStream.flush()
                Observable.create(Observable.OnSubscribe<DownloadInfo> {it.onNext(info)})
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            onResponse?.invoke(it)
                        })
                //downCalls.remove(url)
            } catch (e : IOException) {
                e.printStackTrace()
                Observable.create(Observable.OnSubscribe<IOException> {it.onNext(e)})
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            onError?.invoke(it)
                        })
            }
        }
    }

    /**
     * 获取下载长度
     *
     * @param downloadUrl
     * @return
     */
    private fun getContentLength(downloadUrl: String): Long {
        val request = Request.Builder()
                .url(downloadUrl)
                .build()
        try {
            val call = mClient!!.newCall(request)
            var response = call.execute()
            if (response != null && response.isSuccessful) {
                val contentLength = response.body().contentLength()
                call.cancel()
                log("contentLength",contentLength)
                return if (contentLength == 0L) -1 else contentLength
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return -1
    }
}