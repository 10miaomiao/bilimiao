package com.a10miaomiao.bilimiao.media.callback

import android.view.View

interface MediaController {
    fun setEnabled(enabled: Boolean)
    fun show()
    fun show(timeout: Int)
    fun hide()
    fun isShowing(): Boolean
    fun setMediaPlayer(player: MediaPlayerListener)
    fun setAnchorView(v: View)
    fun setTitle(title: String)
}