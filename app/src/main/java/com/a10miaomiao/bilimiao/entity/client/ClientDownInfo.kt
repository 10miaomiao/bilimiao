package com.a10miaomiao.bilimiao.entity.client

/**
 * Created by 10喵喵 on 2018/2/12.
 */
data class ClientDownInfo (
        var season_id: String,
        var episode_id: String,
        var av_id: String,
        var danmaku_id: String,
        var quality: String,
        var entry: EntryInfo,
        var index: DownIndexInfo? = null
)