package com.a10miaomiao.bilimiao.netword

import okhttp3.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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
            body: Map<String,String> = mapOf(),
            onResponse: ((response: T) -> Unit)? = null,
            onError: ((e: Throwable) -> Unit)? = null,
            parseNetworkResponse: ((response: Response) -> T)
    ) {
        val client = OkHttpClient()
        val request = Request.Builder()
        for (key in headers.keys) {
            request.addHeader(key, headers[key])
        }
        if (method == GET) {
            request.get()
                    .url(url)

        } else if (method == POST) {
            val formBody = FormBody.Builder()
            for (key in body.keys) {
                formBody.add(key, body[key])
            }
            //创建一个Request
            request.post(formBody.build())
                    .url(url)
        }
        //通过client发起请求
        Observable.create<T> {
            val response = client.newCall(request.build()).execute()
            it.onNext(parseNetworkResponse(response))
            it.onCompleted()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ response ->
                    onResponse?.invoke(response)
                },{ err ->
                    onError?.invoke(err)
                })
    }


    fun newStringClient(url: String,
                        method: Int = GET,
                        headers: Map<String,String> = mapOf(),
                        body: Map<String,String> = mapOf(),
                        onResponse: ((response: String) -> Unit)? = null,
                        onError: ((e: Throwable) -> Unit)? = null) {
        newClient<String>(
                url = url,
                method = method,
                headers = headers,
                body = body,
                onResponse = onResponse,
                onError = onError,
                parseNetworkResponse = {
                    it.body()!!.string()
                }
        )
    }


}