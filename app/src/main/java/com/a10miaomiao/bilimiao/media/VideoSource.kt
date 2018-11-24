package com.a10miaomiao.bilimiao.media

import android.net.Uri

data class VideoSource (
        var uri: Uri,
        var length: Long,
        var size: Long
)