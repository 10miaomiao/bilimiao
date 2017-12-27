package com.a10miaomiao.bilimiao.activitys

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.SystemBarHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.gdlgxy.news.utils.FileUtil
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_photo.*

/**
 * Created by 10喵喵 on 2017/11/12.
 */
class PhotoActivity : RxAppCompatActivity() {
    val pic by lazy {
        intent.extras.getString(ConstantUtil.PIC)
    }
    val fileName by lazy{
        intent.extras.getString(ConstantUtil.NAME)
    }
    var picBitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        initViews()
        initToolBar()
    }

    fun initViews() {
        Glide.with(this)
                .load(pic)
                .asBitmap()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                        picBitmap = resource
                        siv_photo.imageBitmap = resource
                    }
                })
        siv_photo.enable()
        btn_download.setOnClickListener {
            saveImage()
        }

    }

    fun initToolBar() {
        //设置StatusBar透明
        SystemBarHelper.immersiveStatusBar(this)
    }


    /**
     * 保存图片
     */
    private fun saveImage() {
        try {
            if (picBitmap == null) {
                ("图片读取失败")
                return
            }
            var filePath = FileUtil("b站封面", this)
                    .saveJPG(picBitmap!!, fileName)
                    .fileName
            toast("图片已保存至 $filePath")
        } catch (e: Exception) {
            toast("保存失败")
        }
    }

    fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG)
                .show()
    }
    companion object {
        fun launch(context: Context, pic: String,fileName: String) {
            val mIntent = Intent(context, PhotoActivity::class.java)
            mIntent.putExtra(ConstantUtil.PIC, pic)
            mIntent.putExtra(ConstantUtil.NAME, fileName)
            context.startActivity(mIntent)
        }
    }
}