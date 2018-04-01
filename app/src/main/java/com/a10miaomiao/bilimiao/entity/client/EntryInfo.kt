package com.a10miaomiao.bilimiao.entity.client

/**
 * Created by 10喵喵 on 2018/2/12.
 */
data class EntryInfo(
        var season_id: String,
        var avid: String? = null,
        var title: String,
        var cover: String,

        var source: SourceInfo? = null,
        var ep: EpInfo,
        var page_data: PageDataInfo? = null,

        var is_completed: Boolean,
        var total_bytes: Long = 0,
        var downloaded_bytes: Long = 0,

        var type_tag: String = "lua.flv720.bb2api.64",

        var prefered_video_quality: Int = 100,
        var guessed_total_bytes: Int = 0,

        var total_time_milli: Long = 0,
        var danmaku_count: Int = 3000,
        var time_update_stamp: Long = 1511875136369,
        var time_create_stamp: Long = 1511868244605
        ) {
//    data class SourceInfo(
//            var av_id: Long,
//            var page: String,
//            var danmaku: Long,
//            var cover: String,
//            var episode_id: Long,
//            var index: String,
//            var index_title: String
//    )

    data class SourceInfo(
            var av_id: Long,
            var cid: Long,
            var website: String = "bangumi",
            var webvideo_id: String = ""
    )
    data class EpInfo(
        var av_id: Long,
        var page: Int = 1,
        var danmaku: Long,
        var cover: String,
        var episode_id: Long,
        var index_title: String = "",
        var from: String = "bangumi"
        //var season_type: Int = 4
    )

    data class PageDataInfo(
            var cid: Long,
            var page: Int,
            var from: String = "vupload",
            var part: String,
            var link: String,
            var weblink: String,
            var rich_vid: String,
            var offsit: String,
            var vid: String,
            var has_alias: Boolean = false,
            var tid: Int
    )
}