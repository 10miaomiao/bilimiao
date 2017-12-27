package com.a10miaomiao.bilimiao.netword

import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.Callback
import okhttp3.Call
import okhttp3.Response
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
            onResponse: ((response: T) -> Unit)? = null,
            onError: ((e: Exception) -> Unit)? = null,
            parseNetworkResponse: ((response: Response) -> T)
    ) {
        if (method == GET) {
            OkHttpUtils.get()
                    .url(url)
                    .build()
                    .execute(object : Callback<T>() {
                        override fun onResponse(response: T?, id: Int) {
                            onResponse?.invoke(response!!)
                        }

                        override fun onError(call: Call?, e: Exception, id: Int) {
                            e.printStackTrace()
                            onError?.invoke(e)
                        }

                        override fun parseNetworkResponse(response: Response?, id: Int): T {
                            return parseNetworkResponse(response!!)
                        }
                    })
        } else if (method == POST) {
            OkHttpUtils.post()
                    .url(url)
                    .build()
                    .execute(object : Callback<T>() {
                        override fun onResponse(response: T, id: Int) {
                            onResponse?.invoke(response)
                        }

                        override fun onError(call: Call, e: Exception, id: Int) {
                            onError?.invoke(e)
                        }

                        override fun parseNetworkResponse(response: Response, id: Int): T {
                            return parseNetworkResponse(response, id)
                        }
                    })
        }
    }


    fun newStringClient(url: String,
                        method: Int = GET,
                        onResponse: ((response: String) -> Unit)? = null,
                        onError: ((e: Exception) -> Unit)? = null) {
        newClient<String>(
                url = url,
                method = method,
                onResponse = onResponse,
                onError = onError,
                parseNetworkResponse = {
                    it.body().string()
                }
        )
    }


}