package com.a10miaomiao.bilimiao

import android.content.Intent
import android.os.Bundle
import com.a10miaomiao.bilimiao.activitys.MainActivity
import com.trello.rxlifecycle.components.RxActivity
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


/**
 * Created by 10喵喵 on 2017/8/31.
 */
class SplashActivity: RxActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { aLong ->
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    this@SplashActivity.finish()
                }

    }
}