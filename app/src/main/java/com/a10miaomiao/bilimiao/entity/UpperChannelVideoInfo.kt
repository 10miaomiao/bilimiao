package com.a10miaomiao.bilimiao.entity

/**
 * Created by 10喵喵 on 2017/10/31.
 */
data class UpperChannelVideoInfo(
        var code: Int,
        var message: String,
        var data: UperChannelVideoData
) {
    data class UperChannelVideoData(
            var list: UperChannelVideoList
    )
    data class UperChannelVideoList(
            var archives: List<VideoArchives>
    )
    data class VideoArchives(
            var aid: Int,
            var duration: Int,
            var title: String,
            var pic: String,
            var pubdate: Long,
            var stat: VideoStat
    )

    data class VideoStat(
            var danmaku: Int,
            var view: Int
    )

}