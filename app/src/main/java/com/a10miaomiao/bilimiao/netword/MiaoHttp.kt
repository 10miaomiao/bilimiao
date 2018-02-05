package com.a10miaomiao.bilimiao.netword

import okhttp3.*
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.lang.Exception

/**
 * Created by 10喵喵 on 2017/11/14.
 */
object MiaoHttp {
    var GET = 1
    var POST = 2

    fun <T> newClient(
            url: String,
            method: Int = GET,
            headers: Map<String,String> = mapOf(),
            onResponse: ((response: T) -> Unit)? = null,
            onError: ((e: Exception) -> Unit)? = null,
            parseNetworkResponse: ((response: Response) -> T)
    ) {
        if (method == GET) {
            val client = OkHttpClient()
            //创建一个Request
            val request = Request.Builder()
                    .get()
                    .url(url)
            for (key in headers.keys) {
                request.addHeader(key, headers[key])
            }
            //通过client发起请求
            client.newCall(request.build()).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Observable.create(Observable.OnSubscribe<IOException> {it.onNext(e)})
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Subscriber<IOException>() {
                                override fun onError(e: Throwable?) {
                                    onError?.invoke(IOException())
                                }

                                override fun onNext(t: IOException) {
                                    onError?.invoke(t)
                                }

                                override fun onCompleted() {

                                }
                            })
                }

                override fun onResponse(call: Call, response: Response) {
                    Observable.create(Observable.OnSubscribe<T> {
                                if (response.isSuccessful) {
                                    it.onNext(parseNetworkResponse(response))
                                }
                                it.onCompleted()
                            }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Subscriber<T>() {
                                override fun onError(e: Throwable?) {
                                    onError?.invoke(IOException())
                                }

                                override fun onNext(t: T) {
                                    onResponse?.invoke(t)
                                }

                                override fun onCompleted() {

                                }
                            })

                }
            })
        } else if (method == POST) {
            val client = OkHttpClient()
            //创建一个Request
            val request = Request.Builder()
                    .get()
                    .url(url)
            for (key in headers.keys) {
                request.addHeader(key, headers[key])
            }
            //通过client发起请求
            client.newCall(request.build()).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    onError?.invoke(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        var data = parseNetworkResponse(response)
                        onResponse?.invoke(data)
                    }
                }
            })
        }
    }


    fun newStringClient(url: String,
                        method: Int = GET,
                        headers: Map<String,String> = mapOf(),
                        onResponse: ((response: String) -> Unit)? = null,
                        onError: ((e: Exception) -> Unit)? = null) {
        newClient<String>(
                url = url,
                method = method,
                headers = headers,
                onResponse = onResponse,
                onError = onError,
                parseNetworkResponse = {
                    it.body().string()
                }
        )
    }


}