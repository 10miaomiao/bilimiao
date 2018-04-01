package com.a10miaomiao.bilimiao.entity.client

import com.a10miaomiao.bilimiao.netword.ApiHelper

/**
 * Created by 10喵喵 on 2018/2/12.
 */
data class DownIndexInfo(
        var from: String = "bangumi",
        var type_tag: String = "lua.hdflv2.bb2api.bd",
        var description: String = "高清",
        var is_stub: Boolean = false,
        var psedo_bitrate: Int = 0,
        var segment_list: List<SegmentInfo>,
        var parse_timestamp_milli: Long = ApiHelper.getTimeSpen(),
        var available_period_milli: Long = 0,
        var local_proxy_type: Int = 0,
        var user_agent: String = "Bilibili Freedoooooom/MarkII",
        var is_downloaded: Boolean = false,
        var is_resolved: Boolean = true,
        var player_codec_config_list: List<PlayerCodecConfigInfo>,
        var time_length: Long
) {
    data class PlayerCodecConfigInfo(
            var player: String,
            var use_list_player: Boolean = false,
            var use_ijk_media_codec: Boolean = false
    )

    data class SegmentInfo(
            var url: String,
            var duration: Long = 0,
            var bytes: Long = 0,
            var meta_url: String = ""
    )

}
/**
 * public class DownIndexModel
{


}
public class player_codec_config_listModel
{

}
public class
{

}
}
 * ***/