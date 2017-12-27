package com.a10miaomiao.bilimiao.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.activitys.PhotoActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.layout_upper_header.view.*

/**
 * Created by 10喵喵 on 2017/11/12.
 */
class UpperHeaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, R.layout.layout_upper_header, this)
        iv_uper_avatar.setOnClickListener {
            PhotoActivity.launch(context, url, name)
        }
    }

    var url = ""
        set(value) {
            field = value
            Glide.with(context)
                    .load(value)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ico_user_default)
                    .dontAnimate()
                    .into(iv_uper_avatar)
        }

    var name = ""
        set(value) {
            field = value
            tv_uper_name.text = value
        }

}