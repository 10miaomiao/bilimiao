package com.a10miaomiao.bilimiao.entity

/**
 * Created by 10喵喵 on 2017/10/29.
 */
data class UpperChannelInfo(
        var code : Int,
        var message : String,
        var data : List<UperChannelData>
){
    data class UperChannelData(
            var cid : Int,
            var mid : Int,
            var name : String,
            var intro : String,
            var mtime : Long,
            var count : Int,
            var cover : String,
            var archives : List<VideoArchives>,
            var isAll: Boolean = false
    )

    data class VideoArchives(
            var aid : Int,
            var duration : Int,
            var title : String,
            var pic : String,
            var ctime : Long
    )

}